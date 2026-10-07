/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {CreationModalContent} from '@liferay/layout-js-components-web';
import React from 'react';

export type AddDisplayPageTemplateFolderDesignLibraryModalContentProps = {
	addDisplayPageTemplateFolderURL: string;
	closeModal: () => void;
	namespace: string;
};

export default function AddDisplayPageTemplateFolderDesignLibraryModalContent({
	addDisplayPageTemplateFolderURL,
	closeModal,
	namespace,
}: AddDisplayPageTemplateFolderDesignLibraryModalContentProps) {
	return (
		<CreationModalContent
			buttonLabel={Liferay.Language.get('create')}
			closeModal={closeModal}
			formSubmitURL={addDisplayPageTemplateFolderURL}
			heading={Liferay.Language.get('new-folder')}
			portletNamespace={namespace}
		/>
	);
}
