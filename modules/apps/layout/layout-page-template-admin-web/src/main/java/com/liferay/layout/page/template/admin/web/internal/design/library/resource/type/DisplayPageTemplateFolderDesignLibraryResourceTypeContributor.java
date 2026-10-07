/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.design.library.resource.type;

import com.liferay.depot.model.DepotEntry;
import com.liferay.design.library.resource.type.DesignLibraryResourceCreationItem;
import com.liferay.design.library.resource.type.DesignLibraryResourceTypeContributor;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateActionKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Javier Moral
 */
@Component(
	property = "service.ranking:Integer=75",
	service = DesignLibraryResourceTypeContributor.class
)
public class DisplayPageTemplateFolderDesignLibraryResourceTypeContributor
	implements DesignLibraryResourceTypeContributor {

	@Override
	public String getColor() {
		return "secondary";
	}

	@Override
	public List<DesignLibraryResourceCreationItem> getCreationItems(
			HttpServletRequest httpServletRequest, DepotEntry depotEntry,
			String backURL)
		throws PortalException {

		return Collections.singletonList(
			new DesignLibraryResourceCreationItem(
				"add-display-page-template-folder",
				LanguageUtil.get(
					httpServletRequest, "new-display-page-template-folder"),
				"{AddDisplayPageTemplateFolderDesignLibraryModalContent} " +
					"from layout-page-template-admin-web",
				HashMapBuilder.<String, Object>put(
					"addDisplayPageTemplateFolderURL",
					PortletURLBuilder.create(
						PortalUtil.getControlPanelPortletURL(
							httpServletRequest, depotEntry.getGroup(),
							LayoutPageTemplateAdminPortletKeys.
								LAYOUT_PAGE_TEMPLATES,
							0, 0, PortletRequest.ACTION_PHASE)
					).setActionName(
						"/layout_page_template_admin" +
							"/add_display_page_collection"
					).setRedirect(
						backURL
					).buildString()
				).put(
					"namespace",
					PortalUtil.getPortletNamespace(
						LayoutPageTemplateAdminPortletKeys.
							LAYOUT_PAGE_TEMPLATES)
				).build()));
	}

	@Override
	public String getDefaultActionId() {
		return "view";
	}

	@Override
	public String getEntryClassName() {
		return LayoutPageTemplateCollection.class.getName();
	}

	@Override
	public List<FDSActionDropdownItem> getFDSActionDropdownItems(
			HttpServletRequest httpServletRequest, DepotEntry depotEntry,
			String backURL)
		throws PortalException {

		Group depotGroup = depotEntry.getGroup();

		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					PortalUtil.getControlPanelPortletURL(
						httpServletRequest, depotGroup,
						LayoutPageTemplateAdminPortletKeys.
							LAYOUT_PAGE_TEMPLATES,
						0, 0, PortletRequest.RENDER_PHASE)
				).setBackURL(
					backURL
				).setTabs1(
					"display-page-templates"
				).setParameter(
					"layoutPageTemplateCollectionExternalReferenceCode",
					"{embedded.externalReferenceCode}"
				).buildString(),
				"view", "view", LanguageUtil.get(httpServletRequest, "view"),
				null, null, "link"),
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					PortalUtil.getControlPanelPortletURL(
						httpServletRequest, depotGroup,
						LayoutPageTemplateAdminPortletKeys.
							LAYOUT_PAGE_TEMPLATES,
						0, 0, PortletRequest.RENDER_PHASE)
				).setMVCRenderCommandName(
					"/layout_page_template_admin" +
						"/edit_layout_page_template_collection"
				).setRedirect(
					backURL
				).setParameter(
					"layoutPageTemplateCollectionExternalReferenceCode",
					"{embedded.externalReferenceCode}"
				).buildString(),
				"pencil", "edit", LanguageUtil.get(httpServletRequest, "edit"),
				null, null, "link"),
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					PortalUtil.getControlPanelPortletURL(
						httpServletRequest, depotGroup,
						LayoutPageTemplateAdminPortletKeys.
							LAYOUT_PAGE_TEMPLATES,
						0, 0, PortletRequest.RENDER_PHASE)
				).setMVCRenderCommandName(
					"/layout_page_template_admin" +
						"/view_layout_page_template_collection_permissions"
				).setParameter(
					"layoutPageTemplateCollectionExternalReferenceCode",
					"{embedded.externalReferenceCode}"
				).buildString(),
				"password-policies", "permissions",
				LanguageUtil.get(httpServletRequest, "permissions"), null,
				"permissions", "modal-permissions"),
			new FDSActionDropdownItem(
				"{actions.delete.href}", "trash", "delete",
				LanguageUtil.get(httpServletRequest, "delete"), "delete",
				"delete", "async"));
	}

	@Override
	public String getIcon() {
		return "folder";
	}

	@Override
	public String getKey() {
		return "display-page-template-folder";
	}

	@Override
	public String getLabel(Locale locale) {
		return LanguageUtil.get(locale, "display-page-template-folder");
	}

	@Override
	public String getType() {
		return String.valueOf(
			LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE);
	}

	@Override
	public boolean hasAddPermission(
		PermissionChecker permissionChecker, DepotEntry depotEntry) {

		return _portletResourcePermission.contains(
			permissionChecker, depotEntry.getGroupId(),
			LayoutPageTemplateActionKeys.ADD_LAYOUT_PAGE_TEMPLATE_COLLECTION);
	}

	@Override
	public boolean hasViewPermission(
		PermissionChecker permissionChecker, DepotEntry depotEntry) {

		return hasAddPermission(permissionChecker, depotEntry);
	}

	@Reference(
		target = "(resource.name=" + LayoutPageTemplateConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}