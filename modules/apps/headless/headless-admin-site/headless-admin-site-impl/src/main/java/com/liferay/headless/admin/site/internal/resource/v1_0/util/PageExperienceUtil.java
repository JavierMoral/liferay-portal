/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0.util;

import com.liferay.headless.admin.site.dto.v1_0.PageExperience;
import com.liferay.headless.admin.site.internal.exception.DuplicatePageExperienceKeyException;
import com.liferay.headless.admin.site.internal.exception.PageExperienceException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.segments.constants.SegmentsExperienceConstants;
import com.liferay.segments.model.SegmentsExperience;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Mikel Lorza
 */
public class PageExperienceUtil {

	public static PageExperience getDefaultPageExperience(
			PageExperience[] pageExperiences)
		throws PageExperienceException {

		if (ArrayUtil.isEmpty(pageExperiences)) {
			throw new PageExperienceException(
				PageExperienceException.EXPERIENCE_REQUIRED,
				"No page experiences were provided");
		}

		for (PageExperience pageExperience : pageExperiences) {
			if (Objects.equals(
					pageExperience.getKey(),
					SegmentsExperienceConstants.KEY_DEFAULT)) {

				return pageExperience;
			}
		}

		throw new PageExperienceException(
			PageExperienceException.DEFAULT_EXPERIENCE_REQUIRED,
			"No default page experience was found");
	}

	public static void validatePageExperiences(
			SegmentsExperience defaultSegmentsExperience,
			PageExperience[] pageExperiences)
		throws PortalException {

		if (defaultSegmentsExperience == null) {
			throw new IllegalStateException(
				"The default segments experience does not exist");
		}

		if (ArrayUtil.isEmpty(pageExperiences)) {
			throw new PageExperienceException(
				PageExperienceException.EXPERIENCE_REQUIRED,
				"No page experiences were provided");
		}

		Set<String> pageExperienceKeys = new HashSet<>(pageExperiences.length);

		PageExperience defaultPageExperience = null;

		for (PageExperience pageExperience : pageExperiences) {
			if (!pageExperienceKeys.add(pageExperience.getKey())) {
				throw new DuplicatePageExperienceKeyException(
					pageExperience.getKey());
			}

			if (Objects.equals(
					pageExperience.getKey(),
					SegmentsExperienceConstants.KEY_DEFAULT)) {

				defaultPageExperience = pageExperience;
			}
		}

		if (defaultPageExperience == null) {
			throw new PageExperienceException(
				PageExperienceException.DEFAULT_EXPERIENCE_REQUIRED,
				"No default page experience was found");
		}

		if ((defaultPageExperience.getPriority() != null) &&
			(defaultPageExperience.getPriority() != 0)) {

			throw new PageExperienceException(
				PageExperienceException.INVALID_DEFAULT_PRIORITY,
				"The default page experience must have a priority of 0");
		}

		if (!StringUtil.equals(
				defaultSegmentsExperience.getExternalReferenceCode(),
				defaultPageExperience.getExternalReferenceCode())) {

			throw new PageExperienceException(
				PageExperienceException.MISMATCHED_EXTERNAL_REFERENCE_CODE,
				"The external reference code does not match the target page " +
					"experience external reference code");
		}

		if (defaultPageExperience.getSegmentItemExternalReference() != null) {
			throw new PageExperienceException(
				PageExperienceException.DEFAULT_REFERENCES_SEGMENT,
				"The default page experience cannot reference a segment");
		}
	}

}