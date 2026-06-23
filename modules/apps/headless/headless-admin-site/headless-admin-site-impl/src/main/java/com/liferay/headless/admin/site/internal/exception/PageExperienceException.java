/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Javier Moral
 */
public class PageExperienceException extends PortalException {

	public static final int CONTENT_PAGES_ONLY = 1;

	public static final int DEFAULT_EXPERIENCE_REQUIRED = 2;

	public static final int DEFAULT_REFERENCES_SEGMENT = 3;

	public static final int EXPERIENCE_REQUIRED = 4;

	public static final int INVALID_DEFAULT_PRIORITY = 5;

	public static final int MISMATCHED_EXTERNAL_REFERENCE_CODE = 6;

	public PageExperienceException(int type, String message) {
		super(message);

		_type = type;
	}

	public int getType() {
		return _type;
	}

	private final int _type;

}