/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useEffect, useState} from 'react';

import CollectionService from '../services/CollectionService';
import isEmptyArray from '../utils/isEmptyArray';

/**
 * Callers must ensure `collections` has a stable reference (e.g. via
 * useSelectorCallback with deepEqual) to avoid re-fetching on every render.
 */
export function useFilterableCollections(collections) {
	const [filterableCollections, setFilterableCollections] = useState(null);
	const [loading, setLoading] = useState(false);

	useEffect(() => {
		if (isEmptyArray(collections)) {
			setFilterableCollections({});
			setLoading(false);

			return;
		}

		let mounted = true;

		setLoading(true);

		CollectionService.getCollectionSupportedFilters(
			collections.map((item) => ({
				collectionId: item.itemId,
				layoutObjectReference: item.config?.collection,
			}))
		)
			.then((response) => {
				if (!mounted) {
					return;
				}

				const nextFilterableCollections = {};

				collections
					.filter(
						(collection) =>
							!isEmptyArray(response[collection.itemId])
					)
					.forEach((collection) => {
						nextFilterableCollections[collection.itemId] = {
							...collection,
							supportedFilters: response[collection.itemId],
						};
					});

				setFilterableCollections(nextFilterableCollections);
			})
			.catch((error) => {
				if (mounted) {
					setFilterableCollections({});
				}

				if (process.env.NODE_ENV === 'development') {
					console.error(error);
				}
			})
			.finally(() => {
				if (mounted) {
					setLoading(false);
				}
			});

		return () => {
			mounted = false;
		};
	}, [collections]);

	return {filterableCollections, loading};
}
