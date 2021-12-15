import { BooleanValidator, ErrorMessage, IAggregateValidator, ListValidator, NumberValidator, StringValidator, TPlatform, TValidator } from '../../validator-common';
import { IForgetPasswordRequest, IPendingResourceOwner, IResourceOwner, IResourceOwnerUpdatePwd } from './interfaze-user';

export class UserValidator extends IAggregateValidator {
    private appCreatePendingUserCommandValidator: Map<string, TValidator> = new Map();
    private adminUpdateUserCommandValidator: Map<string, TValidator> = new Map();
    private appCreateUserCommandValidator: Map<string, TValidator> = new Map();
    private appForgetUserPasswordCommandValidator: Map<string, TValidator> = new Map();
    private appResetUserPasswordCommandValidator: Map<string, TValidator> = new Map();
    private userUpdatePwdCommandValidator: Map<string, TValidator> = new Map();
    constructor(platform?: TPlatform) {
        super(platform)
        this.userUpdatePwdCommandValidator.set('password', this.passwordValidator);
        this.userUpdatePwdCommandValidator.set('currentPwd', this.currentPwdValidator);

        this.appCreatePendingUserCommandValidator.set('email', this.emailValidator);

        this.appCreateUserCommandValidator.set('email', this.emailValidator);
        this.appCreateUserCommandValidator.set('activationCode', this.activationCodeValidator);
        this.appCreateUserCommandValidator.set('password', this.passwordValidator);

        this.appForgetUserPasswordCommandValidator.set('email', this.emailValidator);

        this.appResetUserPasswordCommandValidator.set('email', this.emailValidator);
        this.appResetUserPasswordCommandValidator.set('token', this.tokenValidator);
        this.appResetUserPasswordCommandValidator.set('newPassword', this.passwordValidator);

        this.adminUpdateUserCommandValidator.set('locked', this.lockedValidator);
        this.adminUpdateUserCommandValidator.set('subscription', this.subscriptionValidator);
    }
    public validate(client: IResourceOwner|IPendingResourceOwner|IForgetPasswordRequest, context: string): ErrorMessage[] {
        if (context === 'adminUpdateUserCommandValidator')
            return this.validationWPlatform(client, this.adminUpdateUserCommandValidator)
        if (context === 'userUpdatePwdCommandValidator')
            return this.validationWPlatform(client, this.userUpdatePwdCommandValidator)
        if (context === 'appCreatePendingUserCommandValidator')
            return this.validationWPlatform(client, this.appCreatePendingUserCommandValidator)
        if (context === 'appCreateUserCommandValidator')
            return this.validationWPlatform(client, this.appCreateUserCommandValidator)
        if (context === 'appForgetUserPasswordCommandValidator')
            return this.validationWPlatform(client, this.appForgetUserPasswordCommandValidator)
        if (context === 'appResetUserPasswordCommandValidator')
            return this.validationWPlatform(client, this.appResetUserPasswordCommandValidator)
    }
    passwordValidator = (key: string, payload: IResourceOwnerUpdatePwd) => {
        let results: ErrorMessage[] = [];
        StringValidator.notEmpty(payload[key], results, key)
        return results
    }
    currentPwdValidator = (key: string, payload: IResourceOwnerUpdatePwd) => {
        let results: ErrorMessage[] = [];
        StringValidator.notEmpty(payload[key], results, key)
        return results
    }
    emailValidator = (key: string, payload: IPendingResourceOwner) => {
        let results: ErrorMessage[] = [];
        StringValidator.isEmail(payload[key], results, key)
        return results
    }
    activationCodeValidator = (key: string, payload: IPendingResourceOwner) => {
        let results: ErrorMessage[] = [];
        NumberValidator.isInteger(+payload[key], results, key)
        NumberValidator.greaterThan(+payload[key], 99999, results, key)
        return results
    }
    tokenValidator = (key: string, payload: IPendingResourceOwner) => {
        let results: ErrorMessage[] = [];
        StringValidator.hasValidWhiteListValue(payload[key], results, key)
        NumberValidator.isInteger(+payload[key], results, key)
        NumberValidator.greaterThan(+payload[key], 99999999, results, key)
        return results
    }
    lockedValidator = (key: string, payload: IPendingResourceOwner) => {
        let results: ErrorMessage[] = [];
        BooleanValidator.isBoolean(payload[key], results, key)
        return results
    }
    subscriptionValidator = (key: string, payload: IPendingResourceOwner) => {
        let results: ErrorMessage[] = [];
        BooleanValidator.isBoolean(payload[key], results, key)
        return results
    }
}

