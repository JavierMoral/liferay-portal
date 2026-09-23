/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action;

import com.liferay.design.library.util.DesignLibraryUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.ActionRequest;

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
public class AddDisplayPageMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_addDisplayPageMVCActionCommand, "_language", _language);
		ReflectionTestUtil.setFieldValue(
			_addDisplayPageMVCActionCommand, "_layoutLocalService",
			_layoutLocalService);
		ReflectionTestUtil.setFieldValue(
			_addDisplayPageMVCActionCommand, "_portal", _portal);

		Mockito.when(
			_actionRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			_themeDisplay
		);

		Mockito.when(
			_group.getDescriptiveName(LocaleUtil.US)
		).thenReturn(
			_DESIGN_LIBRARY_NAME
		);

		Mockito.when(
			_language.get(LocaleUtil.US, "page-templates")
		).thenReturn(
			"page-templates"
		);

		Mockito.when(
			_layoutLocalService.fetchDraftLayout(_PLID)
		).thenReturn(
			_draftLayout
		);

		Mockito.when(
			_layoutPageTemplateEntry.getPlid()
		).thenReturn(
			_PLID
		);

		Mockito.when(
			_portal.getHttpServletRequest(_actionRequest)
		).thenReturn(
			_httpServletRequest
		);

		Mockito.when(
			_portal.getLayoutFullURL(_draftLayout, _themeDisplay)
		).thenReturn(
			_LAYOUT_FULL_URL
		);

		Mockito.when(
			_themeDisplay.getLocale()
		).thenReturn(
			LocaleUtil.US
		);

		Mockito.when(
			_themeDisplay.getScopeGroup()
		).thenReturn(
			_group
		);

		_portalUtilMockedStatic.when(
			() -> PortalUtil.stripURLAnchor(
				Mockito.anyString(), Mockito.anyString())
		).thenAnswer(
			invocation -> new String[] {invocation.getArgument(0), ""}
		);

		_portletURLFactoryUtilMockedStatic.when(
			() -> PortletURLFactoryUtil.create(
				Mockito.eq(_actionRequest), Mockito.anyString(),
				Mockito.anyString())
		).thenReturn(
			_portletURL
		);
	}

	@After
	public void tearDown() {
		_designLibraryUtilMockedStatic.close();
		_portalUtilMockedStatic.close();
		_portletURLFactoryUtilMockedStatic.close();
	}

	@Test
	public void testGetRedirectURL() throws Exception {
		_testGetRedirectURLFromADesignLibrary();
		_testGetRedirectURLFromASite();
	}

	private String _getDecodedParameter(String url, String name) {
		return HttpComponentsUtil.decodeURL(
			HttpComponentsUtil.getParameter(url, name, false));
	}

	private String _getRedirectURL() throws Exception {
		return ReflectionTestUtil.invoke(
			_addDisplayPageMVCActionCommand, "getRedirectURL",
			new Class<?>[] {ActionRequest.class, LayoutPageTemplateEntry.class},
			_actionRequest, _layoutPageTemplateEntry);
	}

	private void _testGetRedirectURLFromADesignLibrary() throws Exception {

		// Without this branch the editor sends the author back to the site's
		// page templates instead of the Design Library they created from

		_designLibraryUtilMockedStatic.when(
			() -> DesignLibraryUtil.isDesignLibraryScope(_group)
		).thenReturn(
			true
		);

		_designLibraryUtilMockedStatic.when(
			() -> DesignLibraryUtil.getDesignLibraryResourcesURL(
				_group, _httpServletRequest)
		).thenReturn(
			_DESIGN_LIBRARY_RESOURCES_URL
		);

		String redirectURL = _getRedirectURL();

		Assert.assertTrue(
			redirectURL, redirectURL.startsWith(_LAYOUT_FULL_URL));
		Assert.assertEquals(
			_DESIGN_LIBRARY_RESOURCES_URL,
			_getDecodedParameter(redirectURL, "p_l_back_url"));
		Assert.assertEquals(
			_DESIGN_LIBRARY_NAME,
			_getDecodedParameter(redirectURL, "p_l_back_url_title"));
		Assert.assertEquals(
			Constants.EDIT, _getDecodedParameter(redirectURL, "p_l_mode"));
	}

	private void _testGetRedirectURLFromASite() throws Exception {
		_designLibraryUtilMockedStatic.when(
			() -> DesignLibraryUtil.isDesignLibraryScope(_group)
		).thenReturn(
			false
		);

		String redirectURL = _getRedirectURL();

		Assert.assertEquals(
			"page-templates",
			_getDecodedParameter(redirectURL, "p_l_back_url_title"));
		Assert.assertNotEquals(
			_DESIGN_LIBRARY_RESOURCES_URL,
			_getDecodedParameter(redirectURL, "p_l_back_url"));
	}

	private static final String _DESIGN_LIBRARY_NAME =
		RandomTestUtil.randomString();

	private static final String _DESIGN_LIBRARY_RESOURCES_URL =
		"/group/guest/~/control_panel/manage?p_p_id=com_liferay_design_library";

	private static final String _LAYOUT_FULL_URL =
		"http://localhost:8080/web/guest/d-" + RandomTestUtil.randomString();

	private static final long _PLID = RandomTestUtil.randomLong();

	private final ActionRequest _actionRequest = Mockito.mock(
		ActionRequest.class);
	private final AddDisplayPageMVCActionCommand
		_addDisplayPageMVCActionCommand = new AddDisplayPageMVCActionCommand();
	private final MockedStatic<DesignLibraryUtil>
		_designLibraryUtilMockedStatic = Mockito.mockStatic(
			DesignLibraryUtil.class);
	private final Layout _draftLayout = Mockito.mock(Layout.class);
	private final Group _group = Mockito.mock(Group.class);
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final Language _language = Mockito.mock(Language.class);
	private final LayoutLocalService _layoutLocalService = Mockito.mock(
		LayoutLocalService.class);
	private final LayoutPageTemplateEntry _layoutPageTemplateEntry =
		Mockito.mock(LayoutPageTemplateEntry.class);
	private final Portal _portal = Mockito.mock(Portal.class);
	private final MockedStatic<PortalUtil> _portalUtilMockedStatic =
		Mockito.mockStatic(PortalUtil.class);
	private final LiferayPortletURL _portletURL = Mockito.mock(
		LiferayPortletURL.class);
	private final MockedStatic<PortletURLFactoryUtil>
		_portletURLFactoryUtilMockedStatic = Mockito.mockStatic(
			PortletURLFactoryUtil.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}