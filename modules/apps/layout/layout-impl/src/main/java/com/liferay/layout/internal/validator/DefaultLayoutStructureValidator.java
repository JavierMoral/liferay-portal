/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.validator;

import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererRegistry;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.layout.exception.LayoutStructureException;
import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.layout.util.structure.CollectionItemLayoutStructureItem;
import com.liferay.layout.util.structure.CollectionStyledLayoutStructureItem;
import com.liferay.layout.util.structure.DropZoneLayoutStructureItem;
import com.liferay.layout.util.structure.FormRelationshipStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FormStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructureItemUtil;
import com.liferay.layout.validator.LayoutStructureValidator;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Javier Moral
 */
@Component(service = LayoutStructureValidator.class)
public class DefaultLayoutStructureValidator
	implements LayoutStructureValidator {

	@Override
	public void validate(LayoutStructure layoutStructure)
		throws LayoutStructureException {

		_validate(layoutStructure);
	}

	@Override
	public void validate(LayoutStructure layoutStructure, boolean masterPage)
		throws LayoutStructureException {

		int dropZoneCount = _validate(layoutStructure);

		if (masterPage) {
			if (dropZoneCount != 1) {
				throw new LayoutStructureException(
					"A master page must contain exactly one drop zone");
			}
		}
		else if (dropZoneCount > 0) {
			throw new LayoutStructureException(
				"A drop zone can only be placed inside a master");
		}
	}

	private Set<String> _getFieldTypes(String typeOptions) {
		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject(typeOptions);

			JSONArray jsonArray = jsonObject.getJSONArray("fieldTypes");

			if (jsonArray != null) {
				return JSONUtil.toStringSet(jsonArray);
			}
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}
		}

		return Collections.emptySet();
	}

	private FragmentEntry _getFragmentEntry(
		FragmentEntryLink fragmentEntryLink, Locale locale) {

		FragmentEntry fragmentEntry = fragmentEntryLink.fetchFragmentEntry();

		if (fragmentEntry != null) {
			return fragmentEntry;
		}

		fragmentEntry = _fragmentEntryLocalService.fetchFragmentEntry(
			fragmentEntryLink.getGroupId(), fragmentEntryLink.getRendererKey());

		if (fragmentEntry != null) {
			return fragmentEntry;
		}

		Map<String, FragmentEntry> fragmentEntries =
			_fragmentCollectionContributorRegistry.getFragmentEntries(locale);

		return fragmentEntries.get(fragmentEntryLink.getRendererKey());
	}

	private Set<String> _getFragmentEntryLinkFieldTypes(
		FragmentEntryLink fragmentEntryLink) {

		FragmentRenderer fragmentRenderer =
			_fragmentRendererRegistry.getFragmentRenderer(
				fragmentEntryLink.getRendererKey());

		if (fragmentRenderer != null) {
			return _getFieldTypes(fragmentRenderer.getTypeOptions());
		}

		FragmentEntry fragmentEntry = _getFragmentEntry(
			fragmentEntryLink, LocaleUtil.getMostRelevantLocale());

		if (fragmentEntry != null) {
			return _getFieldTypes(fragmentEntry.getTypeOptions());
		}

		return Collections.emptySet();
	}

	private boolean _hasFragmentDescendant(
		LayoutStructure layoutStructure,
		LayoutStructureItem layoutStructureItem) {

		for (String childItemId : layoutStructureItem.getChildrenItemIds()) {
			LayoutStructureItem childLayoutStructureItem =
				layoutStructure.getLayoutStructureItem(childItemId);

			if (childLayoutStructureItem == null) {
				continue;
			}

			if ((childLayoutStructureItem instanceof
					FragmentStyledLayoutStructureItem) ||
				_hasFragmentDescendant(
					layoutStructure, childLayoutStructureItem)) {

				return true;
			}
		}

		return false;
	}

	private boolean _isChildItemTypeAllowed(
		String parentItemType, String childItemType) {

		if (Objects.equals(
				parentItemType, LayoutDataItemTypeConstants.TYPE_ROW)) {

			return Objects.equals(
				childItemType, LayoutDataItemTypeConstants.TYPE_COLUMN);
		}

		if (Objects.equals(
				parentItemType,
				LayoutDataItemTypeConstants.TYPE_FORM_STEP_CONTAINER)) {

			return Objects.equals(
				childItemType, LayoutDataItemTypeConstants.TYPE_FORM_STEP);
		}

		return true;
	}

	private int _validate(LayoutStructure layoutStructure)
		throws LayoutStructureException {

		if (layoutStructure == null) {
			return 0;
		}

		LayoutStructureItem mainLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(
				layoutStructure.getMainItemId());

		if (mainLayoutStructureItem == null) {
			return 0;
		}

		return _validate(
			layoutStructure, mainLayoutStructureItem, new HashSet<>());
	}

	private int _validate(
			LayoutStructure layoutStructure,
			LayoutStructureItem layoutStructureItem,
			Set<Long> fragmentEntryLinkIds)
		throws LayoutStructureException {

		_validateContentStructureItem(layoutStructure, layoutStructureItem);

		int dropZoneCount = 0;

		if (layoutStructureItem instanceof DropZoneLayoutStructureItem) {
			dropZoneCount++;
		}

		if (layoutStructureItem instanceof FragmentStyledLayoutStructureItem) {
			FragmentStyledLayoutStructureItem
				fragmentStyledLayoutStructureItem =
					(FragmentStyledLayoutStructureItem)layoutStructureItem;

			long fragmentEntryLinkId =
				fragmentStyledLayoutStructureItem.getFragmentEntryLinkId();

			if ((fragmentEntryLinkId > 0) &&
				!fragmentEntryLinkIds.add(fragmentEntryLinkId)) {

				throw new LayoutStructureException(
					"A fragment instance can only be referenced by one page " +
						"element");
			}
		}

		String parentItemType = layoutStructureItem.getItemType();

		int columnCount = 0;

		for (String childItemId : layoutStructureItem.getChildrenItemIds()) {
			LayoutStructureItem childLayoutStructureItem =
				layoutStructure.getLayoutStructureItem(childItemId);

			if (childLayoutStructureItem == null) {
				continue;
			}

			String childItemType = childLayoutStructureItem.getItemType();

			if (!_isChildItemTypeAllowed(parentItemType, childItemType)) {
				throw new LayoutStructureException(
					StringBundler.concat(
						"A ", parentItemType, " cannot contain a ",
						childItemType));
			}

			if (Objects.equals(
					childItemType, LayoutDataItemTypeConstants.TYPE_COLUMN)) {

				columnCount++;
			}

			dropZoneCount += _validate(
				layoutStructure, childLayoutStructureItem,
				fragmentEntryLinkIds);
		}

		if (Objects.equals(
				parentItemType, LayoutDataItemTypeConstants.TYPE_ROW) &&
			!_validColumnCounts.contains(columnCount)) {

			throw new LayoutStructureException(
				"A row can have 1, 2, 3, 4, 5, 6, or 12 columns");
		}

		return dropZoneCount;
	}

	private void _validateCollection(
			LayoutStructure layoutStructure,
			CollectionStyledLayoutStructureItem
				collectionStyledLayoutStructureItem)
		throws LayoutStructureException {

		JSONObject collectionJSONObject =
			collectionStyledLayoutStructureItem.getCollectionJSONObject();

		if ((collectionJSONObject == null) ||
			(collectionJSONObject.length() == 0)) {

			if (_hasFragmentDescendant(
					layoutStructure, collectionStyledLayoutStructureItem)) {

				throw new LayoutStructureException(
					"A fragment cannot be placed inside an unmapped " +
						"collection display");
			}

			return;
		}

		int collectionItemCount = 0;

		for (String childItemId :
				collectionStyledLayoutStructureItem.getChildrenItemIds()) {

			LayoutStructureItem childLayoutStructureItem =
				layoutStructure.getLayoutStructureItem(childItemId);

			if (!(childLayoutStructureItem instanceof
					CollectionItemLayoutStructureItem)) {

				continue;
			}

			if ((collectionItemCount > 0) &&
				_hasFragmentDescendant(
					layoutStructure, childLayoutStructureItem)) {

				throw new LayoutStructureException(
					"Fragments can only be placed in the first collection " +
						"item");
			}

			collectionItemCount++;
		}
	}

	private void _validateContentStructureItem(
			LayoutStructure layoutStructure,
			LayoutStructureItem layoutStructureItem)
		throws LayoutStructureException {

		if (layoutStructureItem instanceof
				CollectionStyledLayoutStructureItem) {

			_validateCollection(
				layoutStructure,
				(CollectionStyledLayoutStructureItem)layoutStructureItem);
		}
		else if (layoutStructureItem instanceof
					FormRelationshipStyledLayoutStructureItem) {

			LayoutStructureItem formLayoutStructureItem =
				LayoutStructureItemUtil.getAncestor(
					layoutStructureItem.getItemId(),
					LayoutDataItemTypeConstants.TYPE_FORM, layoutStructure);

			if (formLayoutStructureItem == null) {
				throw new LayoutStructureException(
					"A form relationship can only be placed inside a form " +
						"container");
			}
		}
		else if (layoutStructureItem instanceof FormStyledLayoutStructureItem) {
			_validateForm(
				layoutStructure,
				(FormStyledLayoutStructureItem)layoutStructureItem);
		}
		else if (layoutStructureItem instanceof
					FragmentStyledLayoutStructureItem) {

			_validateFragment(
				layoutStructure,
				(FragmentStyledLayoutStructureItem)layoutStructureItem);
		}
	}

	private void _validateForm(
			LayoutStructure layoutStructure,
			FormStyledLayoutStructureItem formStyledLayoutStructureItem)
		throws LayoutStructureException {

		if (formStyledLayoutStructureItem.getClassNameId() <= 0) {
			if (_hasFragmentDescendant(
					layoutStructure, formStyledLayoutStructureItem)) {

				throw new LayoutStructureException(
					"A fragment cannot be placed inside an unmapped form " +
						"container");
			}

			return;
		}

		int stepperCount = 0;

		for (String childItemId :
				formStyledLayoutStructureItem.getChildrenItemIds()) {

			LayoutStructureItem childLayoutStructureItem =
				layoutStructure.getLayoutStructureItem(childItemId);

			if (!(childLayoutStructureItem instanceof
					FragmentStyledLayoutStructureItem)) {

				continue;
			}

			FragmentStyledLayoutStructureItem
				fragmentStyledLayoutStructureItem =
					(FragmentStyledLayoutStructureItem)childLayoutStructureItem;

			FragmentEntryLink fragmentEntryLink =
				_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
					fragmentStyledLayoutStructureItem.getFragmentEntryLinkId());

			if (fragmentEntryLink == null) {
				continue;
			}

			Set<String> fieldTypes = _getFragmentEntryLinkFieldTypes(
				fragmentEntryLink);

			if (fieldTypes.contains("stepper")) {
				stepperCount++;
			}
		}

		if (stepperCount > 1) {
			throw new LayoutStructureException(
				"A form can only contain one stepper");
		}
	}

	private void _validateFragment(
			LayoutStructure layoutStructure,
			FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem)
		throws LayoutStructureException {

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentStyledLayoutStructureItem.getFragmentEntryLinkId());

		if (fragmentEntryLink == null) {
			return;
		}

		Set<String> fieldTypes = _getFragmentEntryLinkFieldTypes(
			fragmentEntryLink);

		String itemId = fragmentStyledLayoutStructureItem.getItemId();

		if (fieldTypes.contains("stepper")) {
			LayoutStructureItem parentLayoutStructureItem =
				layoutStructure.getLayoutStructureItem(
					fragmentStyledLayoutStructureItem.getParentItemId());

			if (!(parentLayoutStructureItem instanceof
					FormStyledLayoutStructureItem)) {

				throw new LayoutStructureException(
					"A stepper can only be placed inside a form container");
			}
		}
		else if (fragmentEntryLink.isTypePortlet()) {
			LayoutStructureItem formLayoutStructureItem =
				LayoutStructureItemUtil.getAncestor(
					itemId, LayoutDataItemTypeConstants.TYPE_FORM,
					layoutStructure);

			if (formLayoutStructureItem != null) {
				throw new LayoutStructureException(
					"A widget cannot be placed inside a form container");
			}

			LayoutStructureItem collectionLayoutStructureItem =
				LayoutStructureItemUtil.getAncestor(
					itemId, LayoutDataItemTypeConstants.TYPE_COLLECTION,
					layoutStructure);

			if (collectionLayoutStructureItem != null) {
				JSONObject editableValuesJSONObject =
					fragmentEntryLink.getEditableValuesJSONObject();

				Portlet portlet = _portletLocalService.getPortletById(
					fragmentEntryLink.getCompanyId(),
					editableValuesJSONObject.getString("portletId"));

				if ((portlet != null) && !portlet.isInstanceable()) {
					throw new LayoutStructureException(
						"A noninstanceable widget cannot be placed inside a " +
							"collection display");
				}
			}
		}

		if (fragmentEntryLink.isTypeInput() &&
			!fieldTypes.contains("localizationSelect")) {

			LayoutStructureItem formLayoutStructureItem =
				LayoutStructureItemUtil.getAncestor(
					itemId, LayoutDataItemTypeConstants.TYPE_FORM,
					layoutStructure);

			boolean mappedForm = false;

			if (formLayoutStructureItem instanceof
					FormStyledLayoutStructureItem) {

				FormStyledLayoutStructureItem formStyledLayoutStructureItem =
					(FormStyledLayoutStructureItem)formLayoutStructureItem;

				if (formStyledLayoutStructureItem.getClassNameId() > 0) {
					mappedForm = true;
				}
			}

			if (!mappedForm) {
				throw new LayoutStructureException(
					"This form component can only be placed inside a mapped " +
						"form container");
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultLayoutStructureValidator.class);

	private static final Set<Integer> _validColumnCounts = new HashSet<>(
		Arrays.asList(1, 2, 3, 4, 5, 6, 12));

	@Reference
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Reference
	private FragmentRendererRegistry _fragmentRendererRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private PortletLocalService _portletLocalService;

}