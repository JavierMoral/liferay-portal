/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.design.library;

import com.liferay.depot.model.DepotEntry;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.layout.page.template.constants.LayoutPageTemplateActionKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Javier Moral
 */
public class DisplayPageTemplateDesignLibraryResourceTypeContributorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		Mockito.when(
			_depotEntry.getGroupId()
		).thenReturn(
			_GROUP_ID
		);

		ReflectionTestUtil.setFieldValue(
			_displayPageTemplateDesignLibraryResourceTypeContributor,
			"_portletResourcePermission", _portletResourcePermission);
	}

	@Test
	public void testGetEntryClassName() {
		Assert.assertEquals(
			LayoutPageTemplateEntry.class.getName(),
			_displayPageTemplateDesignLibraryResourceTypeContributor.
				getEntryClassName());
	}

	@Test
	public void testGetFDSActionDropdownItems() throws Exception {
		List<FDSActionDropdownItem> fdsActionDropdownItems =
			_displayPageTemplateDesignLibraryResourceTypeContributor.
				getFDSActionDropdownItems(
					Mockito.mock(HttpServletRequest.class), _depotEntry,
					RandomTestUtil.randomString());

		Assert.assertTrue(
			fdsActionDropdownItems.toString(),
			fdsActionDropdownItems.isEmpty());
	}

	@Test
	public void testGetType() {

		// Masters, content page templates and widget page templates share the
		// entry class name, so the type is what tells a display page template
		// apart.

		Assert.assertEquals(
			String.valueOf(LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE),
			_displayPageTemplateDesignLibraryResourceTypeContributor.getType());
	}

	@Test
	public void testHasAddPermission() {
		Assert.assertFalse(
			_displayPageTemplateDesignLibraryResourceTypeContributor.
				hasAddPermission(_permissionChecker, _depotEntry));

		_setUpPermission(
			LayoutPageTemplateActionKeys.ADD_LAYOUT_PAGE_TEMPLATE_ENTRY);

		Assert.assertTrue(
			_displayPageTemplateDesignLibraryResourceTypeContributor.
				hasAddPermission(_permissionChecker, _depotEntry));
	}

	@Test
	public void testHasViewPermission() {
		Assert.assertFalse(
			_displayPageTemplateDesignLibraryResourceTypeContributor.
				hasViewPermission(_permissionChecker, _depotEntry));

		_setUpPermission(ActionKeys.VIEW);

		Assert.assertTrue(
			_displayPageTemplateDesignLibraryResourceTypeContributor.
				hasViewPermission(_permissionChecker, _depotEntry));
	}

	private void _setUpPermission(String actionId) {
		Mockito.when(
			_portletResourcePermission.contains(
				_permissionChecker, _GROUP_ID, actionId)
		).thenReturn(
			true
		);
	}

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private final DepotEntry _depotEntry = Mockito.mock(DepotEntry.class);
	private final DisplayPageTemplateDesignLibraryResourceTypeContributor
		_displayPageTemplateDesignLibraryResourceTypeContributor =
			new DisplayPageTemplateDesignLibraryResourceTypeContributor();
	private final PermissionChecker _permissionChecker = Mockito.mock(
		PermissionChecker.class);
	private final PortletResourcePermission _portletResourcePermission =
		Mockito.mock(PortletResourcePermission.class);

}