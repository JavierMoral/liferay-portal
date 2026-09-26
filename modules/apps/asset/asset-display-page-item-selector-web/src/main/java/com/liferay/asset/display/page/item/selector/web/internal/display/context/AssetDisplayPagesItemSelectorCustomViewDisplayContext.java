/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.display.page.item.selector.web.internal.display.context;

import com.liferay.asset.display.page.item.selector.AssetDisplayPageItemSelectorCriterion;
import com.liferay.design.library.util.DesignLibraryUtil;
import com.liferay.item.selector.criteria.AssetEntryItemSelectorReturnType;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalServiceUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryServiceUtil;
import com.liferay.layout.page.template.util.comparator.LayoutPageTemplateCollectionLayoutPageTemplateEntryCreateDateComparator;
import com.liferay.layout.page.template.util.comparator.LayoutPageTemplateCollectionLayoutPageTemplateEntryNameComparator;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbEntryBuilder;
import com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbEntryListBuilder;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

/**
 * @author Yurena Cabrera
 */
public class AssetDisplayPagesItemSelectorCustomViewDisplayContext {

	public AssetDisplayPagesItemSelectorCustomViewDisplayContext(
		HttpServletRequest httpServletRequest, String itemSelectedEventName,
		AssetDisplayPageItemSelectorCriterion
			assetDisplayPageItemSelectorCriterion,
		PortletURL portletURL) {

		_httpServletRequest = httpServletRequest;
		_itemSelectedEventName = itemSelectedEventName;
		_assetDisplayPageItemSelectorCriterion =
			assetDisplayPageItemSelectorCriterion;
		_portletURL = portletURL;

		_portletRequest = (PortletRequest)httpServletRequest.getAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST);
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public SearchContainer<?> getAssetDisplayPageSearchContainer() {
		if (_assetDisplayPageSearchContainer != null) {
			return _assetDisplayPageSearchContainer;
		}

		SearchContainer<Object> assetDisplayPageSearchContainer =
			new SearchContainer<>(
				_portletRequest, _portletURL, null,
				"there-are-no-display-page-templates");

		assetDisplayPageSearchContainer.setId(
			"displayPages" + getLayoutPageTemplateCollectionId());
		assetDisplayPageSearchContainer.setOrderByCol(_getOrderByCol());
		assetDisplayPageSearchContainer.setOrderByComparator(
			_getLayoutPageTemplateEntryOrderByComparator(
				_getOrderByCol(), getOrderByType()));
		assetDisplayPageSearchContainer.setOrderByType(getOrderByType());
		assetDisplayPageSearchContainer.setResultsAndTotal(
			() ->
				LayoutPageTemplateEntryServiceUtil.
					getLayoutPageCollectionsAndLayoutPageTemplateEntries(
						_getGroupId(), getLayoutPageTemplateCollectionId(),
						_assetDisplayPageItemSelectorCriterion.getClassNameId(),
						_assetDisplayPageItemSelectorCriterion.getClassTypeId(),
						_getKeywords(),
						LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
						WorkflowConstants.STATUS_APPROVED,
						assetDisplayPageSearchContainer.getStart(),
						assetDisplayPageSearchContainer.getEnd(),
						assetDisplayPageSearchContainer.getOrderByComparator()),
			LayoutPageTemplateEntryServiceUtil.
				getLayoutPageCollectionsAndLayoutPageTemplateEntriesCount(
					_getGroupId(), getLayoutPageTemplateCollectionId(),
					_assetDisplayPageItemSelectorCriterion.getClassNameId(),
					_assetDisplayPageItemSelectorCriterion.getClassTypeId(),
					_getKeywords(),
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
					WorkflowConstants.STATUS_APPROVED));

		_assetDisplayPageSearchContainer = assetDisplayPageSearchContainer;

		return _assetDisplayPageSearchContainer;
	}

	public List<BreadcrumbEntry> getBreadcrumbEntries() {
		LayoutPageTemplateCollection layoutPageTemplateCollection =
			LayoutPageTemplateCollectionLocalServiceUtil.
				fetchLayoutPageTemplateCollection(
					getLayoutPageTemplateCollectionId());

		return BreadcrumbEntryListBuilder.add(
			() -> !_isShowGroupSelector(),
			breadcrumbEntry -> {
				breadcrumbEntry.setTitle(
					LanguageUtil.get(_httpServletRequest, "home"));
				breadcrumbEntry.setURL(_getRootCollectionURL());
			}
		).add(
			this::_isShowGroupSelector,
			breadcrumbEntry -> {
				breadcrumbEntry.setTitle(
					LanguageUtil.get(
						_httpServletRequest, "sites-and-libraries"));
				breadcrumbEntry.setURL(_getGroupSelectorURL(StringPool.BLANK));
			}
		).add(
			this::_isShowGroupTypeBreadcrumbEntry,
			breadcrumbEntry -> {
				breadcrumbEntry.setTitle(_getGroupTypeLabel());
				breadcrumbEntry.setURL(_getGroupSelectorURL(_getGroupType()));
			}
		).add(
			this::_isShowGroupSelector,
			breadcrumbEntry -> {
				breadcrumbEntry.setTitle(_getGroupDescriptiveName());
				breadcrumbEntry.setURL(_getRootCollectionURL());
			}
		).addAll(
			() -> layoutPageTemplateCollection != null,
			() -> {
				List<LayoutPageTemplateCollection>
					layoutPageTemplateCollections =
						layoutPageTemplateCollection.getAncestors();

				Collections.reverse(layoutPageTemplateCollections);

				return TransformUtil.transform(
					layoutPageTemplateCollections,
					curLayoutPageTemplateCollection ->
						BreadcrumbEntryBuilder.setTitle(
							curLayoutPageTemplateCollection.getName()
						).setURL(
							PortletURLBuilder.create(
								_portletURL
							).setParameter(
								"layoutPageTemplateCollectionId",
								curLayoutPageTemplateCollection.
									getLayoutPageTemplateCollectionId()
							).buildString()
						).build());
			}
		).build();
	}

