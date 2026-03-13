/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useEffect, useMemo, useState} from 'react';

import {FREEMARKER_FRAGMENT_ENTRY_PROCESSOR} from '../../config/constants/freemarkerFragmentEntryProcessor';
import {useSelectorCallback} from '../../contexts/StoreContext';
import {useFilterableCollections} from '../../hooks/useFilterableCollections';
import CollectionService from '../../services/CollectionService';
import {deepEqual} from '../../utils/checkDeepEqual';
import isEmptyArray from '../../utils/isEmptyArray';
import {SelectField} from './SelectField';
import {selectConfiguredCollectionDisplays} from './TargetCollectionsField';

export function FilterSelectorField({field, item, onValueSelect, value}) {
	const targetCollections = useSelectorCallback(
		(state) => {
			const fragmentEntryLink =
				state.fragmentEntryLinks[item.config?.fragmentEntryLinkId];

			return (
				fragmentEntryLink?.editableValues[
					FREEMARKER_FRAGMENT_ENTRY_PROCESSOR
				]?.targetCollections ?? []
			);
		},
		[item.config?.fragmentEntryLinkId],
		deepEqual
	);

	const collections = useSelectorCallback(
		selectConfiguredCollectionDisplays,
		[],
		deepEqual
	);

	const {filterableCollections} = useFilterableCollections(collections);

	const [collectionFilters, setCollectionFilters] = useState(null);

	useEffect(() => {
		let mounted = true;

		CollectionService.getCollectionFilters()
			.then((filters) => {
				if (mounted) {
					setCollectionFilters(filters);
				}
			})
			.catch((error) => {
				if (mounted) {
					setCollectionFilters({});
				}

				if (process.env.NODE_ENV === 'development') {
					console.error(error);
				}
			});

		return () => {
			mounted = false;
		};
	}, []);

	const supportedFilters = useMemo(() => {
		if (!collectionFilters || !filterableCollections) {
			return [];
		}

		return _filterSupportedFilters({
			collectionFilters: Object.values(collectionFilters),
			filterableCollections,
			targetCollections,
		});
	}, [collectionFilters, filterableCollections, targetCollections]);

	useEffect(() => {
		if (collectionFilters === null || filterableCollections === null) {
			return;
		}

		if (
			value &&
			(isEmptyArray(targetCollections) ||
				!supportedFilters.some((filter) => filter.key === value))
		) {
			onValueSelect(field.name, '');
		}
	}, [
		collectionFilters,
		field.name,
		filterableCollections,
		onValueSelect,
		supportedFilters,
		targetCollections,
		value,
	]);

	if (isEmptyArray(targetCollections)) {
		return null;
	}

	const dynamicField = {
		...field,
		typeOptions: {
			...field.typeOptions,
			validValues: [
				{
					label: Liferay.Language.get('none'),
					value: '',
				},
				...supportedFilters.map(({key, label}) => ({
					label,
					value: key,
				})),
			],
		},
	};

	return (
		<SelectField
			field={dynamicField}
			onValueSelect={onValueSelect}
			value={value}
		/>
	);
}

function _filterSupportedFilters({
	collectionFilters,
	filterableCollections,
	targetCollections = [],
}) {
	if (isEmptyArray(targetCollections)) {
		return [];
	}

	const targetCollectionsSupportedFilters = targetCollections
		.map(
			(targetCollection) =>
				filterableCollections[targetCollection]?.supportedFilters
		)
		.filter(Boolean);

	if (isEmptyArray(targetCollectionsSupportedFilters)) {
		return [];
	}

	return collectionFilters.filter(({key}) =>
		targetCollectionsSupportedFilters.every(
			(targetCollectionSupportedFilters) =>
				targetCollectionSupportedFilters.includes(key)
		)
	);
}
