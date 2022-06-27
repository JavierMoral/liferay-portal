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

import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.util.List;

/**
 * @author Javier Moral
 */
public class DDMFormInstanceUpgradeProcess extends UpgradeProcess {

	public DDMFormInstanceUpgradeProcess(
		DDMFormInstanceLocalService ddmFormInstanceLocalService,
		ResourcePermissionLocalService resourcePermissionLocalService,
		RoleLocalService roleLocalService) {

		_ddmFormInstanceLocalService = ddmFormInstanceLocalService;
		_resourcePermissionLocalService = resourcePermissionLocalService;
		_roleLocalService = roleLocalService;
	}

	@Override
	protected void doUpgrade() throws PortalException {
		_removeGuestViewPermissions();
	}

	private void _removeGuestViewPermissions() throws PortalException {
		int windowSize = 100;
		int start = 0;
		int end = windowSize;
		int count = windowSize;

		while (count == windowSize) {
			List<DDMFormInstance> ddmFormInstances =
				_ddmFormInstanceLocalService.getDDMFormInstances(start, end);
			start = end;
			end += windowSize;
			count = ddmFormInstances.size();

			_removePermissions(ddmFormInstances);
		}
	}

	private void _removePermission(DDMFormInstance ddmFormInstance)
		throws PortalException {

		long companyId = ddmFormInstance.getCompanyId();
		String primKey = String.valueOf(ddmFormInstance.getPrimaryKey());
		String name = "com.liferay.dynamic.data.mapping.model.DDMFormInstance";

		Role guestRole = _roleLocalService.getRole(
			companyId, RoleConstants.GUEST);

		String viewActionId = "VIEW";

		ResourcePermission resourcePermission =
			_resourcePermissionLocalService.getResourcePermission(
				companyId, name, ResourceConstants.SCOPE_INDIVIDUAL, primKey,
				guestRole.getRoleId());

		if (!resourcePermission.hasActionId(viewActionId)) {
			return;
		}

		_resourcePermissionLocalService.removeResourcePermission(
			companyId, name, ResourceConstants.SCOPE_INDIVIDUAL, primKey,
			guestRole.getRoleId(), viewActionId);
	}

	private void _removePermissions(List<DDMFormInstance> ddmFormInstances)
		throws PortalException {

		for (DDMFormInstance ddmFormInstance : ddmFormInstances) {
			_removePermission(ddmFormInstance);
		}
	}

	private final DDMFormInstanceLocalService _ddmFormInstanceLocalService;
	private final ResourcePermissionLocalService
		_resourcePermissionLocalService;
	private final RoleLocalService _roleLocalService;

}