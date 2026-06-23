/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.vulcan.problem;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.problem.Problem;

import java.util.Locale;

/**
 * @author Lourdes Fernández Besada
 * @author Javier Moral
 */
public class ProblemUtil {

	public static <T extends Throwable> Problem getCannotCreateInSiteProblem(
		String entity, T throwable) {

		String message = "A " + entity + " cannot be created in this site";

		String type = StringUtil.toLowerCase(
			StringUtil.replace(entity, CharPool.SPACE, CharPool.DASH) +
				"-cannot-be-created-in-this-site");

		return _getProblem(
			type, message, message, Problem.Status.BAD_REQUEST, throwable);
	}

	public static <T extends Throwable> Problem getDuplicateProblem(
		String attribute, String entity, String value, T throwable) {

		String title = StringBundler.concat(
			"A ", entity, " with the same ", attribute, " already exists");

		String detail = title;

		if (Validator.isNotNull(value)) {
			detail = StringBundler.concat(
				"A ", entity, " with ", attribute, " ", value,
				" already exists");
		}

		String type = StringUtil.toLowerCase(
			StringBundler.concat(
				StringUtil.replace(entity, CharPool.SPACE, CharPool.DASH),
				"-with-the-same-",
				StringUtil.replace(attribute, CharPool.SPACE, CharPool.DASH),
				"-already-exists"));

		return _getProblem(
			type, detail, title, Problem.Status.CONFLICT, throwable);
	}

	public static <T extends Throwable> Problem getInvalidCharacterProblem(
		String character, String field, T throwable) {

		String title = "The " + field + " contains an invalid character";

		String detail = title;

		if (Validator.isNotNull(character)) {
			detail = StringBundler.concat(
				"The ", field, " contains an invalid character: \"", character,
				"\"");
		}

		String type = StringUtil.toLowerCase(
			StringUtil.replace(field, CharPool.SPACE, CharPool.DASH) +
				"-contains-an-invalid-character");

		return _getProblem(
			type, detail, title, Problem.Status.BAD_REQUEST, throwable);
	}

	public static <T extends Throwable> Problem getKeyInvalidCharacterProblem(
		T throwable) {

		String message =
			"The key must contain only alphanumeric characters, dashes, and " +
				"underscores";

		return _getProblem(
			"key-must-contain-only-alphanumeric-characters-dashes-and-" +
				"underscores",
			message, message, Problem.Status.BAD_REQUEST, throwable);
	}

	public static <T extends Throwable> Problem getKeyTooLongProblem(
		int maxLength, T throwable) {

		String title = "The key is too long";

		String detail = title;

		if (maxLength > 0) {
			detail =
				"The key exceeds the maximum length of " + maxLength +
					" characters";
		}

		return _getProblem(
			"key-is-too-long", detail, title, Problem.Status.BAD_REQUEST,
			throwable);
	}

	public static <T extends Throwable> Problem getMustBePublishedFirstProblem(
		String entity, T throwable) {

		String message = "The default " + entity + " must be published first";

		String type = StringUtil.toLowerCase(
			"default-" +
				StringUtil.replace(entity, CharPool.SPACE, CharPool.DASH) +
					"-must-be-published-first");

		return _getProblem(
			type, message, message, Problem.Status.CONFLICT, throwable);
	}

	public static <T extends Throwable> Problem getNameTooLongProblem(
		int maxLength, T throwable) {

		String title = "The name is too long";

		String detail = title;

		if (maxLength > 0) {
			detail =
				"The name exceeds the maximum length of " + maxLength +
					" characters";
		}

		return _getProblem(
			"name-is-too-long", detail, title, Problem.Status.BAD_REQUEST,
			throwable);
	}

	public static <T extends Throwable> Problem getProblem(
		Problem.Status status, T throwable) {

		return getProblem(throwable.getMessage(), status, throwable);
	}

	public static <T extends Throwable> Problem getProblem(
		String detail, Problem.Status status, String title, String type,
		T throwable) {

		return _getProblem(type, detail, title, status, throwable);
	}

	public static <T extends Throwable> Problem getProblem(
		String message, Problem.Status status, String type, T throwable) {

		return _getProblem(type, message, message, status, throwable);
	}

	public static <T extends Throwable> Problem getProblem(
		String message, Problem.Status status, T throwable) {

		return _getProblem(null, message, message, status, throwable);
	}

	public static <T extends Throwable> Problem getProblem(T throwable) {
		return getProblem(Problem.Status.BAD_REQUEST, throwable);
	}

	public static <T extends Throwable> Problem getReferenceTypeMismatchProblem(
		String entity, String externalReferenceCode, T throwable) {

		String title =
			"The external reference code does not point to a " + entity;

		String detail = title;

		if (Validator.isNotNull(externalReferenceCode)) {
			detail = StringBundler.concat(
				"The external reference code \"", externalReferenceCode,
				"\" does not point to a ", entity);
		}

		String type = StringUtil.toLowerCase(
			"external-reference-code-does-not-point-to-a-" +
				StringUtil.replace(entity, CharPool.SPACE, CharPool.DASH));

		return _getProblem(
			type, detail, title, Problem.Status.BAD_REQUEST, throwable);
	}

	public static <T extends Throwable> Problem getRequiredFieldProblem(
		String field, T throwable) {

		String message = "A " + field + " is required";

		String type = StringUtil.toLowerCase(
			StringUtil.replace(field, CharPool.SPACE, CharPool.DASH) +
				"-is-required");

		return _getProblem(
			type, message, message, Problem.Status.BAD_REQUEST, throwable);
	}

	public static <T extends Throwable> Problem getTypeMismatchProblem(
		String actualType, String expectedType, T throwable) {

		String message = StringBundler.concat(
			"The ", actualType, " type does not match the ", expectedType,
			" type");

		String type = StringUtil.toLowerCase(
			StringBundler.concat(
				StringUtil.replace(actualType, CharPool.SPACE, CharPool.DASH),
				"-type-does-not-match-the-",
				StringUtil.replace(expectedType, CharPool.SPACE, CharPool.DASH),
				"-type"));

		return _getProblem(
			type, message, message, Problem.Status.BAD_REQUEST, throwable);
	}

	public static <T extends Throwable> Problem getUnsupportedEnumProblem(
		String enumType, String supportedTypes, String value, T throwable) {

		String title = "The page type is not supported";

		String detail = title;

		if (Validator.isNotNull(value)) {
			if (Validator.isNull(supportedTypes)) {
				detail = StringBundler.concat(
					"\"", value, "\" is not a supported ", enumType);
			}
			else {
				detail = StringBundler.concat(
					"\"", value, "\" is not a supported ", enumType,
					". Supported types are: ", supportedTypes);
			}
		}

		String type = StringUtil.toLowerCase(
			StringUtil.replace(enumType, CharPool.SPACE, CharPool.DASH) +
				"-not-supported");

		return _getProblem(
			type, detail, title, Problem.Status.BAD_REQUEST, throwable);
	}

	private static Problem _getProblem(
		String type, String detail, String title, Problem.Status status,
		Throwable throwable) {

		String resolvedType = _resolveType(type, throwable);

		return new Problem() {

			@Override
			public String getDetail(Locale locale) {
				return detail;
			}

			@Override
			public Status getStatus() {
				return status;
			}

			@Override
			public String getTitle(Locale locale) {
				return title;
			}

			@Override
			public String getType() {
				return resolvedType;
			}

		};
	}

	private static String _resolveType(String type, Throwable throwable) {
		if (type != null) {
			return type;
		}

		Class<? extends Throwable> throwableClass = throwable.getClass();

		return throwableClass.getName();
	}

}