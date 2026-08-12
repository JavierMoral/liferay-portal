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
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
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
	public void setUp() {

		// HttpComponentsUtil#addParameters reaches PortalUtil#stripURLAnchor

		Portal portal = Mockito.mock(Portal.class);

		Mockito.when(
			portal.stripURLAnchor(Mockito.anyString(), Mockito.anyString())
		).thenAnswer(
			invocationOnMock -> new String[] {
				invocationOnMock.getArgument(0), ""
			}
		);

		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(portal);
	}

	@Test
	public void testRenderRedirectsToDraftLayoutInEditMode() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();
		long plid = RandomTestUtil.randomLong();
		String layoutFullURL = "http://localhost:8080/group/guest/d/x";

		Layout draftLayout = Mockito.mock(Layout.class);

		LayoutLocalService layoutLocalService = Mockito.mock(
			LayoutLocalService.class);

		Mockito.when(
			layoutLocalService.fetchDraftLayout(plid)
		).thenReturn(
			draftLayout
		);

		LayoutPageTemplateEntry layoutPageTemplateEntry = Mockito.mock(
			LayoutPageTemplateEntry.class);

		Mockito.when(
			layoutPageTemplateEntry.getPlid()
		).thenReturn(
			plid
		);

		ThemeDisplay themeDisplay = _getThemeDisplay();

		LayoutPageTemplateEntryService layoutPageTemplateEntryService =
			Mockito.mock(LayoutPageTemplateEntryService.class);

		Mockito.when(
			layoutPageTemplateEntryService.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					externalReferenceCode, themeDisplay.getScopeGroupId())
		).thenReturn(
			layoutPageTemplateEntry
		);

		Portal portal = Mockito.mock(Portal.class);

		Mockito.when(
			portal.getLayoutFullURL(draftLayout, themeDisplay)
		).thenReturn(
			layoutFullURL
		);

		HttpServletResponse httpServletResponse = Mockito.mock(
			HttpServletResponse.class);

		RenderResponse renderResponse = Mockito.mock(RenderResponse.class);

		Mockito.when(
			portal.getHttpServletResponse(renderResponse)
		).thenReturn(
			httpServletResponse
		);

		Assert.assertEquals(
			MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH,
			_createMVCRenderCommand(
				layoutLocalService, layoutPageTemplateEntryService, portal
			).render(
				_getRenderRequest(externalReferenceCode, themeDisplay),
				renderResponse
			));

		ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(
			String.class);

		Mockito.verify(
			httpServletResponse
		).sendRedirect(
			argumentCaptor.capture()
		);

		String redirectURL = argumentCaptor.getValue();

		Assert.assertTrue(redirectURL, redirectURL.startsWith(layoutFullURL));
		Assert.assertTrue(
			redirectURL, redirectURL.contains("p_l_mode=" + Constants.EDIT));
	}

	@Test
	public void testRenderWhenDisplayPageTemplateIsMissing() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();

		ThemeDisplay themeDisplay = _getThemeDisplay();

		LayoutPageTemplateEntryService layoutPageTemplateEntryService =
			Mockito.mock(LayoutPageTemplateEntryService.class);

		Mockito.when(
			layoutPageTemplateEntryService.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					externalReferenceCode, themeDisplay.getScopeGroupId())
		).thenReturn(
			null
		);

		Assert.assertEquals(
			"/view.jsp",
			_createMVCRenderCommand(
				Mockito.mock(LayoutLocalService.class),
				layoutPageTemplateEntryService, Mockito.mock(Portal.class)
			).render(
				_getRenderRequest(externalReferenceCode, themeDisplay),
				Mockito.mock(RenderResponse.class)
			));
	}

	@Test
	public void testRenderWhenExternalReferenceCodeIsNull() throws Exception {
		Assert.assertEquals(
			"/view.jsp",
			_createMVCRenderCommand(
				Mockito.mock(LayoutLocalService.class),
				Mockito.mock(LayoutPageTemplateEntryService.class),
				Mockito.mock(Portal.class)
			).render(
				_getRenderRequest(null, _getThemeDisplay()),
				Mockito.mock(RenderResponse.class)
			));
	}

	private EditDisplayPageMVCRenderCommand _createMVCRenderCommand(
		LayoutLocalService layoutLocalService,
		LayoutPageTemplateEntryService layoutPageTemplateEntryService,
		Portal portal) {

		EditDisplayPageMVCRenderCommand editDisplayPageMVCRenderCommand =
			new EditDisplayPageMVCRenderCommand();

		ReflectionTestUtil.setFieldValue(
			editDisplayPageMVCRenderCommand, "_layoutLocalService",
			layoutLocalService);
		ReflectionTestUtil.setFieldValue(
			editDisplayPageMVCRenderCommand, "_layoutPageTemplateEntryService",
			layoutPageTemplateEntryService);
		ReflectionTestUtil.setFieldValue(
			editDisplayPageMVCRenderCommand, "_portal", portal);

		return editDisplayPageMVCRenderCommand;
	}

	private RenderRequest _getRenderRequest(
		String externalReferenceCode, ThemeDisplay themeDisplay) {

		RenderRequest renderRequest = Mockito.mock(RenderRequest.class);

		Mockito.when(
			renderRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		Mockito.when(
			renderRequest.getParameter(
				"displayPageTemplateExternalReferenceCode")
		).thenReturn(
			externalReferenceCode
		);

		return renderRequest;
	}

	private ThemeDisplay _getThemeDisplay() {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setScopeGroupId(RandomTestUtil.randomLong());

		return themeDisplay;
	}

}