/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import {openToast} from 'frontend-js-components-web';
import {addParams, fetch, navigate} from 'frontend-js-web';
import React, {useCallback, useRef, useState} from 'react';

import ContentTypeModalForm from './components/ContentTypeModalForm';
import {MODAL_TYPES} from './constants/modalTypes';
import {MappingType} from './types/MappingTypes';
import {ValidationError} from './types/ValidationError';

interface Props {
	closeModal: () => void;
	formSubmitURL: string;
	mappingTypes: MappingType[];
	namespace: string;
}

export default function DesignLibraryAddDisplayPageTemplate({
	closeModal,
	formSubmitURL,
	mappingTypes,
	namespace,
}: Props) {
	const [error, setError] = useState<ValidationError>({});
	const [submitting, setSubmitting] = useState(false);

	const formRef = useRef<HTMLFormElement>(null);

	const handleSubmit = useCallback(
		(event: React.FormEvent<HTMLFormElement>) => {
			event.preventDefault();

			if (!formRef.current) {
				return;
			}

			setSubmitting(true);

			fetch(formSubmitURL, {
				body: new FormData(formRef.current),
				method: 'POST',
			})
				.then((response) => response.json())
				.then(
					(
						responseError: ValidationError & {redirectURL?: string}
					) => {
						const {redirectURL, ...validationError} = responseError;

						if (Object.keys(validationError).length) {
							setError(validationError);
							setSubmitting(false);

							return;
						}

						closeModal();

						navigate(
							redirectURL
								? addParams(
										{
											[`${namespace}redirect`]:
												location.href,
										},
										redirectURL
									)
								: location.href
						);
					}
				)
				.catch(() => {
					setSubmitting(false);

					openToast({
						message: Liferay.Language.get(
							'an-unexpected-error-occurred'
						),
						type: 'danger',
					});
				});
		},
		[closeModal, formSubmitURL, namespace]
	);

	return (
		<>
			<ClayModal.Header>
				{Liferay.Language.get('new-display-page-template')}
			</ClayModal.Header>

			<ClayModal.Body>
				<ContentTypeModalForm
					displayPageName=""
					error={error}
					formRef={formRef}
					mappingTypes={mappingTypes}
					namespace={namespace}
					onSubmit={handleSubmit}
					type={MODAL_TYPES.create}
				/>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={closeModal}
							type="button"
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							disabled={submitting}
							onClick={() =>
								formRef.current?.requestSubmit
									? formRef.current.requestSubmit()
									: formRef.current?.dispatchEvent(
											new Event('submit', {
												bubbles: true,
												cancelable: true,
											})
										)
							}
							type="button"
						>
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
}
