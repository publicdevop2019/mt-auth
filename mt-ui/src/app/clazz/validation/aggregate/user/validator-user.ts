import { BooleanValidator, ErrorMessage, IAggregateValidator, ListValidator, NumberValidator, StringValidator, TPlatform, TValidator } from '../../validator-common';
import { IForgetPasswordRequest, IPendingResourceOwner, IAuthUser, IResourceOwnerUpdatePwd } from './interfaze-user';

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
        this.appCreateUserCommandValidator.set('mobileNumber', this.mobileNumberValidator);
        this.appCreateUserCommandValidator.set('countryCode', this.countryCodeValidator);

        this.appForgetUserPasswordCommandValidator.set('email', this.emailValidator);

        this.appResetUserPasswordCommandValidator.set('email', this.emailValidator);
        this.appResetUserPasswordCommandValidator.set('token', this.tokenValidator);
        this.appResetUserPasswordCommandValidator.set('newPassword', this.passwordValidator);

        this.adminUpdateUserCommandValidator.set('locked', this.lockedValidator);
    }
    public validate(client: IAuthUser | IPendingResourceOwner | IForgetPasswordRequest, context: string): ErrorMessage[] {
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
        const pwd: string = payload[key]
        if (pwd.search(/[a-z]/i) < 0) {
            results.push({ type: 'pwd', key: key, message: 'PWD_LETTER' })
        }
        if (pwd.search(/[0-9]/i) < 0) {
            results.push({ type: 'pwd', key: key, message: 'PWD_NUMBER' })
        }
        if (pwd.search(/^\S*$/) < 0) {
            results.push({ type: 'pwd', key: key, message: 'PWD_WHITESPACE' })
        }
        if (pwd.search(/^(?=.*[~`!@#$%^&*()--+={}\[\]|\\:;"'<>,.?/_₹]).*$/) < 0) {
            results.push({ type: 'pwd', key: key, message: 'PWD_SPECIAL_CHAR' })
        }
        if (pwd.length < 10 || pwd.length > 16) {
            results.push({ type: 'pwd', key: key, message: 'PWD_LENGTH' })
        }
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
    mobileNumberValidator = (key: string, payload: IPendingResourceOwner) => {
        let results: ErrorMessage[] = [];
        StringValidator.lessThanOrEqualTo(payload[key], 11, results, key)
        StringValidator.greaterThanOrEqualTo(payload[key], 10, results, key)
        return results
    }
    countryCodeValidator = (key: string, payload: IPendingResourceOwner) => {
        let results: ErrorMessage[] = [];
        StringValidator.notBlank(payload[key], results, key)
        StringValidator.belongsTo(payload[key], ['1', '86'], results, key)
        return results
    }
    activationCodeValidator = (key: string, payload: IPendingResourceOwner) => {
        let results: ErrorMessage[] = [];
        StringValidator.notBlank(payload[key], results, key)
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

