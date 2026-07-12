/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Javier Moral
 */
public class LayoutStructureException extends PortalException {

	public LayoutStructureException() {
	}

	public LayoutStructureException(String msg) {
		super(msg);
	}

	public LayoutStructureException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

	public LayoutStructureException(Throwable throwable) {
		super(throwable);
	}

}