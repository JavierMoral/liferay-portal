/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.validator;

import com.liferay.layout.exception.LayoutStructureException;
import com.liferay.layout.util.structure.LayoutStructure;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Validates content-structure invariants (the CS-* catalog) on a fully
 * assembled {@link LayoutStructure}. Implementations reference only higher-level
 * services so they can be referenced from any writer without affecting bundle
 * activation order.
 *
 * <p>
 * This is invoked from the structure-ingestion paths (export/import, staging,
 * page-definition import, and the headless importer), not from the interactive
 * editor or from every low-level persistence write. The default implementation
 * can be overridden by registering a higher-ranked service.
 * </p>
 *
 * @author Javier Moral
 */
@ProviderType
public interface LayoutStructureValidator {

	/**
	 * Validates the content-structure rules (allowed children, column count,
	 * fragment-instance uniqueness, and the form, collection, stepper, widget,
	 * and input placement invariants) on the assembled structure. These rules
	 * are page-type-independent, so a single walk covers every ingestion path.
	 * The drop-zone rule is not checked; use {@link #validate(LayoutStructure,
	 * boolean)} for a page whose type is known.
	 */
	public void validate(LayoutStructure layoutStructure)
		throws LayoutStructureException;

	/**
	 * Validates the structural rules plus the drop-zone rule for a page whose
	 * type is known. This is invoked from the client-driven authoring paths on
	 * the assembled structure: a master page must contain exactly one drop zone
	 * and a content page none. Because a content page built on a master inherits
	 * that master's drop zone, callers pass <code>false</code> only for a content
	 * page that has no master.
	 */
	public void validate(LayoutStructure layoutStructure, boolean masterPage)
		throws LayoutStructureException;

}