/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.internal.group.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.group.provider.SiteConnectedGroupGroupProvider;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Moral
 */
@RunWith(Arquillian.class)
public class SiteConnectedGroupGroupProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = _addGroup();
	}

	@FeatureFlag("LPD-57283")
	@Test
	public void testGetDesignLibraryConnectedSiteGroupIds() throws Exception {
		DepotEntry depotEntry = _addDepotEntry(
			DepotConstants.TYPE_DESIGN_LIBRARY);
		Group group = _addGroup();

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry.getDepotEntryId(), _group.getGroupId());
		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry.getDepotEntryId(), group.getGroupId());

		long[] expectedGroupIds = {_group.getGroupId(), group.getGroupId()};

		Arrays.sort(expectedGroupIds);

		long[] groupIds =
			_siteConnectedGroupGroupProvider.
				getDesignLibraryConnectedSiteGroupIds(depotEntry.getGroupId());

		Arrays.sort(groupIds);

		Assert.assertArrayEquals(expectedGroupIds, groupIds);
	}

	@FeatureFlag(enable = false, value = "LPD-57283")
	@Test
	public void testGetDesignLibraryConnectedSiteGroupIdsWhenFeatureFlagIsDisabled()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(
			DepotConstants.TYPE_DESIGN_LIBRARY);

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry.getDepotEntryId(), _group.getGroupId());

		Assert.assertArrayEquals(
			new long[0],
			_siteConnectedGroupGroupProvider.
				getDesignLibraryConnectedSiteGroupIds(depotEntry.getGroupId()));
	}

	@FeatureFlag("LPD-57283")
	@Test
	public void testGetDesignLibraryConnectedSiteGroupIdsWhenScopeIsNotADesignLibrary()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(
			DepotConstants.TYPE_ASSET_LIBRARY);

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry.getDepotEntryId(), _group.getGroupId());

		Assert.assertArrayEquals(
			new long[0],
			_siteConnectedGroupGroupProvider.
				getDesignLibraryConnectedSiteGroupIds(depotEntry.getGroupId()));

		Assert.assertArrayEquals(
			new long[0],
			_siteConnectedGroupGroupProvider.
				getDesignLibraryConnectedSiteGroupIds(_group.getGroupId()));
	}

	private DepotEntry _addDepotEntry(int type) throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			type, ServiceContextTestUtil.getServiceContext());

		_depotEntries.add(depotEntry);

		return depotEntry;
	}

	private Group _addGroup() throws Exception {
		Group group = GroupTestUtil.addGroup();

		_groups.add(group);

		return group;
	}

	@DeleteAfterTestRun
	private final List<DepotEntry> _depotEntries = new ArrayList<>();

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	private Group _group;

	@DeleteAfterTestRun
	private final List<Group> _groups = new ArrayList<>();

	@Inject
	private SiteConnectedGroupGroupProvider _siteConnectedGroupGroupProvider;

}