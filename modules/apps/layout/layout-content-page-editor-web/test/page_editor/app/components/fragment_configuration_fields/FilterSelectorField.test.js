/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, render, screen, waitFor} from '@testing-library/react';
import React from 'react';

import {FilterSelectorField} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/components/fragment_configuration_fields/FilterSelectorField';
import {FREEMARKER_FRAGMENT_ENTRY_PROCESSOR} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/freemarkerFragmentEntryProcessor';
import {StoreContextProvider} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import CollectionService from '../../../../../src/main/resources/META-INF/resources/page_editor/app/services/CollectionService';

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/page_editor/app/config/index',
	() => ({
		config: {
			getCollectionFiltersURL: '/collection-filters',
			getCollectionSupportedFiltersURL: '/collection-supported-filters',
			portletNamespace: 'pageEditor',
		},
	})
);

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/page_editor/app/services/CollectionService',
	() => ({
		__esModule: true,
		default: {
			getCollectionFilters: jest.fn(),
			getCollectionSupportedFilters: jest.fn(),
		},
	})
);

const COLLECTION_FILTERS = {
	category: {key: 'category', label: 'Category'},
	keywords: {key: 'keywords', label: 'Keywords'},
	tags: {key: 'tags', label: 'Tags'},
};

const DEFAULT_FIELD = {
	label: 'Filter',
	name: 'filterKey',
	type: 'filterSelector',
};

const DEFAULT_ITEM = {
	config: {
		fragmentEntryLinkId: 'fragment-entry-link-1',
	},
};

const FRAGMENT_ENTRY_LINK_ID = 'fragment-entry-link-1';

function _buildState({targetCollections = []} = {}) {
	return {
		fragmentEntryLinks: {
			[FRAGMENT_ENTRY_LINK_ID]: {
				editableValues: {
					[FREEMARKER_FRAGMENT_ENTRY_PROCESSOR]: {
						targetCollections,
					},
				},
			},
		},
		layoutData: {
			deletedItems: [],
			items: {
				'collection-display-a': {
					children: [],
					config: {
						collection: {title: 'Collection A'},
					},
					itemId: 'collection-display-a',
					type: 'collection',
				},
				'collection-display-b': {
					children: [],
					config: {
						collection: {title: 'Collection B'},
					},
					itemId: 'collection-display-b',
					type: 'collection',
				},
			},
		},
	};
}

function _renderComponent({
	item = DEFAULT_ITEM,
	onValueSelect = jest.fn(),
	state,
	value = '',
} = {}) {
	return render(
		<StoreContextProvider
			initialState={state}
			reducer={(state) => state}
		>
			<FilterSelectorField
				field={DEFAULT_FIELD}
				item={item}
				onValueSelect={onValueSelect}
				value={value}
			/>
		</StoreContextProvider>
	);
}

describe('FilterSelectorField', () => {
	beforeEach(() => {
		CollectionService.getCollectionFilters.mockResolvedValue(
			COLLECTION_FILTERS
		);

		CollectionService.getCollectionSupportedFilters.mockResolvedValue({
			'collection-display-a': ['category', 'keywords'],
			'collection-display-b': ['category', 'tags'],
		});
	});

	afterEach(cleanup);

	it('renders nothing when no target collections are selected', () => {
		const state = _buildState({targetCollections: []});

		const {container} = _renderComponent({state});

		expect(container).toBeEmptyDOMElement();
	});

	it('renders a select with supported filters', async () => {
		const state = _buildState({
			targetCollections: ['collection-display-a'],
		});

		_renderComponent({state});

		await waitFor(() => {
			expect(screen.getByRole('combobox')).toBeInTheDocument();
		});

		expect(screen.getByText('Category')).toBeInTheDocument();
		expect(screen.getByText('Keywords')).toBeInTheDocument();
		expect(screen.getByText('none')).toBeInTheDocument();
	});

	it('renders only filters supported by all target collections', async () => {
		const state = _buildState({
			targetCollections: [
				'collection-display-a',
				'collection-display-b',
			],
		});

		_renderComponent({state});

		await waitFor(() => {
			expect(screen.getByText('Category')).toBeInTheDocument();
		});

		expect(screen.queryByText('Keywords')).not.toBeInTheDocument();
		expect(screen.queryByText('Tags')).not.toBeInTheDocument();
	});

	it('clears filter value when target collections become empty', async () => {
		const onValueSelect = jest.fn();

		const state = _buildState({targetCollections: []});

		_renderComponent({
			onValueSelect,
			state,
			value: 'category',
		});

		await waitFor(() => {
			expect(onValueSelect).toHaveBeenCalledWith('filterKey', '');
		});
	});

	it('clears filter value when selected filter is no longer supported', async () => {
		const onValueSelect = jest.fn();

		const state = _buildState({
			targetCollections: [
				'collection-display-a',
				'collection-display-b',
			],
		});

		_renderComponent({
			onValueSelect,
			state,
			value: 'keywords',
		});

		await waitFor(() => {
			expect(onValueSelect).toHaveBeenCalledWith('filterKey', '');
		});
	});

	it('does not clear filter value when selected filter is still supported', async () => {
		const onValueSelect = jest.fn();

		const state = _buildState({
			targetCollections: [
				'collection-display-a',
				'collection-display-b',
			],
		});

		_renderComponent({
			onValueSelect,
			state,
			value: 'category',
		});

		await waitFor(() => {
			expect(screen.getByText('Category')).toBeInTheDocument();
		});

		expect(onValueSelect).not.toHaveBeenCalled();
	});
});
