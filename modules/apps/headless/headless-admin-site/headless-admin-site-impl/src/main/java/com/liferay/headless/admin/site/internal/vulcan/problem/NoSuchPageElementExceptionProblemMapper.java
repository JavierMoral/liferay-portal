/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.vulcan.problem;

import com.liferay.headless.admin.site.internal.exception.NoSuchPageElementException;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.problem.Problem;
import com.liferay.portal.vulcan.problem.ProblemMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Rubén Pulido
 */
@Component(service = ProblemMapper.class)
public class NoSuchPageElementExceptionProblemMapper
	implements ProblemMapper<NoSuchPageElementException> {

	@Override
	public Problem getProblem(
		NoSuchPageElementException noSuchPageElementException) {

		String title = "The page element could not be found";

		String detail = title;

		String externalReferenceCode =
			noSuchPageElementException.getExternalReferenceCode();

		if (Validator.isNotNull(externalReferenceCode)) {
			detail = StringBundler.concat(
				"No page element with external reference code ",
				externalReferenceCode, " exists in this page experience");
		}

		return ProblemUtil.getProblem(
			detail, Problem.Status.NOT_FOUND, title, "page-element-not-found",
			noSuchPageElementException);
	}

}