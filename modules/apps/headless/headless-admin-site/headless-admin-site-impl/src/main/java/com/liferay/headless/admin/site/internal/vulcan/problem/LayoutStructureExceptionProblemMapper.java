/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.vulcan.problem;

import com.liferay.layout.exception.LayoutStructureException;
import com.liferay.portal.vulcan.problem.Problem;
import com.liferay.portal.vulcan.problem.ProblemMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Javier Moral
 */
@Component(service = ProblemMapper.class)
public class LayoutStructureExceptionProblemMapper
	implements ProblemMapper<LayoutStructureException> {

	@Override
	public Problem getProblem(
		LayoutStructureException layoutStructureException) {

		return ProblemUtil.getProblem(
			layoutStructureException.getMessage(), Problem.Status.BAD_REQUEST,
			layoutStructureException);
	}

}