	public String getItemSelectedEventName() {
		return _itemSelectedEventName;
	}

	public long getLayoutPageTemplateCollectionId() {
		if (_layoutPageTemplateCollectionId != null) {
			return _layoutPageTemplateCollectionId;
		}

		_layoutPageTemplateCollectionId = ParamUtil.getLong(
			_httpServletRequest, "layoutPageTemplateCollectionId",
			LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT);

		return _layoutPageTemplateCollectionId;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = ParamUtil.getString(
			_httpServletRequest, "orderByType", "asc");

		return _orderByType;
	}

	public String getPayload(LayoutPageTemplateEntry layoutPageTemplateEntry) {
		return JSONUtil.put(
			"id", layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
		).put(
			"name", layoutPageTemplateEntry.getName()
		).put(
			"plid", layoutPageTemplateEntry.getPlid()
		).put(
			"type", "asset-display-page"
		).put(
			"uuid", layoutPageTemplateEntry.getUuid()
		).toString();
	}

	public PortletURL getPortletURL() {
		return _portletURL;
	}

	public String getReturnType() {
		return AssetEntryItemSelectorReturnType.class.getName();
	}

	private Group _getGroup() {
		return GroupLocalServiceUtil.fetchGroup(_getGroupId());
	}

	private String _getGroupDescriptiveName() {
		Group group = _getGroup();

		if (group == null) {
			return StringPool.BLANK;
		}

		try {
			return group.getDescriptiveName(_themeDisplay.getLocale());
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return group.getName(_themeDisplay.getLocale());
		}
	}

	private long _getGroupId() {
		if (_groupId != null) {
			return _groupId;
		}

		_groupId = ParamUtil.getLong(
			_httpServletRequest, "groupId", _themeDisplay.getScopeGroupId());

		return _groupId;
	}

	private String _getGroupSelectorURL(String groupType) {
		return PortletURLBuilder.create(
			_portletURL
		).setParameter(
			"groupType", groupType
		).setParameter(
			"groupTypes", "site,design-library"
		).setParameter(
			"showGroupSelector", true
		).setParameter(
			"showGroupTypeSelector", true
		).buildString();
	}

	private String _getGroupType() {
		String groupType = ParamUtil.getString(
			_httpServletRequest, "groupType");

		if (Validator.isNotNull(groupType)) {
			return groupType;
		}

		if (_isDesignLibraryScope()) {
			return "design-library";
		}

		Group group = _getGroup();

		if ((group != null) && group.isDepot()) {
			return StringPool.BLANK;
		}

		return "site";
	}

	private String _getGroupTypeLabel() {
		String groupType = _getGroupType();

		if (groupType.equals("design-library")) {
			return LanguageUtil.get(_httpServletRequest, "design-libraries");
		}

		return LanguageUtil.get(_httpServletRequest, "site");
	}

	private String _getKeywords() {
		if (Validator.isNotNull(_keywords)) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		return _keywords;
	}

	private OrderByComparator<Object>
		_getLayoutPageTemplateEntryOrderByComparator(
			String orderByCol, String orderByType) {

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		OrderByComparator<Object> orderByComparator = null;

		if (orderByCol.equals("create-date")) {
			orderByComparator =
				LayoutPageTemplateCollectionLayoutPageTemplateEntryCreateDateComparator.
					getInstance(orderByAsc);
		}
		else if (orderByCol.equals("name")) {
			orderByComparator =
				LayoutPageTemplateCollectionLayoutPageTemplateEntryNameComparator.
					getInstance(orderByAsc);
		}

		return orderByComparator;
	}

	private String _getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = ParamUtil.getString(
			_httpServletRequest, "orderByCol", "create-date");

		return _orderByCol;
	}

	private String _getRootCollectionURL() {
		return PortletURLBuilder.create(
			_portletURL
		).setParameter(
			"layoutPageTemplateCollectionId",
			LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT
		).buildString();
	}

	private boolean _isDesignLibraryScope() {
		if (_designLibraryScope != null) {
			return _designLibraryScope;
		}

		_designLibraryScope = DesignLibraryUtil.isDesignLibraryScope(
			_getGroupId());

		return _designLibraryScope;
	}

	private boolean _isShowGroupSelector() {
		return FeatureFlagManagerUtil.isEnabled(
			_themeDisplay.getCompanyId(), "LPD-57283");
	}

	private boolean _isShowGroupTypeBreadcrumbEntry() {
		if (!_isShowGroupSelector()) {
			return false;
		}

		return Validator.isNotNull(_getGroupType());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetDisplayPagesItemSelectorCustomViewDisplayContext.class);

	private final AssetDisplayPageItemSelectorCriterion
		_assetDisplayPageItemSelectorCriterion;
	private SearchContainer<?> _assetDisplayPageSearchContainer;
	private Boolean _designLibraryScope;
	private Long _groupId;
	private final HttpServletRequest _httpServletRequest;
	private final String _itemSelectedEventName;
	private String _keywords;
	private Long _layoutPageTemplateCollectionId;
	private String _orderByCol;
	private String _orderByType;
	private final PortletRequest _portletRequest;
	private final PortletURL _portletURL;
	private final ThemeDisplay _themeDisplay;

}