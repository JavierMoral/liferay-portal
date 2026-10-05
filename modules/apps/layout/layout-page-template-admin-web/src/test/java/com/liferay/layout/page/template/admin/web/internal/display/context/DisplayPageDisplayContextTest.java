/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.display.context;

import com.liferay.design.library.util.DesignLibraryUtil;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.layout.page.template.admin.web.internal.util.LayoutPageTemplatePortletUtil;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

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
public class DisplayPageDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_setUpThemeDisplay();
	}

	@After
	public void tearDown() {
		_designLibraryUtilMockedStatic.close();
		_layoutPageTemplatePortletUtilMockedStatic.close();
	}

	@Test
	@TestInfo("LPD-108335")
	public void testGetLayoutPageTemplateCollectionId() {
		_testGetLayoutPageTemplateCollectionIdFromCollection();
		_testGetLayoutPageTemplateCollectionIdFromRequest();
		_testGetLayoutPageTemplateCollectionIdWithoutCollection();
	}

	@Test
	@TestInfo("LPD-108335")
	public void testIsShowBreadcrumb() {
		_testIsShowBreadcrumb(false);
		_testIsShowBreadcrumb(true);
	}

	private DisplayPageDisplayContext _getDisplayPageDisplayContext() {
		return new DisplayPageDisplayContext(
			_httpServletRequest, _infoItemServiceRegistry,
			_liferayPortletRequest, _liferayPortletResponse);
	}

	private long _getLayoutPageTemplateCollectionId() {
		DisplayPageDisplayContext displayPageDisplayContext =
			_getDisplayPageDisplayContext();

		return displayPageDisplayContext.getLayoutPageTemplateCollectionId();
	}

	private void _setUpDesignLibraryScope(boolean designLibraryScope) {
		_designLibraryUtilMockedStatic.when(
			() -> DesignLibraryUtil.isDesignLibraryScope(_group)
		).thenReturn(
			designLibraryScope
		);
	}

	private void _setUpRequest(
		LayoutPageTemplateCollection layoutPageTemplateCollection,
		String layoutPageTemplateCollectionId) {

		_layoutPageTemplatePortletUtilMockedStatic.when(
			() ->
				LayoutPageTemplatePortletUtil.fetchLayoutPageTemplateCollection(
					_httpServletRequest, _GROUP_ID)
		).thenReturn(
			layoutPageTemplateCollection
		);

		Mockito.when(
			_httpServletRequest.getParameter("layoutPageTemplateCollectionId")
		).thenReturn(
			layoutPageTemplateCollectionId
		);
	}

	private void _setUpThemeDisplay() {
		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			_httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		Mockito.when(
			themeDisplay.getScopeGroup()
		).thenReturn(
			_group
		);

		Mockito.when(
			themeDisplay.getScopeGroupId()
		).thenReturn(
			_GROUP_ID
		);
	}

	private void _testGetLayoutPageTemplateCollectionIdFromCollection() {
		LayoutPageTemplateCollection layoutPageTemplateCollection =
			Mockito.mock(LayoutPageTemplateCollection.class);
		long layoutPageTemplateCollectionId = RandomTestUtil.randomLong();

		Mockito.when(
			layoutPageTemplateCollection.getLayoutPageTemplateCollectionId()
		).thenReturn(
			layoutPageTemplateCollectionId
		);

		_setUpRequest(layoutPageTemplateCollection, null);

		Assert.assertEquals(
			layoutPageTemplateCollectionId,
			_getLayoutPageTemplateCollectionId());
	}

	private void _testGetLayoutPageTemplateCollectionIdFromRequest() {
		long layoutPageTemplateCollectionId = RandomTestUtil.randomLong();

		_setUpRequest(null, String.valueOf(layoutPageTemplateCollectionId));

		Assert.assertEquals(
			layoutPageTemplateCollectionId,
			_getLayoutPageTemplateCollectionId());
	}

	private void _testGetLayoutPageTemplateCollectionIdWithoutCollection() {
		_setUpRequest(null, null);

		Assert.assertEquals(
			LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
			_getLayoutPageTemplateCollectionId());
	}

	private void _testIsShowBreadcrumb(boolean designLibraryScope) {
		_setUpDesignLibraryScope(designLibraryScope);

		DisplayPageDisplayContext displayPageDisplayContext =
			_getDisplayPageDisplayContext();

		Assert.assertEquals(
			!designLibraryScope, displayPageDisplayContext.isShowBreadcrumb());
	}

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private final MockedStatic<DesignLibraryUtil>
		_designLibraryUtilMockedStatic = Mockito.mockStatic(
			DesignLibraryUtil.class);
	private final Group _group = Mockito.mock(Group.class);
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final InfoItemServiceRegistry _infoItemServiceRegistry =
		Mockito.mock(InfoItemServiceRegistry.class);
	private final MockedStatic<LayoutPageTemplatePortletUtil>
		_layoutPageTemplatePortletUtilMockedStatic = Mockito.mockStatic(
			LayoutPageTemplatePortletUtil.class);
	private final LiferayPortletRequest _liferayPortletRequest = Mockito.mock(
		LiferayPortletRequest.class);
	private final LiferayPortletResponse _liferayPortletResponse = Mockito.mock(
		LiferayPortletResponse.class);

}