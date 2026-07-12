/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.exception;

import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Brian Wing Shun Chan
 */
public class LayoutPageTemplateEntryNameException extends PortalException {

	public LayoutPageTemplateEntryNameException() {
		_type = LayoutPageTemplateEntryTypeConstants.BASIC;
	}

	public LayoutPageTemplateEntryNameException(String msg) {
		super(msg);

		_type = LayoutPageTemplateEntryTypeConstants.BASIC;
	}

	public LayoutPageTemplateEntryNameException(
		String msg, Throwable throwable) {

		super(msg, throwable);

		_type = LayoutPageTemplateEntryTypeConstants.BASIC;
	}

	public LayoutPageTemplateEntryNameException(Throwable throwable) {
		super(throwable);

		_type = LayoutPageTemplateEntryTypeConstants.BASIC;
	}

	public int getType() {
		return _type;
	}

	public static class MustNotBeDuplicate
		extends LayoutPageTemplateEntryNameException {

		public MustNotBeDuplicate(long groupId, String name) {
			this(groupId, name, LayoutPageTemplateEntryTypeConstants.BASIC);
		}

		public MustNotBeDuplicate(long groupId, String name, int type) {
			super(
				StringBundler.concat(
					"Duplicate layout page template for group ", groupId,
					" with name ", name),
				type);
		}

	}

	public static class MustNotBeNull
		extends LayoutPageTemplateEntryNameException {

		public MustNotBeNull() {
			super("Name must not be null");
		}

	}

	public static class MustNotContainInvalidCharacters
		extends LayoutPageTemplateEntryNameException {

		public MustNotContainInvalidCharacters(char character) {
			super("Invalid character in name " + character);

			this.character = character;
		}

		public final char character;

	}

	public static class MustNotExceedMaximumSize
		extends LayoutPageTemplateEntryNameException {

		public MustNotExceedMaximumSize(int maxLength) {
			super("Maximum length of name exceeded " + maxLength);
		}

	}

	private LayoutPageTemplateEntryNameException(String msg, int type) {
		super(msg);

		_type = type;
	}

	private final int _type;

}