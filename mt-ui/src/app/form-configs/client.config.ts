import { IForm } from 'mt-form-builder/lib/classes/template.interface';
import { CLIENT_TYPE } from '../clazz/validation/aggregate/client/interfaze-client';
import { GRANT_TYPE_LIST } from '../clazz/validation/constant';

export const FORM_CONFIG: IForm = {
    "repeatable": false,
    "inputs": [
        {
            "type": "text",
            "display": false,
            "label": "ID",
            "key": "id",
        },
        {
            "type": "text",
            "display": false,
            "label": "",
            "key": "projectId",
        },
        {
            "type": "text",
            "display": true,
            "label": "ENTER_NAME",
            "key": "name",
            required: true,
        },
        {
            "type": "text",
            "display": true,
            "label": "ENTER_DESCRIPTION",
            "key": "description",
        },
        {
            "type": "radio",
            "display": true,
            "label": "SELECT_APP_TYPE_1",
            "key": "frontOrBackApp",
            direction:'column',
            "options": [
                { label: 'YES', value: 'FRONTEND_APP'},
                { label: 'NO', value: 'BACKEND_APP'},
            ],
            required: true,
        },
        {
            "type": "text",
            "display": false,
            "label": "ENTER_PATH",
            "key": "path",
            required: true,
        },
        {
            "type": "text",
            "display": false,
            "label": "ENTER_EXTERNAL_URL",
            "key": "externalUrl",
            required: true,
        },
        {
            "type": "text",
            "display": false,
            "label": "ENTER_CLIENT_SECRET",
            "key": "clientSecret",
            required: true,
        },
        {
            "type": "select",
            "display": false,
            "label": "SELECT_A_GRANT_TYPE",
            "key": "grantType",
            "options": GRANT_TYPE_LIST.filter(e => e.value !== 'AUTHORIZATION_CODE'),
            required: true,
        },
        {
            "type": "text",
            "display": false,
            "label": "ENTER_REDIRECT_URI",
            "key": "registeredRedirectUri",
            required: true,
        },
        {
            "type": "checkbox",
            "display": false,
            "label": "",
            "key": "refreshToken",
            "options": [
                { label: 'ADD_REFRESH_TOKEN', value: "Add Refresh Token" },
            ]
        },
        {
            "type": "checkbox",
            "display": false,
            "label": "",
            "key": "resourceIndicator",
            "options": [
                { label: 'SET_AS_RESOURCE', value: "Set As Resource" },
            ],
        },
        {
            "type": "checkbox",
            "display": false,
            "label": "",
            "key": "autoApprove",
            "options": [
                { label: 'AUTO_APPROVE_AUTHORIZE_REQUEST', value: "Auto approve authorize request" },
            ],
        },
        {
            "type": "paginated-select",
            "display": false,
            "multiple": true,
            "label": "PLEASE_SELECT_RESOUCE_ID(S)",
            "key": "resourceId",
            "options": [
            ],
        },
        {
            "type": "text",
            "display": false,
            "label": "ACCESS_TOKEN_VALIDITY_SECONDS",
            "key": "accessTokenValiditySeconds",
            required: true,
        },
        {
            "type": "text",
            "display": false,
            "label": "REFRESH_TOKEN_VALIDITY_SECONDS",
            "key": "refreshTokenValiditySeconds",
            required: true,
        },
    ],
}
