/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.http;

import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Javier Moral
 */
public class IsolatedAttributesServletRequestTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetAttribute() {
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute("alpha", "1");

		IsolatedAttributesServletRequest isolatedAttributesServletRequest =
			new IsolatedAttributesServletRequest(httpServletRequest);

		Assert.assertEquals(
			"1", isolatedAttributesServletRequest.getAttribute("alpha"));

		isolatedAttributesServletRequest.setAttribute("alpha", "2");

		Assert.assertEquals(
			"2", isolatedAttributesServletRequest.getAttribute("alpha"));

		Assert.assertEquals("1", httpServletRequest.getAttribute("alpha"));
	}

	@Test
	public void testGetAttributeNames() {
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute("alpha", "1");

		IsolatedAttributesServletRequest isolatedAttributesServletRequest =
			new IsolatedAttributesServletRequest(httpServletRequest);

		isolatedAttributesServletRequest.removeAttribute("alpha");
		isolatedAttributesServletRequest.setAttribute("beta", "2");

		List<String> names = Collections.list(
			isolatedAttributesServletRequest.getAttributeNames());

		Assert.assertEquals(names.toString(), 1, names.size());
		Assert.assertEquals("beta", names.get(0));
	}

	@Test
	public void testRemoveAttribute() {
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute("alpha", "1");

		IsolatedAttributesServletRequest isolatedAttributesServletRequest =
			new IsolatedAttributesServletRequest(httpServletRequest);

		isolatedAttributesServletRequest.removeAttribute("alpha");

		Assert.assertNull(
			isolatedAttributesServletRequest.getAttribute("alpha"));

		Assert.assertEquals("1", httpServletRequest.getAttribute("alpha"));
	}

	@Test
	public void testSetAttribute() {
		_testSetAttribute();

		_testSetAttributeWithRequestDispatcherAttribute();
	}

	private void _testSetAttribute() {
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		IsolatedAttributesServletRequest isolatedAttributesServletRequest =
			new IsolatedAttributesServletRequest(httpServletRequest);

		isolatedAttributesServletRequest.setAttribute("alpha", "1");

		Assert.assertEquals(
			"1", isolatedAttributesServletRequest.getAttribute("alpha"));

		Assert.assertNull(httpServletRequest.getAttribute("alpha"));
	}

	private void _testSetAttributeWithRequestDispatcherAttribute() {
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		IsolatedAttributesServletRequest isolatedAttributesServletRequest =
			new IsolatedAttributesServletRequest(httpServletRequest);

		isolatedAttributesServletRequest.setAttribute(
			JavaConstants.JAKARTA_SERVLET_INCLUDE_REQUEST_URI, "/alpha");

		Assert.assertEquals(
			"/alpha",
			httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_SERVLET_INCLUDE_REQUEST_URI));
	}

}