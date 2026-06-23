/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Javier Moral
 */
public class NoSuchPageElementException extends PortalException {

	public NoSuchPageElementException(String externalReferenceCode) {
		super(
			"No page element exists with the external reference code " +
				externalReferenceCode);

		_externalReferenceCode = externalReferenceCode;
	}

	public String getExternalReferenceCode() {
		return _externalReferenceCode;
	}

	private final String _externalReferenceCode;

}