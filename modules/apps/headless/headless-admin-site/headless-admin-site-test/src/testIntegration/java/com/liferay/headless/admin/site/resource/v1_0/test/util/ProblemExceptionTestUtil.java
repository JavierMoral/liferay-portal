/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test.util;

import com.liferay.headless.admin.site.client.problem.Problem;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.kernel.util.Validator;

import org.junit.Assert;

/**
 * @author Javier Moral
 */
public class ProblemExceptionTestUtil {

	public static void assertProblemException(
			String expectedDetail, String expectedStatus, String expectedTitle,
			String expectedType, UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try {
			unsafeRunnable.run();

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			if (Validator.isNotNull(expectedDetail)) {
				Assert.assertEquals(expectedDetail, problem.getDetail());
			}
			else if (problem.getDetail() != null) {
				Assert.assertEquals(expectedTitle, problem.getDetail());
			}

			if (expectedType != null) {
				Assert.assertEquals(expectedType, problem.getType());
			}

			Assert.assertEquals(expectedStatus, problem.getStatus());
			Assert.assertEquals(expectedTitle, problem.getTitle());
		}
	}

	public static void assertProblemException(
			String expectedDetail, String expectedStatus, String expectedTitle,
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		assertProblemException(
			expectedDetail, expectedStatus, expectedTitle, null,
			unsafeRunnable);
	}

	public static void assertProblemException(
			String expectedStatus, String expectedTitle,
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		assertProblemException(
			null, expectedStatus, expectedTitle, null, unsafeRunnable);
	}

}