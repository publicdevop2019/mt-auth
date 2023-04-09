import { GRANT_TYPE_LIST_EXT } from '../../constant';
import { CLIENT_TYPE, grantTypeEnums, IClient } from './interfaze-client';
import { BooleanValidator, descriptionValidator, ErrorMessage, IAggregateValidator, ListValidator, NumberValidator, StringValidator, TPlatform, TValidator } from '../../validator-common';

export class ClientValidator extends IAggregateValidator {
    private rootCreateClientCommandValidator: Map<string, TValidator> = new Map();
    private rootUpdateClientCommandValidator: Map<string, TValidator> = new Map();
    constructor(platform?: TPlatform) {
        super(platform);
        this.rootCreateClientCommandValidator.set('name', this.clientNameValidator);
        this.rootCreateClientCommandValidator.set('description', descriptionValidator);
        this.rootCreateClientCommandValidator.set('hasSecret', this.clientHasSecretValidator);
        this.rootCreateClientCommandValidator.set('grantTypeEnums', this.clientGrantTypeValidator);
        this.rootCreateClientCommandValidator.set('resourceIndicator', this.clientResourceIndicatorValidator);
        this.rootCreateClientCommandValidator.set('resourceIds', this.clientResourceIdValidator);
        this.rootCreateClientCommandValidator.set('accessTokenValiditySeconds', this.clientAccessTokenValiditySecondsValidator);
        this.rootCreateClientCommandValidator.set('refreshTokenValiditySeconds', this.clientRefreshTokenValiditySecondsValidator);
        this.rootCreateClientCommandValidator.set('registeredRedirectUri', this.clientRegisteredRedirectUriValidator);
        this.rootCreateClientCommandValidator.set('autoApprove', this.clientAutoApproveValidator);

        this.rootUpdateClientCommandValidator.set('name', this.clientNameValidator);
        this.rootUpdateClientCommandValidator.set('description', descriptionValidator);
        this.rootUpdateClientCommandValidator.set('hasSecret', this.clientHasSecretValidator);
        this.rootUpdateClientCommandValidator.set('grantTypeEnums', this.clientGrantTypeValidator);
        this.rootUpdateClientCommandValidator.set('resourceIndicator', this.clientResourceIndicatorValidator);
        this.rootUpdateClientCommandValidator.set('resourceIds', this.clientResourceIdValidator);
        this.rootUpdateClientCommandValidator.set('accessTokenValiditySeconds', this.clientAccessTokenValiditySecondsValidator);
        this.rootUpdateClientCommandValidator.set('refreshTokenValiditySeconds', this.clientRefreshTokenValiditySecondsValidator);
        this.rootUpdateClientCommandValidator.set('registeredRedirectUri', this.clientRegisteredRedirectUriValidator);
        this.rootUpdateClientCommandValidator.set('autoApprove', this.clientAutoApproveValidator);
    }
    public validate(payload: IClient, context: string): ErrorMessage[] {
        if (context === 'rootCreateClientCommandValidator')
            return this.validationWPlatform(payload, this.rootCreateClientCommandValidator)
        if (context === 'rootUpdateClientCommandValidator')
            return this.validationWPlatform(payload, this.rootUpdateClientCommandValidator)
    }
    clientAutoApproveValidator = (key: string, payload: IClient) => {
        let results: ErrorMessage[] = [];
        if (payload.grantTypeEnums) {
            if (payload.grantTypeEnums.includes(grantTypeEnums.authorization_code)) {
                BooleanValidator.isBoolean(payload[key], results, key);
            } else {
                if (payload[key] === null) {
                } else {
                    results.push({ type: 'autoApproveRequiresAuthorizationCodeGrant', message: 'AUTO_APPROVE_REQUIRES_AUTHORIZATION_CODE_GRANT', key: key })
                }
            }
        } else {
            results.push({ type: 'noGrantTypeEnumsFoundForAutoApprove', message: 'NO_GRANT_TYPE_ENUMS_FOUND_FOR_AUTO_APPROVE', key: key })
        }
        return results
    }
    clientRegisteredRedirectUriValidator = (key: string, payload: IClient) => {
        let results: ErrorMessage[] = [];
        if (payload.grantTypeEnums) {
            if (payload.grantTypeEnums.includes(grantTypeEnums.authorization_code)) {
                ListValidator.hasValue(payload[key], results, key);
            } else {
                if (payload[key]) {
                    results.push({ type: 'redirectUriRequiresAuthorizationCodeGrant', message: 'REDIRECT_URI_REQUIRES_AUTHORIZATION_CODE_GRANT', key: key })
                } else {
                }
            }
        } else {
            results.push({ type: 'noGrantTypeEnumsFoundForRedirectUri', message: 'NO_GRANT_TYPE_ENUMS_FOUND_FOR_REDIRECT_URI', key: key })
        }
        return results
    }
    clientNameValidator = (key: string, payload: IClient) => {
        let results: ErrorMessage[] = [];
        StringValidator.hasValidWhiteListValue(payload[key], results, key)
        StringValidator.lessThanOrEqualTo(payload[key], 50, results, key);
        StringValidator.greaterThanOrEqualTo(payload[key], 1, results, key);
        return results
    }
    clientAccessTokenValiditySecondsValidator = (key: string, payload: IClient) => {
        let results: ErrorMessage[] = [];
        NumberValidator.isNumber(payload[key], results, key);
        NumberValidator.greaterThanOrEqualTo(payload[key], 60, results, key);
        return results
    }
    clientRefreshTokenValiditySecondsValidator = (key: string, payload: IClient) => {
        let results: ErrorMessage[] = [];
        if (payload.grantTypeEnums) {
            if (payload.grantTypeEnums.includes(grantTypeEnums.password) && payload.grantTypeEnums.includes(grantTypeEnums.refresh_token)) {
                NumberValidator.isNumber(payload[key], results, key);
                NumberValidator.greaterThanOrEqualTo(payload[key], 120, results, key);
            } else {
                if (payload[key] > 0) {
                    results.push({ type: 'requiresRefreshTokenAndPasswordGrant', message: 'REQUIRES_REFRESH_TOKEN_AND_PASSWORD_GRANT', key: key })
                } else {

                }
            }
        } else {
            results.push({ type: 'noGrantTypeEnumsFoundForRefreshToken', message: 'NO_GRANT_TYPE_ENUMS_FOUND_FOR_REFRESH_TOKEN', key: key })
        }
        return results
    }
    clientResourceIdValidator = (key: string, payload: IClient) => {
        let results: ErrorMessage[] = [];
        if (ListValidator.hasValue(payload[key], results, key)) {
            (payload[key] as string[]).forEach((e, index) => {
                StringValidator.hasValidWhiteListValue(e, results, index + "_" + key)
            })
        } else {
            results = [];
        }
        return results
    }
    clientResourceIndicatorValidator = (key: string, payload: IClient) => {
        let results: ErrorMessage[] = [];
        BooleanValidator.isBoolean(payload[key], results, key);
        if (payload[key] === true) {
            let var0 = [CLIENT_TYPE.backend_app];
            if (var0.some(e => !payload.types.includes(e))) {
                results.push({ type: 'resourceIndicatorRequiresRole', message: 'RESOURCE_INDICATOR_REQUIRES_ROLE', key: key })
            }
        }
        return results
    }
    clientGrantTypeValidator = (key: string, payload: IClient) => {
        let results: ErrorMessage[] = [];
        let value = payload[key];
        ListValidator.hasValue(value, results, key);
        ListValidator.isSubListOf(value, GRANT_TYPE_LIST_EXT.map(e => e.value), results, key);
        // can only be one of the below cases
        // password
        // password + refresh_token
        // client_credentials
        // authorization_code
        if (Array.isArray(value)) {

            if (value.length === 1 && value[0] === grantTypeEnums.password) { }
            else if (value.length === 1 && value[0] === grantTypeEnums.client_credentials) { }
            else if (value.length === 1 && value[0] === grantTypeEnums.authorization_code) { }
            else if (value.length === 2 && value.includes(grantTypeEnums.password) && value.includes(grantTypeEnums.refresh_token)) { }
            else {
                results.push({ type: 'invalidGrantTypeValue', message: 'INVALID_GRANT_TYPE_VALUE', key: key })
            }
        } else {
            results.push({ type: 'grantTypeNotArray', message: 'GRANT_TYPE_NOT_ARRAY', key: key })
        }
        return results
    }
    clientTypeValidator = (key: string, payload: IClient) => {
        let results: ErrorMessage[] = [];
        let value = payload[key];
        ListValidator.hasValue(value, results, key);
        ListValidator.isSubListOf(value, Object.values(CLIENT_TYPE), results, key);
        return results
    }
    clientHasSecretValidator = (key: string, payload: IClient) => {
        let results: ErrorMessage[] = [];
        BooleanValidator.isBoolean(payload[key], results, key)
        return results
    }
}

