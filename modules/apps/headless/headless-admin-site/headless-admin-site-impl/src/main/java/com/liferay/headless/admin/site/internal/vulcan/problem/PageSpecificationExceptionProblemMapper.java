/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.vulcan.problem;

import com.liferay.headless.admin.site.internal.exception.PageSpecificationException;
import com.liferay.portal.vulcan.problem.Problem;
import com.liferay.portal.vulcan.problem.ProblemMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Javier Moral
 */
@Component(service = ProblemMapper.class)
public class PageSpecificationExceptionProblemMapper
	implements ProblemMapper<PageSpecificationException> {

	@Override
	public Problem getProblem(
		PageSpecificationException pageSpecificationException) {

		int type = pageSpecificationException.getType();

		if (type == PageSpecificationException.EXACTLY_ONE_REQUIRED) {
			return _getCountProblem(
				"Exactly one page specification is required",
				pageSpecificationException);
		}

		if (type == PageSpecificationException.EXACTLY_TWO_REQUIRED) {
			return _getCountProblem(
				"Exactly two page specifications are required",
				pageSpecificationException);
		}

		if (type == PageSpecificationException.INVALID) {
			return ProblemUtil.getProblem(
				"The page specification is invalid or has not been approved",
				Problem.Status.BAD_REQUEST,
				"page-specification-is-invalid-or-has-not-been-approved",
				pageSpecificationException);
		}

		if (type ==
				PageSpecificationException.
					MISMATCHED_EXTERNAL_REFERENCE_CODES) {

			return ProblemUtil.getProblem(
				"The draft and published page specifications have mismatched " +
					"external reference codes",
				Problem.Status.BAD_REQUEST,
				"draft-and-published-page-specifications-have-mismatched-" +
					"external-reference-codes",
				pageSpecificationException);
		}

		throw new IllegalArgumentException(
			"Unknown PageSpecificationException type: " + type);
	}

	private Problem _getCountProblem(
		String detail, PageSpecificationException pageSpecificationException) {

		return ProblemUtil.getProblem(
			detail, Problem.Status.BAD_REQUEST,
			"The number of page specifications does not match the page type " +
				"requirements",
			"number-of-page-specifications-does-not-match-the-page-type-" +
				"requirements",
			pageSpecificationException);
	}

}