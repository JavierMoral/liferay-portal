/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.collection.filter.tags;

import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.fragment.collection.filter.FragmentCollectionFilter;
import com.liferay.fragment.collection.filter.constants.FragmentCollectionFilterConstants;
import com.liferay.fragment.collection.filter.tags.display.context.FragmentCollectionFilterTagsDisplayContext;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pablo Molina
 */
@Component(service = FragmentCollectionFilter.class)
public class FragmentCollectionFilterTags implements FragmentCollectionFilter {

	@Override
	public String getFilterKey() {
		return "tags";
	}

	@Override
	public String getFilterValueLabel(String filterValue, Locale locale) {
		AssetTag assetTag = _assetTagLocalService.fetchAssetTag(
			GetterUtil.getLong(filterValue));

		if (assetTag == null) {
			return filterValue;
		}

		return assetTag.getName();
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "tags");
	}

	@Override
	public void render(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			httpServletRequest.setAttribute(
				FragmentCollectionFilterTagsDisplayContext.class.getName(),
				new FragmentCollectionFilterTagsDisplayContext(
					(JSONObject)httpServletRequest.getAttribute(
						FragmentCollectionFilterConstants.
							CONFIGURATION_JSON_OBJECT_KEY),
					_fragmentEntryConfigurationParser, fragmentRendererContext,
					httpServletRequest));

			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher("/page.jsp");

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to render collection filter fragment", exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FragmentCollectionFilterTags.class);

	@Reference
	private AssetTagLocalService _assetTagLocalService;

	@Reference
	private FragmentEntryConfigurationParser _fragmentEntryConfigurationParser;

	@Reference
	private Language _language;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.fragment.collection.filter.tags)"
	)
	private ServletContext _servletContext;

}
