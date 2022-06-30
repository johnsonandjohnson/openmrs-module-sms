/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import { ToastStatusContent, CloseButton } from './toast-builder-util';
import { toast } from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
import '../../css/toast.scss';

const CLOSE_DELAY = 5000;
const TOAST_CLASS = 'toast-item';

export const successToast = message => displayToast(message, toast.TYPE.SUCCESS);

export const errorToast = message => displayToast(message, toast.TYPE.ERROR);

export const displayToast = (message, type) =>
  toast(
    <ToastStatusContent {... { message, type }} />,
    {
      autoClose: CLOSE_DELAY,
      closeButton: <CloseButton />,
      className: TOAST_CLASS,
      hideProgressBar: true
    });
