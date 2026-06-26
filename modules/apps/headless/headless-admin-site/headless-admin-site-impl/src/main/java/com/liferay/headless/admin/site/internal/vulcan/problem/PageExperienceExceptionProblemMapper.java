/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.vulcan.problem;

import com.liferay.headless.admin.site.internal.exception.PageExperienceException;
import com.liferay.portal.vulcan.problem.Problem;
import com.liferay.portal.vulcan.problem.ProblemMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Javier Moral
 */
@Component(service = ProblemMapper.class)
public class PageExperienceExceptionProblemMapper
	implements ProblemMapper<PageExperienceException> {

	@Override
	public Problem getProblem(PageExperienceException pageExperienceException) {
		int type = pageExperienceException.getType();

		if (type == PageExperienceException.DEFAULT_EXPERIENCE_REQUIRED) {
			return ProblemUtil.getRequiredFieldProblem(
				"default page experience", pageExperienceException);
		}

		if (type == PageExperienceException.EXPERIENCE_REQUIRED) {
			return ProblemUtil.getRequiredFieldProblem(
				"page experience", pageExperienceException);
		}

		return ProblemUtil.getProblem(
			_getMessage(type), Problem.Status.BAD_REQUEST, _getType(type),
			pageExperienceException);
	}

	private String _getMessage(int type) {
		if (type == PageExperienceException.CONTENT_PAGES_ONLY) {
			return "Only site pages can define additional page experiences";
		}

		if (type == PageExperienceException.DEFAULT_REFERENCES_SEGMENT) {
			return "The default page experience cannot reference a segment";
		}

		if (type == PageExperienceException.INVALID_DEFAULT_PRIORITY) {
			return "The default page experience must have a priority of 0";
		}

		if (type ==
				PageExperienceException.MISMATCHED_EXTERNAL_REFERENCE_CODE) {

			return "The external reference code does not match the target " +
				"page's experience external reference code";
		}

		throw new IllegalArgumentException(
			"Unknown PageExperienceException type: " + type);
	}

	private String _getType(int type) {
		if (type == PageExperienceException.CONTENT_PAGES_ONLY) {
			return "only-site-pages-can-define-additional-page-experiences";
		}

		if (type == PageExperienceException.DEFAULT_REFERENCES_SEGMENT) {
			return "default-page-experience-cannot-reference-a-segment";
		}

		if (type == PageExperienceException.INVALID_DEFAULT_PRIORITY) {
			return "default-page-experience-must-have-a-priority-of-0";
		}

		if (type ==
				PageExperienceException.MISMATCHED_EXTERNAL_REFERENCE_CODE) {

			return "external-reference-code-does-not-match-the-target-pages-" +
				"experience-external-reference-code";
		}

		throw new IllegalArgumentException(
			"Unknown PageExperienceException type: " + type);
	}

}