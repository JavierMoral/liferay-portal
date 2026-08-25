/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0.util;

import com.liferay.headless.admin.site.internal.resource.v1_0.DisplayPageTemplateResourceImpl;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.util.ActionUtil;

import jakarta.ws.rs.core.UriInfo;

import java.util.Map;

/**
 * @author Javier Moral
 */
public class DisplayPageTemplateActionUtil {

	public static Map<String, Map<String, String>> getDesignLibraryActions(
		Object contextScopeChecker, String designLibraryExternalReferenceCode,
		LayoutPageTemplateEntry layoutPageTemplateEntry,
		ModelResourcePermission<LayoutPageTemplateEntry>
			modelResourcePermission,
		UriInfo uriInfo) {

		Map<String, String> templateParameterMap = HashMapBuilder.put(
			"designLibraryExternalReferenceCode",
			designLibraryExternalReferenceCode
		).put(
			"displayPageTemplateExternalReferenceCode",
			layoutPageTemplateEntry.getExternalReferenceCode()
		).put(

			// The permissions of a display page template are read through the
			// site scoped resource, which resolves its group by external
			// reference code without requiring the group to be a site. A
			// design library is a group, so its own code resolves there.

			"siteExternalReferenceCode", designLibraryExternalReferenceCode
		).build();

		return HashMapBuilder.<String, Map<String, String>>put(
			"delete",
			_addAction(
				ActionKeys.DELETE, contextScopeChecker,
				"deleteDesignLibraryDisplayPageTemplate",
				layoutPageTemplateEntry, modelResourcePermission,
				templateParameterMap, uriInfo)
		).put(
			"get",
			_addAction(
				ActionKeys.VIEW, contextScopeChecker,
				"getDesignLibraryDisplayPageTemplate", layoutPageTemplateEntry,
				modelResourcePermission, templateParameterMap, uriInfo)
		).put(
			"permissions",
			_addAction(
				ActionKeys.PERMISSIONS, contextScopeChecker,
				"getSiteDisplayPageTemplatePermissionsPage",
				layoutPageTemplateEntry, modelResourcePermission,
				templateParameterMap, uriInfo)
		).put(
			() -> {
				if (_isMarkableAsDefault(layoutPageTemplateEntry)) {
					return "markAsDefault";
				}

				return null;
			},
			_addAction(
				ActionKeys.UPDATE, contextScopeChecker,
				"postDesignLibraryDisplayPageTemplateMarkAsDefault",
				layoutPageTemplateEntry, modelResourcePermission,
				templateParameterMap, uriInfo)
		).put(
			() -> {
				if (layoutPageTemplateEntry.isDefaultTemplate()) {
					return "unmarkAsDefault";
				}

				return null;
			},
			_addAction(
				ActionKeys.UPDATE, contextScopeChecker,
				"postDesignLibraryDisplayPageTemplateUnmarkAsDefault",
				layoutPageTemplateEntry, modelResourcePermission,
				templateParameterMap, uriInfo)
		).build();
	}

	private static Map<String, String> _addAction(
		String actionName, Object contextScopeChecker, String methodName,
		LayoutPageTemplateEntry layoutPageTemplateEntry,
		ModelResourcePermission<LayoutPageTemplateEntry>
			modelResourcePermission,
		Map<String, String> templateParameterMap, UriInfo uriInfo) {

		return ActionUtil.addAction(
			actionName, DisplayPageTemplateResourceImpl.class,
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(), methodName,
			contextScopeChecker, modelResourcePermission, templateParameterMap,
			uriInfo);
	}

	private static boolean _isMarkableAsDefault(
		LayoutPageTemplateEntry layoutPageTemplateEntry) {

		if (!layoutPageTemplateEntry.isApproved() ||
			layoutPageTemplateEntry.isDefaultTemplate() ||
			Validator.isNull(layoutPageTemplateEntry.getClassName())) {

			return false;
		}

		return true;
	}

}