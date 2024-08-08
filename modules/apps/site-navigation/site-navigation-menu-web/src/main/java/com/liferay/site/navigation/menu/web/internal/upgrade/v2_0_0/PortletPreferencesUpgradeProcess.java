/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.menu.web.internal.upgrade.v2_0_0;

import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.upgrade.BasePortletPreferencesUpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.site.navigation.constants.SiteNavigationMenuPortletKeys;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalService;

import javax.portlet.PortletPreferences;

/**
 * @author Javier Moral
 */
public class PortletPreferencesUpgradeProcess
	extends BasePortletPreferencesUpgradeProcess {

	public PortletPreferencesUpgradeProcess(
		SiteNavigationMenuLocalService siteNavigationMenuLocalService,
		SiteNavigationMenuItemLocalService siteNavigationMenuItemLocalService) {

		_siteNavigationMenuLocalService = siteNavigationMenuLocalService;
		_siteNavigationMenuItemLocalService =
			siteNavigationMenuItemLocalService;
	}

	@Override
	protected String[] getPortletIds() {
		return new String[] {
			SiteNavigationMenuPortletKeys.SITE_NAVIGATION_MENU + "%"
		};
	}

	@Override
	protected String upgradePreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId, String xml)
		throws Exception {

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.fromXML(
				companyId, ownerId, ownerType, plid, portletId, xml);

		portletPreferences = upgradePreferences(portletPreferences);

		return PortletPreferencesFactoryUtil.toXML(portletPreferences);
	}

	protected PortletPreferences upgradePreferences(
			PortletPreferences portletPreferences)
		throws Exception {

		long siteNavigationMenuId = GetterUtil.getLong(
			portletPreferences.getValue("siteNavigationMenuId", "0"));

		if (siteNavigationMenuId > 0) {
			SiteNavigationMenu siteNavigationMenu =
				_siteNavigationMenuLocalService.fetchSiteNavigationMenu(
					siteNavigationMenuId);

			if (siteNavigationMenu != null) {
				portletPreferences.setValue(
					"siteNavigationMenuExternalReferenceCode",
					siteNavigationMenu.getExternalReferenceCode());
				portletPreferences.reset("siteNavigationMenuId");
			}
		}

		long rootMenuItemId = GetterUtil.getLong(
			portletPreferences.getValue("rootMenuItemId", "0"));

		if (rootMenuItemId > 0) {
			SiteNavigationMenuItem siteNavigationMenuItem =
				_siteNavigationMenuItemLocalService.fetchSiteNavigationMenuItem(
					rootMenuItemId);

			if (siteNavigationMenuItem != null) {
				portletPreferences.setValue(
					"rootMenuItemExternalReferenceCode",
					siteNavigationMenuItem.getExternalReferenceCode());
				portletPreferences.reset("rootMenuItemId");
			}
		}

		return portletPreferences;
	}

	private final SiteNavigationMenuItemLocalService
		_siteNavigationMenuItemLocalService;
	private final SiteNavigationMenuLocalService
		_siteNavigationMenuLocalService;

}