/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.vulcan.problem;

import com.liferay.layout.page.template.exception.LayoutPageTemplateEntryLayoutPageTemplateEntryKeyException;
import com.liferay.portal.vulcan.problem.Problem;
import com.liferay.portal.vulcan.problem.ProblemMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Javier Moral
 */
@Component(service = ProblemMapper.class)
public class LayoutPageTemplateEntryKeyInvalidCharacterExceptionProblemMapper
	implements ProblemMapper
		<LayoutPageTemplateEntryLayoutPageTemplateEntryKeyException.
			MustNotContainInvalidCharacters> {

	@Override
	public Problem getProblem(
		LayoutPageTemplateEntryLayoutPageTemplateEntryKeyException.
			MustNotContainInvalidCharacters mustNotContainInvalidCharacters) {

		return ProblemUtil.getProblem(
			"The key must contain only alphanumeric characters, dashes, and " +
				"underscores",
			Problem.Status.BAD_REQUEST, mustNotContainInvalidCharacters);
	}

}