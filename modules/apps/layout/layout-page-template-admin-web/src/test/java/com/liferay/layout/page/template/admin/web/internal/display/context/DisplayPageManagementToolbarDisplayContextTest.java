/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Javier Moral
 */
public class DisplayPageManagementToolbarDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_setUpDisplayPageDisplayContext();
		_setUpLanguageUtil();
		_setUpThemeDisplay();
	}

	@Test
	@TestInfo("LPD-108335")
	public void testGetCreationMenu() {
		DisplayPageManagementToolbarDisplayContext
			displayPageManagementToolbarDisplayContext =
				new DisplayPageManagementToolbarDisplayContext(
					_httpServletRequest, _getMockLiferayPortletActionRequest(),
					new MockLiferayPortletRenderResponse(),
					_displayPageDisplayContext);

		CreationMenu creationMenu =
			displayPageManagementToolbarDisplayContext.getCreationMenu();

		List<DropdownItem> dropdownItems = (List<DropdownItem>)creationMenu.get(
			"primaryItems");

		Assert.assertEquals(dropdownItems.toString(), 2, dropdownItems.size());

		DropdownItem dropdownItem = dropdownItems.get(0);

		Map<String, Object> data = (Map<String, Object>)dropdownItem.get(
			"data");

		_assertLayoutPageTemplateCollectionId(
			(String)data.get("addDisplayPageCollectionURL"));

		dropdownItem = dropdownItems.get(1);

		_assertLayoutPageTemplateCollectionId((String)dropdownItem.get("href"));
	}

	private void _assertLayoutPageTemplateCollectionId(String url) {
		Assert.assertTrue(
			url,
			url.contains(
				"layoutPageTemplateCollectionId=" +
					_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID));
	}

	private MockLiferayPortletActionRequest
		_getMockLiferayPortletActionRequest() {

		return new MockLiferayPortletActionRequest() {

			@Override
			public Portlet getPortlet() {
				Portlet portlet = Mockito.mock(Portlet.class);

				Mockito.when(
					portlet.getPortletApp()
				).thenReturn(
					Mockito.mock(PortletApp.class)
				);

				return portlet;
			}

		};
	}

	private void _setUpDisplayPageDisplayContext() {
		Mockito.when(
			_displayPageDisplayContext.getDisplayPagesSearchContainer()
		).thenReturn(
			Mockito.mock(SearchContainer.class)
		);

		Mockito.when(
			_displayPageDisplayContext.getLayoutPageTemplateCollectionId()
		).thenReturn(
			_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID
		);
	}

	private void _setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(Mockito.mock(Language.class));
	}

	private void _setUpThemeDisplay() {
		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			_httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		Mockito.when(
			themeDisplay.getURLCurrent()
		).thenReturn(
			RandomTestUtil.randomString()
		);
	}

	private static final long _LAYOUT_PAGE_TEMPLATE_COLLECTION_ID =
		RandomTestUtil.randomLong();

	private final DisplayPageDisplayContext _displayPageDisplayContext =
		Mockito.mock(DisplayPageDisplayContext.class);
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);

}