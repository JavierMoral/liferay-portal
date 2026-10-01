/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.util;

import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;

/**
 * @author Javier Moral
 */
public class BackURLUtil {

	/**
	 * Returns the given URL stripped of the return path of the portlet that
	 * produced it, so that it can be nested in another URL as a back URL.
	 *
	 * <p>
	 * A back URL only has to identify the screen to come back to. Carrying the
	 * return path of that screen as well nests one more URL on every hop, and
	 * since each hop escapes the previous one, the length grows geometrically.
	 * Once the result goes over <code>Http#URL_MAXIMUM_LENGTH</code>,
	 * <code>HttpComponentsUtil#shortenURL(String)</code> silently discards the
	 * back URL altogether.
	 * </p>
	 */
	public static String getBackURL(String url) {
		String portletNamespace = PortalUtil.getPortletNamespace(
			HttpComponentsUtil.getParameter(url, "p_p_id", false));

		url = HttpComponentsUtil.removeParameter(
			url, portletNamespace + "backURL");

		return HttpComponentsUtil.removeParameter(
			url, portletNamespace + "redirect");
	}

}