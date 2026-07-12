/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.vulcan.problem;

import com.liferay.layout.utility.page.exception.LayoutUtilityPageEntryNameException;
import com.liferay.portal.vulcan.problem.Problem;
import com.liferay.portal.vulcan.problem.ProblemMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Javier Moral
 */
@Component(service = ProblemMapper.class)
public class LayoutUtilityPageEntryNameRequiredExceptionProblemMapper
	implements ProblemMapper
		<LayoutUtilityPageEntryNameException.MustNotBeNull> {

	@Override
	public Problem getProblem(
		LayoutUtilityPageEntryNameException.MustNotBeNull mustNotBeNull) {

		return ProblemUtil.getProblem(
			"A name is required", Problem.Status.BAD_REQUEST, mustNotBeNull);
	}

}