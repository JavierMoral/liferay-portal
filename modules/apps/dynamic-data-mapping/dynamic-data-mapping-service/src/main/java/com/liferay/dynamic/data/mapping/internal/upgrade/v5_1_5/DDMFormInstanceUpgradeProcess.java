/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v5_1_5;

import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Javier Moral
 */
public class DDMFormInstanceUpgradeProcess extends UpgradeProcess {

	public DDMFormInstanceUpgradeProcess(
		ResourcePermissionLocalService resourcePermissionLocalService,
		RoleLocalService roleLocalService) {

		_resourcePermissionLocalService = resourcePermissionLocalService;
		_roleLocalService = roleLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement selectPreparedStatement =
				connection.prepareStatement(
					"select companyid, forminstanceid from DDMFormInstance");
			ResultSet resultSet = selectPreparedStatement.executeQuery()) {

			while (resultSet.next()) {
				long companyId = resultSet.getLong(1);

				long primKey = resultSet.getLong(2);

				Role guestRole = _roleLocalService.getRole(
					companyId, RoleConstants.GUEST);

				ResourcePermission resourcePermission =
					_resourcePermissionLocalService.getResourcePermission(
						companyId, _CLASS_NAME_FORM_INSTANCE,
						ResourceConstants.SCOPE_INDIVIDUAL,
						String.valueOf(primKey), guestRole.getRoleId());

				if (!resourcePermission.hasActionId(ActionKeys.VIEW)) {
					return;
				}

				_resourcePermissionLocalService.removeResourcePermission(
					companyId, _CLASS_NAME_FORM_INSTANCE,
					ResourceConstants.SCOPE_INDIVIDUAL, String.valueOf(primKey),
					guestRole.getRoleId(), ActionKeys.VIEW);
			}
		}
	}

	private static final String _CLASS_NAME_FORM_INSTANCE =
		"com.liferay.dynamic.data.mapping.model.DDMFormInstance";

	private final ResourcePermissionLocalService
		_resourcePermissionLocalService;
	private final RoleLocalService _roleLocalService;

}