/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action;

import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Javier Moral
 */
public class EditDisplayPageMVCRenderCommandTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_editDisplayPageMVCRenderCommand, "_layoutLocalService",
			_layoutLocalService);
		ReflectionTestUtil.setFieldValue(
			_editDisplayPageMVCRenderCommand, "_layoutPageTemplateEntryService",
			_layoutPageTemplateEntryService);
		ReflectionTestUtil.setFieldValue(
			_editDisplayPageMVCRenderCommand, "_portal", _portal);

		Mockito.when(
			_portal.escapeRedirect(Mockito.anyString())
		).thenAnswer(
			invocation -> invocation.getArgument(0)
		);

		Mockito.when(
			_portal.getHttpServletResponse(_renderResponse)
		).thenReturn(
			_httpServletResponse
		);

		Mockito.when(
			_portletDisplay.getPortletDisplayName()
		).thenReturn(
			_PORTLET_DISPLAY_NAME
		);

		Mockito.when(
			_renderRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			_themeDisplay
		);

		Mockito.when(
			_themeDisplay.getPortletDisplay()
		).thenReturn(
			_portletDisplay
		);

		Mockito.when(
			_themeDisplay.getScopeGroupId()
		).thenReturn(
			_GROUP_ID
		);

		Mockito.when(
			_themeDisplay.getURLCurrent()
		).thenReturn(
			_URL_CURRENT
		);

		_portalUtilMockedStatic.when(
			() -> PortalUtil.stripURLAnchor(
				Mockito.anyString(), Mockito.anyString())
		).thenAnswer(
			invocation -> new String[] {invocation.getArgument(0), ""}
		);
	}

	@After
	public void tearDown() {
		_portalUtilMockedStatic.close();
	}

	@Test
	public void testRender() throws Exception {
		_testRenderDiscardsAnUnsafeRedirect();
		_testRenderKeepsASafeRedirect();
		_testRenderRedirectsToTheDraftLayoutEditor();
		_testRenderWithoutDraftLayout();
		_testRenderWithoutExternalReferenceCode();
		_testRenderWithUnknownExternalReferenceCode();
	}

	private String _captureRedirect() throws Exception {
		ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(
			String.class);

		Mockito.verify(
			_httpServletResponse, Mockito.times(1)
		).sendRedirect(
			argumentCaptor.capture()
		);

		return argumentCaptor.getValue();
	}

	private String _getDecodedParameter(String url, String name) {
		return HttpComponentsUtil.decodeURL(
			HttpComponentsUtil.getParameter(url, name, false));
	}

	private String _render(String externalReferenceCode) throws Exception {
		Mockito.clearInvocations(_httpServletResponse);

		Mockito.when(
			_renderRequest.getParameter(
				"displayPageTemplateExternalReferenceCode")
		).thenReturn(
			externalReferenceCode
		);

		return _editDisplayPageMVCRenderCommand.render(
			_renderRequest, _renderResponse);
	}

	private void _setUpResolvableEntry() throws Exception {
		LayoutPageTemplateEntry layoutPageTemplateEntry = Mockito.mock(
			LayoutPageTemplateEntry.class);

		Mockito.when(
			layoutPageTemplateEntry.getPlid()
		).thenReturn(
			_PLID
		);

		Mockito.when(
			_layoutPageTemplateEntryService.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					_EXTERNAL_REFERENCE_CODE, _GROUP_ID)
		).thenReturn(
			layoutPageTemplateEntry
		);

		Layout draftLayout = Mockito.mock(Layout.class);

		Mockito.when(
			_layoutLocalService.fetchDraftLayout(_PLID)
		).thenReturn(
			draftLayout
		);

		Mockito.when(
			_portal.getLayoutFullURL(draftLayout, _themeDisplay)
		).thenReturn(
			_LAYOUT_FULL_URL
		);
	}

	private void _testRenderDiscardsAnUnsafeRedirect() throws Exception {

		// A row action URL is attacker addressable, and the locked layout view
		// follows p_l_back_url without escaping it again

		_setUpResolvableEntry();

		Mockito.when(
			_renderRequest.getParameter("redirect")
		).thenReturn(
			_UNSAFE_REDIRECT
		);

		Mockito.when(
			_portal.escapeRedirect(_UNSAFE_REDIRECT)
		).thenReturn(
			null
		);

		_render(_EXTERNAL_REFERENCE_CODE);

		Assert.assertEquals(
			_URL_CURRENT,
			_getDecodedParameter(_captureRedirect(), "p_l_back_url"));
	}

	private void _testRenderKeepsASafeRedirect() throws Exception {
		_setUpResolvableEntry();

		Mockito.when(
			_renderRequest.getParameter("redirect")
		).thenReturn(
			_SAFE_REDIRECT
		);

		_render(_EXTERNAL_REFERENCE_CODE);

		Assert.assertEquals(
			_SAFE_REDIRECT,
			_getDecodedParameter(_captureRedirect(), "p_l_back_url"));
	}

	private void _testRenderRedirectsToTheDraftLayoutEditor() throws Exception {
		_setUpResolvableEntry();

		Mockito.when(
			_renderRequest.getParameter("redirect")
		).thenReturn(
			null
		);

		Assert.assertEquals(
			MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH,
			_render(_EXTERNAL_REFERENCE_CODE));

		String redirect = _captureRedirect();

		Assert.assertTrue(redirect, redirect.startsWith(_LAYOUT_FULL_URL));
		Assert.assertEquals(
			_URL_CURRENT, _getDecodedParameter(redirect, "p_l_back_url"));
		Assert.assertEquals(
			_PORTLET_DISPLAY_NAME,
			_getDecodedParameter(redirect, "p_l_back_url_title"));
		Assert.assertEquals(
			Constants.EDIT, _getDecodedParameter(redirect, "p_l_mode"));
	}

	private void _testRenderWithUnknownExternalReferenceCode()
		throws Exception {

		Assert.assertEquals(
			_MVC_PATH_VIEW, _render(RandomTestUtil.randomString()));
	}

	private void _testRenderWithoutDraftLayout() throws Exception {
		LayoutPageTemplateEntry layoutPageTemplateEntry = Mockito.mock(
			LayoutPageTemplateEntry.class);

		Mockito.when(
			layoutPageTemplateEntry.getPlid()
		).thenReturn(
			_PLID_WITHOUT_DRAFT_LAYOUT
		);

		Mockito.when(
			_layoutPageTemplateEntryService.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					_EXTERNAL_REFERENCE_CODE_WITHOUT_DRAFT_LAYOUT, _GROUP_ID)
		).thenReturn(
			layoutPageTemplateEntry
		);

		Assert.assertEquals(
			_MVC_PATH_VIEW,
			_render(_EXTERNAL_REFERENCE_CODE_WITHOUT_DRAFT_LAYOUT));
	}

	private void _testRenderWithoutExternalReferenceCode() throws Exception {
		Assert.assertEquals(_MVC_PATH_VIEW, _render(null));
		Assert.assertEquals(_MVC_PATH_VIEW, _render(""));
	}

	private static final String _EXTERNAL_REFERENCE_CODE =
		RandomTestUtil.randomString();

	private static final String _EXTERNAL_REFERENCE_CODE_WITHOUT_DRAFT_LAYOUT =
		RandomTestUtil.randomString();

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private static final String _LAYOUT_FULL_URL =
		"http://localhost:8080/web/guest/d-" + RandomTestUtil.randomString();

	private static final String _MVC_PATH_VIEW = "/view.jsp";

	private static final long _PLID = RandomTestUtil.randomLong();

	private static final long _PLID_WITHOUT_DRAFT_LAYOUT =
		RandomTestUtil.randomLong();

	private static final String _PORTLET_DISPLAY_NAME =
		RandomTestUtil.randomString();

	private static final String _SAFE_REDIRECT =
		"/group/guest/~/control_panel/manage?p_p_id=com_liferay_design_library";

	private static final String _UNSAFE_REDIRECT = "http://evil.example.com";

	private static final String _URL_CURRENT =
		"/group/guest/~/control_panel/manage";

	private final EditDisplayPageMVCRenderCommand
		_editDisplayPageMVCRenderCommand =
			new EditDisplayPageMVCRenderCommand();
	private final HttpServletResponse _httpServletResponse = Mockito.mock(
		HttpServletResponse.class);
	private final LayoutLocalService _layoutLocalService = Mockito.mock(
		LayoutLocalService.class);
	private final LayoutPageTemplateEntryService
		_layoutPageTemplateEntryService = Mockito.mock(
			LayoutPageTemplateEntryService.class);
	private final Portal _portal = Mockito.mock(Portal.class);
	private final MockedStatic<PortalUtil> _portalUtilMockedStatic =
		Mockito.mockStatic(PortalUtil.class);
	private final PortletDisplay _portletDisplay = Mockito.mock(
		PortletDisplay.class);
	private final RenderRequest _renderRequest = Mockito.mock(
		RenderRequest.class);
	private final RenderResponse _renderResponse = Mockito.mock(
		RenderResponse.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}