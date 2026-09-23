/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action;

import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.taglib.security.PermissionsURLTag;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
public class ViewDisplayPagePermissionsMVCRenderCommandTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_viewDisplayPagePermissionsMVCRenderCommand,
			"_layoutPageTemplateEntryService", _layoutPageTemplateEntryService);
		ReflectionTestUtil.setFieldValue(
			_viewDisplayPagePermissionsMVCRenderCommand, "_portal", _portal);

		Mockito.when(
			_portal.getHttpServletRequest(_renderRequest)
		).thenReturn(
			_httpServletRequest
		);

		Mockito.when(
			_portal.getHttpServletResponse(_renderResponse)
		).thenReturn(
			_httpServletResponse
		);

		Mockito.when(
			_renderRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			_themeDisplay
		);

		Mockito.when(
			_themeDisplay.getScopeGroupId()
		).thenReturn(
			_GROUP_ID
		);
	}

	@After
	public void tearDown() {
		_permissionsURLTagMockedStatic.close();
	}

	@Test
	public void testRender() throws Exception {
		_testRenderRedirectsToThePermissionsModal();
		_testRenderWithoutExternalReferenceCode();
		_testRenderWithUnknownExternalReferenceCode();
	}

	private String _render(String externalReferenceCode) throws Exception {
		Mockito.when(
			_renderRequest.getParameter(
				"displayPageTemplateExternalReferenceCode")
		).thenReturn(
			externalReferenceCode
		);

		return _viewDisplayPagePermissionsMVCRenderCommand.render(
			_renderRequest, _renderResponse);
	}

	private void _testRenderRedirectsToThePermissionsModal() throws Exception {
		LayoutPageTemplateEntry layoutPageTemplateEntry = Mockito.mock(
			LayoutPageTemplateEntry.class);

		Mockito.when(
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
		).thenReturn(
			_LAYOUT_PAGE_TEMPLATE_ENTRY_ID
		);

		Mockito.when(
			layoutPageTemplateEntry.getName()
		).thenReturn(
			_NAME
		);

		Mockito.when(
			_layoutPageTemplateEntryService.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					_EXTERNAL_REFERENCE_CODE, _GROUP_ID)
		).thenReturn(
			layoutPageTemplateEntry
		);

		_permissionsURLTagMockedStatic.when(
			() -> PermissionsURLTag.doTag(
				StringPool.BLANK, LayoutPageTemplateEntry.class.getName(),
				_NAME, null, String.valueOf(_LAYOUT_PAGE_TEMPLATE_ENTRY_ID),
				LiferayWindowState.POP_UP.toString(), null, _httpServletRequest)
		).thenReturn(
			_PERMISSIONS_URL
		);

		Assert.assertEquals(
			MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH,
			_render(_EXTERNAL_REFERENCE_CODE));

		Mockito.verify(
			_httpServletResponse, Mockito.times(1)
		).sendRedirect(
			_PERMISSIONS_URL
		);
	}

	private void _testRenderWithUnknownExternalReferenceCode()
		throws Exception {

		Assert.assertEquals(
			_MVC_PATH_VIEW, _render(RandomTestUtil.randomString()));
	}

	private void _testRenderWithoutExternalReferenceCode() throws Exception {
		Assert.assertEquals(_MVC_PATH_VIEW, _render(null));
		Assert.assertEquals(_MVC_PATH_VIEW, _render(""));
	}

	private static final String _EXTERNAL_REFERENCE_CODE =
		RandomTestUtil.randomString();

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private static final long _LAYOUT_PAGE_TEMPLATE_ENTRY_ID =
		RandomTestUtil.randomLong();

	private static final String _MVC_PATH_VIEW = "/view.jsp";

	private static final String _NAME = RandomTestUtil.randomString();

	private static final String _PERMISSIONS_URL =
		"http://localhost:8080/group/guest/~/control_panel/manage?p_p_id=" +
			"com_liferay_portlet_configuration_web_portlet_" +
				"PortletConfigurationPortlet";

	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final HttpServletResponse _httpServletResponse = Mockito.mock(
		HttpServletResponse.class);
	private final LayoutPageTemplateEntryService
		_layoutPageTemplateEntryService = Mockito.mock(
			LayoutPageTemplateEntryService.class);
	private final MockedStatic<PermissionsURLTag>
		_permissionsURLTagMockedStatic = Mockito.mockStatic(
			PermissionsURLTag.class);
	private final Portal _portal = Mockito.mock(Portal.class);
	private final RenderRequest _renderRequest = Mockito.mock(
		RenderRequest.class);
	private final RenderResponse _renderResponse = Mockito.mock(
		RenderResponse.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);
	private final ViewDisplayPagePermissionsMVCRenderCommand
		_viewDisplayPagePermissionsMVCRenderCommand =
			new ViewDisplayPagePermissionsMVCRenderCommand();

}