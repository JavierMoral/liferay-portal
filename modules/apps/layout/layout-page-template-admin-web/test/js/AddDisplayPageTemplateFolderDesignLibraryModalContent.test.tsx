/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {fetch, navigate} from 'frontend-js-web';
import React from 'react';

import AddDisplayPageTemplateFolderDesignLibraryModalContent from '../../src/main/resources/META-INF/resources/js/AddDisplayPageTemplateFolderDesignLibraryModalContent';

jest.mock('frontend-js-web', () => {
	const actual = jest.requireActual('frontend-js-web');

	return {
		...actual,
		fetch: jest.fn(),
		navigate: jest.fn(),
	};
});

const ADD_DISPLAY_PAGE_TEMPLATE_FOLDER_URL = '/add_display_page_collection';

const NAMESPACE = '_namespace_';

const closeModal = jest.fn();

const renderComponent = () =>
	render(
		<AddDisplayPageTemplateFolderDesignLibraryModalContent
			addDisplayPageTemplateFolderURL={
				ADD_DISPLAY_PAGE_TEMPLATE_FOLDER_URL
			}
			closeModal={closeModal}
			namespace={NAMESPACE}
		/>
	);

describe('AddDisplayPageTemplateFolderDesignLibraryModalContent', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('creates a display page template folder', async () => {
		(fetch as jest.Mock).mockResolvedValue({
			json: () =>
				Promise.resolve({redirectURL: '/display_page_templates'}),
		});

		const {container} = renderComponent();

		expect(screen.getByText('new-folder')).toBeInTheDocument();

		await userEvent.type(
			container.querySelector(`#${NAMESPACE}name`)!,
			'Folder 1'
		);
		await userEvent.type(
			container.querySelector(`#${NAMESPACE}description`)!,
			'Description 1'
		);

		await userEvent.click(screen.getByText('create'));

		await waitFor(() => expect(closeModal).toHaveBeenCalledTimes(1));

		const [url, {body}] = (fetch as jest.Mock).mock.calls[0];

		expect(url).toBe(ADD_DISPLAY_PAGE_TEMPLATE_FOLDER_URL);
		expect(Object.fromEntries(body.entries())).toEqual({
			[`${NAMESPACE}description`]: 'Description 1',
			[`${NAMESPACE}name`]: 'Folder 1',
		});

		expect(navigate).toHaveBeenCalledWith('/display_page_templates');
	});

	it('does not create a display page template folder without a name', async () => {
		renderComponent();

		await userEvent.click(screen.getByText('create'));

		expect(screen.getByText('this-field-is-required')).toBeInTheDocument();
		expect(closeModal).not.toHaveBeenCalled();
		expect(fetch).not.toHaveBeenCalled();
	});

	it('keeps the modal open when the folder name is rejected', async () => {
		(fetch as jest.Mock).mockResolvedValue({
			json: () =>
				Promise.resolve({error: 'please-enter-a-unique-folder-name'}),
		});

		const {container} = renderComponent();

		await userEvent.type(
			container.querySelector(`#${NAMESPACE}name`)!,
			'Folder 1'
		);

		await userEvent.click(screen.getByText('create'));

		expect(
			await screen.findByText('please-enter-a-unique-folder-name')
		).toBeInTheDocument();

		expect(closeModal).not.toHaveBeenCalled();
		expect(navigate).not.toHaveBeenCalled();
	});
});
