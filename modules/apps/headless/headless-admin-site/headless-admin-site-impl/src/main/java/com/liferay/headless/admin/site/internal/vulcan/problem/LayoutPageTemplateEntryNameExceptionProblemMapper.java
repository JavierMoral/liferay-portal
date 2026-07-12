/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.vulcan.problem;

import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.exception.LayoutPageTemplateEntryNameException;
import com.liferay.portal.vulcan.problem.Problem;
import com.liferay.portal.vulcan.problem.ProblemMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Javier Moral
 */
@Component(service = ProblemMapper.class)
public class LayoutPageTemplateEntryNameExceptionProblemMapper
	implements ProblemMapper
		<LayoutPageTemplateEntryNameException.MustNotBeDuplicate> {

	@Override
	public Problem getProblem(
		LayoutPageTemplateEntryNameException.MustNotBeDuplicate
			mustNotBeDuplicate) {

		String name = "page template";

		if (mustNotBeDuplicate.getType() ==
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE) {

			name = "display page template";
		}
		else if (mustNotBeDuplicate.getType() ==
					LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT) {

			name = "master page";
		}

		return ProblemUtil.getProblem(
			"A " + name + " with the same name already exists",
			Problem.Status.CONFLICT, mustNotBeDuplicate);
	}

}