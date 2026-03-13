/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import PropTypes from 'prop-types';
import React, {useCallback} from 'react';

import {COMMON_STYLES_ROLES} from '../../../../../../app/config/constants/commonStylesRoles';
import {FREEMARKER_FRAGMENT_ENTRY_PROCESSOR} from '../../../../../../app/config/constants/freemarkerFragmentEntryProcessor';
import {
	useDispatch,
	useSelector,
	useSelectorCallback,
} from '../../../../../../app/contexts/StoreContext';
import selectLanguageId from '../../../../../../app/selectors/selectLanguageId';
import {getResponsiveConfig} from '../../../../../../app/utils/getResponsiveConfig';
import updateConfigurationValue from '../../../../../../app/utils/updateConfigurationValue';
import getLayoutDataItemPropTypes from '../../../../../../prop_types/getLayoutDataItemPropTypes';
import {CommonStyles} from './CommonStyles';
import {FieldSet} from './FieldSet';

export function CollectionAppliedFiltersGeneralPanel({item}) {
	const dispatch = useDispatch();
	const fragmentEntryLink = useSelectorCallback(
		(state) => state.fragmentEntryLinks[item.config.fragmentEntryLinkId],
		[item.config.fragmentEntryLinkId]
	);

	const languageId = useSelector(selectLanguageId);

	const selectedViewportSize = useSelector(
		(state) => state.selectedViewportSize
	);

	const itemConfig = getResponsiveConfig(item.config, selectedViewportSize);

	const configurationValues =
		fragmentEntryLink.editableValues[FREEMARKER_FRAGMENT_ENTRY_PROCESSOR] ||
		{};

	const onValueSelect = useCallback(
		(name, value) => {
			updateConfigurationValue({
				dispatch,
				fragmentEntryLink,
				languageId,
				name,
				value,
			});
		},
		[dispatch, fragmentEntryLink, languageId]
	);

	const fieldSets = fragmentEntryLink.configuration?.fieldSets ?? [];

	return (
		<>
			<p aria-live="polite" className="alert alert-info text-center">
				{Liferay.Language.get(
					'you-will-see-this-fragment-on-the-page-only-after-applying-a-filter'
				)}
			</p>

			{fieldSets.map((fieldSet, index) => (
				<div className="mb-3 panel-group-sm" key={index}>
					<FieldSet
						fields={fieldSet.fields}
						item={item}
						label={fieldSet.label}
						languageId={languageId}
						onValueSelect={onValueSelect}
						values={configurationValues}
					/>
				</div>
			))}

			<CommonStyles
				commonStylesValues={itemConfig.styles}
				item={item}
				role={COMMON_STYLES_ROLES.general}
			/>
		</>
	);
}

CollectionAppliedFiltersGeneralPanel.propTypes = {
	item: getLayoutDataItemPropTypes({
		config: PropTypes.shape({
			fragmentEntryLinkId: PropTypes.string.isRequired,
		}).isRequired,
	}),
};
