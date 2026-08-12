/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.design.library;

import com.liferay.depot.model.DepotEntry;
import com.liferay.layout.page.template.constants.LayoutPageTemplateActionKeys;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
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

	@Test
	public void testGetEntryClassName() {
		Assert.assertEquals(
			LayoutPageTemplateEntry.class.getName(),
			_createContributor(
				Mockito.mock(PortletResourcePermission.class)
			).getEntryClassName());
	}

	@Test
	public void testGetType() {

		// The guard in LayoutPageTemplateEntryLocalServiceImpl only admits
		// display page templates in a Design Library, so every entry listed
		// from a library group is one and the entry class name identifies the
		// type on its own

		Assert.assertNull(
			_createContributor(
				Mockito.mock(PortletResourcePermission.class)
			).getType());
	}

	@Test
	public void testHasAddPermission() {
		_testHasPermission(false);
		_testHasPermission(true);
	}

	private DisplayPageTemplateDesignLibraryResourceTypeContributor
		_createContributor(
			PortletResourcePermission portletResourcePermission) {

		DisplayPageTemplateDesignLibraryResourceTypeContributor
			displayPageTemplateDesignLibraryResourceTypeContributor =
				new DisplayPageTemplateDesignLibraryResourceTypeContributor();

		ReflectionTestUtil.setFieldValue(
			displayPageTemplateDesignLibraryResourceTypeContributor,
			"_portletResourcePermission", portletResourcePermission);

		return displayPageTemplateDesignLibraryResourceTypeContributor;
	}

	private void _testHasPermission(boolean contains) {
		long groupId = RandomTestUtil.randomLong();

		DepotEntry depotEntry = Mockito.mock(DepotEntry.class);

		Mockito.when(
			depotEntry.getGroupId()
		).thenReturn(
			groupId
		);

		PermissionChecker permissionChecker = Mockito.mock(
			PermissionChecker.class);

		PortletResourcePermission portletResourcePermission = Mockito.mock(
			PortletResourcePermission.class);

		Mockito.when(
			portletResourcePermission.contains(
				permissionChecker, groupId,
				LayoutPageTemplateActionKeys.ADD_LAYOUT_PAGE_TEMPLATE_ENTRY)
		).thenReturn(
			contains
		);

		DisplayPageTemplateDesignLibraryResourceTypeContributor
			displayPageTemplateDesignLibraryResourceTypeContributor =
				_createContributor(portletResourcePermission);

		Assert.assertEquals(
			contains,
			displayPageTemplateDesignLibraryResourceTypeContributor.
				hasAddPermission(permissionChecker, depotEntry));
		Assert.assertEquals(
			contains,
			displayPageTemplateDesignLibraryResourceTypeContributor.
				hasViewPermission(permissionChecker, depotEntry));
	}

}