/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.vulcan.problem;

import com.liferay.headless.admin.site.internal.exception.DuplicateUuidException;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.problem.Problem;
import com.liferay.portal.vulcan.problem.ProblemMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Javier Moral
 */
@Component(service = ProblemMapper.class)
public class DuplicateUuidExceptionProblemMapper
	implements ProblemMapper<DuplicateUuidException> {

	@Override
	public Problem getProblem(DuplicateUuidException duplicateUuidException) {
		String uuid = duplicateUuidException.getUuid();

		String message = "This UUID is already in use";

		if (Validator.isNotNull(uuid)) {
			message = "This UUID is already in use: " + uuid;
		}

		return ProblemUtil.getProblem(
			message, Problem.Status.CONFLICT, duplicateUuidException);
	}

}