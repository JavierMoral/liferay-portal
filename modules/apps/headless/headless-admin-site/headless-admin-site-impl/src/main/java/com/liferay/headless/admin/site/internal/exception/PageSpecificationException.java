/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Javier Moral
 */
public class PageSpecificationException extends PortalException {

	public static final int EXACTLY_ONE_REQUIRED = 1;

	public static final int EXACTLY_TWO_REQUIRED = 2;

	public static final int INVALID = 3;

	public static final int MISMATCHED_EXTERNAL_REFERENCE_CODES = 4;

	public PageSpecificationException(int type, String message) {
		super(message);

		_type = type;
	}

	public int getType() {
		return _type;
	}

	private final int _type;

}