/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.vulcan.problem;

import com.liferay.portal.kernel.exception.LayoutFriendlyURLException;
import com.liferay.portal.vulcan.problem.Problem;
import com.liferay.portal.vulcan.problem.ProblemMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Javier Moral
 */
@Component(service = ProblemMapper.class)
public class LayoutFriendlyURLExceptionProblemMapper
	implements ProblemMapper<LayoutFriendlyURLException> {

	@Override
	public Problem getProblem(
		LayoutFriendlyURLException layoutFriendlyURLException) {

		int type = layoutFriendlyURLException.getType();

		if ((type == LayoutFriendlyURLException.DUPLICATE) ||
			(type == LayoutFriendlyURLException.POSSIBLE_DUPLICATE)) {

			return ProblemUtil.getProblem(
				"The friendly URL is already in use", Problem.Status.CONFLICT,
				layoutFriendlyURLException);
		}

		if (type == LayoutFriendlyURLException.KEYWORD_CONFLICT) {
			return ProblemUtil.getProblem(
				"The friendly URL conflicts with a reserved keyword",
				Problem.Status.CONFLICT, layoutFriendlyURLException);
		}

		String message = "The friendly URL is invalid";

		if (type == LayoutFriendlyURLException.ADJACENT_SLASHES) {
			message = "The friendly URL must not contain consecutive slashes";
		}
		else if (type == LayoutFriendlyURLException.DOES_NOT_START_WITH_SLASH) {
			message = "The friendly URL must begin with a slash";
		}
		else if (type == LayoutFriendlyURLException.ENDS_WITH_DASH) {
			message = "The friendly URL must not end with a dash";
		}
		else if (type == LayoutFriendlyURLException.ENDS_WITH_SLASH) {
			message = "The friendly URL must not end with a slash";
		}
		else if (type == LayoutFriendlyURLException.INVALID_CHARACTERS) {
			message = "The friendly URL contains an invalid character";
		}
		else if (type == LayoutFriendlyURLException.TOO_DEEP) {
			message = "The friendly URL has too many path segments";
		}
		else if (type == LayoutFriendlyURLException.TOO_LONG) {
			message = "The friendly URL is too long";
		}
		else if (type == LayoutFriendlyURLException.TOO_SHORT) {
			message = "The friendly URL is too short";
		}

		return ProblemUtil.getProblem(
			message, Problem.Status.BAD_REQUEST, layoutFriendlyURLException);
	}

}