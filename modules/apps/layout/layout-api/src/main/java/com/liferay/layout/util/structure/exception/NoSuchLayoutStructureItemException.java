/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.util.structure.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Eudaldo Alonso
 * @author Javier Moral
 */
public class NoSuchLayoutStructureItemException extends PortalException {

	public NoSuchLayoutStructureItemException() {
	}

	public NoSuchLayoutStructureItemException(String externalReferenceCode) {
		super(
			"No layout structure item exists with the external reference " +
				"code " + externalReferenceCode);

		_externalReferenceCode = externalReferenceCode;
	}

	public String getExternalReferenceCode() {
		return _externalReferenceCode;
	}

	private String _externalReferenceCode;

}