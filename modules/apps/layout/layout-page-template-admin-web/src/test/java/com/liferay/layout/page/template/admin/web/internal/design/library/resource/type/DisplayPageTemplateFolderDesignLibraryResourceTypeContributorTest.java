/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.design.library.resource.type;

import com.liferay.depot.model.DepotEntry;
import com.liferay.design.library.resource.type.DesignLibraryResourceCreationItem;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateActionKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Javier Moral
 */
public class DisplayPageTemplateFolderDesignLibraryResourceTypeContributorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_displayPageTemplateFolderDesignLibraryResourceTypeContributor,
			"_portletResourcePermission", _portletResourcePermission);

		Mockito.when(
			_depotEntry.getGroup()
		).thenReturn(
			_group
		);

		Mockito.when(
			_depotEntry.getGroupId()
		).thenReturn(
			_GROUP_ID
		);

		Mockito.when(
			_addLiferayPortletURL.toString()
		).thenReturn(
			_ADD_URL
		);

		Mockito.when(
			_editLiferayPortletURL.toString()
		).thenReturn(
			_EDIT_URL
		);

		Mockito.when(
			_permissionsLiferayPortletURL.toString()
		).thenReturn(
			_PERMISSIONS_URL
		);

		Mockito.when(
			_viewLiferayPortletURL.toString()
		).thenReturn(
			_VIEW_URL
		);

		_languageUtilMockedStatic.when(
			() -> LanguageUtil.get(
				Mockito.any(HttpServletRequest.class), Mockito.anyString())
		).thenAnswer(
			invocation -> invocation.getArgument(1)
		);

		_portalUtilMockedStatic.when(
			() -> PortalUtil.getControlPanelPortletURL(
				Mockito.eq(_httpServletRequest), Mockito.eq(_group),
				Mockito.eq(
					LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES),
				Mockito.anyLong(), Mockito.anyLong(),
				Mockito.eq(PortletRequest.ACTION_PHASE))
		).thenReturn(
			_addLiferayPortletURL
		);

		_portalUtilMockedStatic.when(
			() -> PortalUtil.getControlPanelPortletURL(
				Mockito.eq(_httpServletRequest), Mockito.eq(_group),
				Mockito.eq(
					LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES),
				Mockito.anyLong(), Mockito.anyLong(),
				Mockito.eq(PortletRequest.RENDER_PHASE))
		).thenReturn(
			_viewLiferayPortletURL, _editLiferayPortletURL,
			_permissionsLiferayPortletURL
		);

		_portalUtilMockedStatic.when(
			() -> PortalUtil.getPortletNamespace(
				LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES)
		).thenReturn(
			_NAMESPACE
		);
	}

	@After
	public void tearDown() {
		_languageUtilMockedStatic.close();
		_portalUtilMockedStatic.close();
	}

	@Test
	@TestInfo("LPD-108336")
	public void testGetCreationItems() throws Exception {
		List<DesignLibraryResourceCreationItem>
			designLibraryResourceCreationItems =
				_displayPageTemplateFolderDesignLibraryResourceTypeContributor.
					getCreationItems(
						_httpServletRequest, _depotEntry, _BACK_URL);

		Assert.assertEquals(
			designLibraryResourceCreationItems.toString(), 1,
			designLibraryResourceCreationItems.size());

		DesignLibraryResourceCreationItem designLibraryResourceCreationItem =
			designLibraryResourceCreationItems.get(0);

		Assert.assertEquals(
			"add-display-page-template-folder",
			designLibraryResourceCreationItem.getId());
		Assert.assertEquals(
			"new-display-page-template-folder",
			designLibraryResourceCreationItem.getLabel());
		Assert.assertEquals(
			"{AddDisplayPageTemplateFolderDesignLibraryModalContent} from " +
				"layout-page-template-admin-web",
			designLibraryResourceCreationItem.getModule());

		Map<String, Object> moduleProps =
			designLibraryResourceCreationItem.getModuleProps();

		Assert.assertEquals(
			_ADD_URL, moduleProps.get("addDisplayPageTemplateFolderURL"));
		Assert.assertEquals(_NAMESPACE, moduleProps.get("namespace"));

		Mockito.verify(
			_addLiferayPortletURL
		).setParameter(
			ActionRequest.ACTION_NAME,
			"/layout_page_template_admin/add_display_page_collection"
		);

		Mockito.verify(
			_addLiferayPortletURL
		).setParameter(
			"redirect", _BACK_URL
		);
	}

	@Test
	@TestInfo("LPD-108336")
	public void testGetEntryClassName() {
		Assert.assertEquals(
			LayoutPageTemplateCollection.class.getName(),
			_displayPageTemplateFolderDesignLibraryResourceTypeContributor.
				getEntryClassName());
	}

	@Test
	@TestInfo("LPD-108336")
	public void testGetFDSActionDropdownItems() throws Exception {
		List<FDSActionDropdownItem> fdsActionDropdownItems =
			_displayPageTemplateFolderDesignLibraryResourceTypeContributor.
				getFDSActionDropdownItems(
					_httpServletRequest, _depotEntry, _BACK_URL);

		Assert.assertEquals(
			fdsActionDropdownItems.toString(), 4,
			fdsActionDropdownItems.size());

		_assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(0), _VIEW_URL, "view", "view", "view",
			null, null, "link");
		_assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(1), _EDIT_URL, "pencil", "edit", "edit",
			null, null, "link");
		_assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(2), _PERMISSIONS_URL,
			"password-policies", "permissions", "permissions", null,
			"permissions", "modal-permissions");
		_assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(3), "{actions.delete.href}", "trash",
			"delete", "delete", "delete", "delete", "async");

		Mockito.verify(
			_viewLiferayPortletURL
		).setParameter(
			"backURL", _BACK_URL
		);

		Mockito.verify(
			_viewLiferayPortletURL
		).setParameter(
			"tabs1", "display-page-templates"
		);

		Mockito.verify(
			_viewLiferayPortletURL
		).setParameter(
			"layoutPageTemplateCollectionExternalReferenceCode",
			"{embedded.externalReferenceCode}"
		);

		Mockito.verify(
			_editLiferayPortletURL
		).setParameter(
			"mvcRenderCommandName",
			"/layout_page_template_admin/edit_layout_page_template_collection"
		);

		Mockito.verify(
			_editLiferayPortletURL
		).setParameter(
			"redirect", _BACK_URL
		);

		Mockito.verify(
			_editLiferayPortletURL
		).setParameter(
			"layoutPageTemplateCollectionExternalReferenceCode",
			"{embedded.externalReferenceCode}"
		);

		Mockito.verify(
			_permissionsLiferayPortletURL
		).setParameter(
			"mvcRenderCommandName",
			"/layout_page_template_admin" +
				"/view_layout_page_template_collection_permissions"
		);

		Mockito.verify(
			_permissionsLiferayPortletURL
		).setParameter(
			"layoutPageTemplateCollectionExternalReferenceCode",
			"{embedded.externalReferenceCode}"
		);
	}

	@Test
	@TestInfo("LPD-108336")
	public void testGetType() {
		Assert.assertEquals(
			String.valueOf(
				LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE),
			_displayPageTemplateFolderDesignLibraryResourceTypeContributor.
				getType());
	}

	@Test
	@TestInfo("LPD-108336")
	public void testHasAddPermission() {
		Assert.assertFalse(
			_displayPageTemplateFolderDesignLibraryResourceTypeContributor.
				hasAddPermission(_permissionChecker, _depotEntry));

		_setUpAddLayoutPageTemplateCollectionPermission();

		Assert.assertTrue(
			_displayPageTemplateFolderDesignLibraryResourceTypeContributor.
				hasAddPermission(_permissionChecker, _depotEntry));
	}

	@Test
	@TestInfo("LPD-108336")
	public void testHasViewPermission() {
		Assert.assertFalse(
			_displayPageTemplateFolderDesignLibraryResourceTypeContributor.
				hasViewPermission(_permissionChecker, _depotEntry));

		_setUpAddLayoutPageTemplateCollectionPermission();

		Assert.assertTrue(
			_displayPageTemplateFolderDesignLibraryResourceTypeContributor.
				hasViewPermission(_permissionChecker, _depotEntry));
	}

	private void _assertFDSActionDropdownItem(
		FDSActionDropdownItem fdsActionDropdownItem, String href, String icon,
		String id, String label, String method, String permissionKey,
		String target) {

		Assert.assertEquals(href, fdsActionDropdownItem.get("href"));
		Assert.assertEquals(icon, fdsActionDropdownItem.get("icon"));
		Assert.assertEquals(label, fdsActionDropdownItem.get("label"));
		Assert.assertEquals(target, fdsActionDropdownItem.get("target"));

		Map<String, Object> data =
			(Map<String, Object>)fdsActionDropdownItem.get("data");

		Assert.assertEquals(id, data.get("id"));
		Assert.assertEquals(method, data.get("method"));
		Assert.assertEquals(permissionKey, data.get("permissionKey"));
	}

	private void _setUpAddLayoutPageTemplateCollectionPermission() {
		Mockito.when(
			_portletResourcePermission.contains(
				_permissionChecker, _GROUP_ID,
				LayoutPageTemplateActionKeys.
					ADD_LAYOUT_PAGE_TEMPLATE_COLLECTION)
		).thenReturn(
			true
		);
	}

	private static final String _ADD_URL = RandomTestUtil.randomString();

	private static final String _BACK_URL = RandomTestUtil.randomString();

	private static final String _EDIT_URL = RandomTestUtil.randomString();

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private static final String _NAMESPACE = RandomTestUtil.randomString();

	private static final String _PERMISSIONS_URL =
		RandomTestUtil.randomString();

	private static final String _VIEW_URL = RandomTestUtil.randomString();

	private final LiferayPortletURL _addLiferayPortletURL = Mockito.mock(
		LiferayPortletURL.class);
	private final DepotEntry _depotEntry = Mockito.mock(DepotEntry.class);
	private final DisplayPageTemplateFolderDesignLibraryResourceTypeContributor
		_displayPageTemplateFolderDesignLibraryResourceTypeContributor =
			new DisplayPageTemplateFolderDesignLibraryResourceTypeContributor();
	private final LiferayPortletURL _editLiferayPortletURL = Mockito.mock(
		LiferayPortletURL.class);
	private final Group _group = Mockito.mock(Group.class);
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final MockedStatic<LanguageUtil> _languageUtilMockedStatic =
		Mockito.mockStatic(LanguageUtil.class);
	private final PermissionChecker _permissionChecker = Mockito.mock(
		PermissionChecker.class);
	private final LiferayPortletURL _permissionsLiferayPortletURL =
		Mockito.mock(LiferayPortletURL.class);
	private final MockedStatic<PortalUtil> _portalUtilMockedStatic =
		Mockito.mockStatic(PortalUtil.class);
	private final PortletResourcePermission _portletResourcePermission =
		Mockito.mock(PortletResourcePermission.class);
	private final LiferayPortletURL _viewLiferayPortletURL = Mockito.mock(
		LiferayPortletURL.class);

}