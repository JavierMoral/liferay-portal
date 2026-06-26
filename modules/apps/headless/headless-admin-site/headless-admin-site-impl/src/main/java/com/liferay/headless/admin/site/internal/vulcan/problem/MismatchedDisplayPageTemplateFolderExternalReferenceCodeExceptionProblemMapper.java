/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.vulcan.problem;

import com.liferay.headless.admin.site.internal.exception.MismatchedDisplayPageTemplateFolderExternalReferenceCodeException;
import com.liferay.portal.vulcan.problem.Problem;
import com.liferay.portal.vulcan.problem.ProblemMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Javier Moral
 */
@Component(service = ProblemMapper.class)
public class
	MismatchedDisplayPageTemplateFolderExternalReferenceCodeExceptionProblemMapper
		implements ProblemMapper
			<MismatchedDisplayPageTemplateFolderExternalReferenceCodeException> {

	@Override
	public Problem getProblem(
		MismatchedDisplayPageTemplateFolderExternalReferenceCodeException
			mismatchedDisplayPageTemplateFolderExternalReferenceCodeException) {

		return ProblemUtil.getProblem(
			"The parent display page template folder external reference " +
				"codes do not match",
			Problem.Status.BAD_REQUEST,
			"parent-display-page-template-folder-external-reference-codes-do-" +
				"not-match",
			mismatchedDisplayPageTemplateFolderExternalReferenceCodeException);
	}

}