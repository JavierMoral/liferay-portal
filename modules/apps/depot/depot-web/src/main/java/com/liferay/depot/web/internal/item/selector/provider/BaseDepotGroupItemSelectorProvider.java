/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.item.selector.provider;

import com.liferay.depot.group.provider.SiteConnectedGroupGroupProvider;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.item.selector.provider.GroupItemSelectorProvider;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.cache.thread.local.Lifecycle;
import com.liferay.portal.kernel.cache.thread.local.ThreadLocalCache;
import com.liferay.portal.kernel.cache.thread.local.ThreadLocalCacheManager;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.security.auth.GuestOrUserUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.service.LayoutPrototypeService;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 * @author Roberto Díaz
 */
public abstract class BaseDepotGroupItemSelectorProvider
	implements GroupItemSelectorProvider {

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	@Override
	public String getEmptyResultsMessage() {
		return getEmptyResultsMessageKey();
	}

	@Override
	public String getEmptyResultsMessage(Locale locale) {
		return ResourceBundleUtil.getString(
			ResourceBundleUtil.getBundle(locale, getClass()),
			getEmptyResultsMessageKey());
	}

	@Override
	public List<Group> getGroups(
		long companyId, long groupId, String keywords, int start, int end) {

		try {
			return TransformUtil.transform(
				_filterDepotEntries(groupId, start, end),
				depotEntry -> depotEntry.getGroup());
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return Collections.emptyList();
		}
	}

	@Override
	public int getGroupsCount(long companyId, long groupId, String keywords) {
		try {
			return _getDepotEntriesCount(groupId);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return 0;
		}
	}

	@Override
	public String getIcon() {
		return "books";
	}

	@Override
	public String getLabel(Locale locale) {
		return language.get(locale, getLabelKey());
	}

	protected abstract int getDepotEntryType();

	protected abstract String getEmptyResultsMessageKey();

	protected abstract String getLabelKey();

	@Reference
	protected DepotEntryLocalService depotEntryLocalService;

	@Reference
	protected GroupService groupService;

	@Reference
	protected Language language;

	@Reference
	protected LayoutPageTemplateEntryLocalService
		layoutPageTemplateEntryLocalService;

	@Reference
	protected LayoutPrototypeService layoutPrototypeService;

	@Reference
	protected SiteConnectedGroupGroupProvider siteConnectedGroupGroupProvider;

	private List<DepotEntry> _fetchDepotEntries(
		long groupId, int start, int end) {

		try {
			return _getDepotEntries(groupId, start, end);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return Collections.emptyList();
		}
	}

	private int _fetchDepotEntriesCount(long groupId) {
		try {
			return _getDepotEntriesCount(groupId);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return 0;
		}
	}

	private List<DepotEntry> _filterDepotEntries(
			long groupId, int start, int end)
		throws PortalException {

		List<DepotEntry> depotEntries = _getDepotEntries(groupId, start, end);

		if ((end - start) <= 0) {
			return ListUtil.filter(
				depotEntries, depotEntry -> _hasViewPermission(depotEntry));
		}

		return ListUtil.filter(
			depotEntries,
			(startIndex, endIndex) -> _fetchDepotEntries(
				groupId, startIndex, endIndex),
			() -> _fetchDepotEntriesCount(groupId),
			depotEntry -> _hasViewPermission(depotEntry), start, end);
	}

	private long[] _getConnectedSiteGroupIds(long groupId)
		throws PortalException {

		Group group = _getGroup(groupId);

		long liveGroupId = group.getGroupId();

		if (group.isStagingGroup()) {
			liveGroupId = group.getLiveGroupId();
		}

		return siteConnectedGroupGroupProvider.
			getDesignLibraryConnectedSiteGroupIds(liveGroupId);
	}

	private List<DepotEntry> _getDepotEntries(long groupId, int start, int end)
		throws PortalException {

		long[] connectedSiteGroupIds = _getConnectedSiteGroupIds(groupId);

		if (ArrayUtil.isEmpty(connectedSiteGroupIds)) {
			Group group = _getGroup(groupId);

			return depotEntryLocalService.
				getCurrentAndGroupConnectedDepotEntries(
					group.getGroupId(), getDepotEntryType(), start, end);
		}

		return depotEntryLocalService.getGroupConnectedDepotEntries(
			connectedSiteGroupIds, getDepotEntryType(), start, end);
	}

	private int _getDepotEntriesCount(long groupId) throws PortalException {
		long[] connectedSiteGroupIds = _getConnectedSiteGroupIds(groupId);

		if (ArrayUtil.isEmpty(connectedSiteGroupIds)) {
			Group group = _getGroup(groupId);

			return depotEntryLocalService.
				getCurrentAndGroupConnectedDepotEntriesCount(
					group.getGroupId(), getDepotEntryType());
		}

		return depotEntryLocalService.getGroupConnectedDepotEntriesCount(
			connectedSiteGroupIds, getDepotEntryType());
	}

	private Group _getGroup(long groupId) throws PortalException {
		String key = String.valueOf(groupId);
		ThreadLocalCache<Group> threadLocalCache =
			ThreadLocalCacheManager.getThreadLocalCache(
				Lifecycle.REQUEST,
				BaseDepotGroupItemSelectorProvider.class.getName());

		Group group = threadLocalCache.get(key);

		if (group != null) {
			return group;
		}

		group = groupService.getGroup(groupId);

		if (group.isLayoutPrototype()) {
			LayoutPrototype layoutPrototype =
				layoutPrototypeService.getLayoutPrototype(group.getClassPK());

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				layoutPageTemplateEntryLocalService.
					fetchFirstLayoutPageTemplateEntry(
						layoutPrototype.getLayoutPrototypeId());

			if ((layoutPageTemplateEntry != null) &&
				(layoutPageTemplateEntry.getGroupId() > 0)) {

				group = groupService.getGroup(
					layoutPageTemplateEntry.getGroupId());
			}
		}

		threadLocalCache.put(key, group);

		return group;
	}

	private boolean _hasViewPermission(DepotEntry depotEntry) {
		try {
			Group group = depotEntry.getGroup();
			PermissionChecker permissionChecker =
				GuestOrUserUtil.getPermissionChecker();

			if (group.isCompany() ||
				permissionChecker.isGroupAdmin(group.getGroupId()) ||
				GroupPermissionUtil.contains(
					permissionChecker, group, ActionKeys.VIEW)) {

				return true;
			}

			return false;
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseDepotGroupItemSelectorProvider.class);

}