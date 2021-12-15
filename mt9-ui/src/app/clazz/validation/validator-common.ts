export interface ErrorMessage {
    type: string,
    key: string
    formId?: string
    message?: string
}
export type TValidator = (value: any, payload: any) => ErrorMessage[];
export type TPlatform = 'CLIENT' | 'SERVER';
export abstract class IAggregateValidator {
    platform: TPlatform = 'CLIENT';
    constructor(platform: TPlatform) {
        if (platform) {
            this.platform = platform;
        }
    }
    abstract validate(payload: any, context: string): ErrorMessage[];

    public validationWPlatform(payload: any, validator: Map<string, TValidator>): ErrorMessage[] {
        let errors: ErrorMessage[] = [];
        if (this.platform === 'CLIENT') {
            validator.forEach((fn, field) => {
                errors.push(...fn(field, payload))
            })
        } else {
            //fail fast for server
            validator.forEach((fn, field) => {
                if (errors.length === 0) {
                    if (fn(field, payload).length > 0) {
                        errors = fn(field, payload);
                    }
                }
            })
        }
        return errors.filter((v, i, a) => a.findIndex(t => (t.key === v.key && t.message === v.message && t.type === v.type)) === i);
    }
}
export class StringValidator {
    static STRING_WHITE_LIST = new RegExp(/^[a-zA-Z0-9 \u4E00-\u9FFF_]*$/)
    static STRING_WHITE_LIST_DESC = new RegExp(/^[a-zA-Z0-9 \(\)\+\u4E00-\u9FFF]*$/)
    static STRING_WHITE_LIST_ATTR = new RegExp(/^[a-zA-Z0-9\:\u4E00-\u9FFF]*$/)
    static STRING_WHITE_LIST_ATTR_VALUE = new RegExp(/^[a-zA-Z0-9-\+\x\(\)\/\:\u4E00-\u9FFF]*$/)
    static STRING_URL_PATH_LIST = new RegExp(/^[a-zA-Z-\/\*]*$/)
    public static greaterThanOrEqualTo(var0: string, arg1: number, results: ErrorMessage[], key: string): boolean {
        if (!StringValidator.isString(var0, results, key) || var0.length < arg1) {
            results.push({ type: "greaterThanOrEqualTo", message: 'STRING_GREATER_THAN_OR_EQUAL_TO', key: key })
            return false
        } else {
            return true
        }
    }
    public static whiteListValueOnly(var0: string, results: ErrorMessage[], key: string): boolean {
        if (!StringValidator.isString(var0, results, key) || !StringValidator.STRING_WHITE_LIST.test(var0)) {
            results.push({ type: "whiteListValueOnly", message: 'STRING_WHITE_LIST_VALUE_ONLY', key: key })
            return false
        } else {
            return true
        }
    }
    public static isDescription(var0: string, results: ErrorMessage[], key: string): boolean {
        if (!StringValidator.isString(var0, results, key) || !StringValidator.STRING_WHITE_LIST_DESC.test(var0)) {
            results.push({ type: "isDescription", message: 'STRING_IS_DESCRIPTION', key: key })
            return false
        } else {
            return true
        }
    }
    public static isAttrKeyPair(var0: string, results: ErrorMessage[], key: string): boolean {
        if (!StringValidator.isString(var0, results, key) || !StringValidator.STRING_WHITE_LIST_ATTR.test(var0)) {
            results.push({ type: "isAttrKeyPair", message: 'STRING_IS_ATTR_KEY_PAIR', key: key })
            return false
        } else {
            return true
        }
    }
    public static isAttrValue(var0: string, results: ErrorMessage[], key: string): boolean {
        if (!StringValidator.isString(var0, results, key) || !StringValidator.STRING_WHITE_LIST_ATTR_VALUE.test(var0)) {
            results.push({ type: "isAttrValue", message: 'STRING_IS_ATTR_VALUE', key: key })
            return false
        } else {
            return true
        }
    }
    public static isHttpPath(var0: string, results: ErrorMessage[], key: string): boolean {
        if (!StringValidator.isString(var0, results, key) || !StringValidator.STRING_URL_PATH_LIST.test(var0) || !StringValidator.notEmpty(var0, results, key)) {
            results.push({ type: "isHttpPath", message: 'STRING_IS_HTTP_PATH', key: key })
            return false
        } else {
            return true
        }
    }
    public static lessThanOrEqualTo(var0: any, arg1: number, results: ErrorMessage[], key: string): boolean {
        if (!StringValidator.isString(var0, results, key) || var0.length > arg1) {
            results.push({ type: "lessThanOrEqualTo", message: 'STRING_LESS_THAN_OR_EQUAL_TO', key: key });
            return false
        } else {
            return true
        }
    }
    public static isString(var0: string, results: ErrorMessage[], key: string): boolean {
        if (typeof var0 !== 'string') {
            results.push({ type: "typeMatch", message: 'STRING_TYPE_MATCH', key: key })
            return false
        } else {
            return true
        }
    }
    public static isEmail(var0: string, results: ErrorMessage[], key: string): boolean {
        if (!StringValidator.isString(var0, results, key) || !new RegExp(/^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/g).test(var0)) {
            results.push({ type: "isEmail", message: 'STRING_IS_EMAIL', key: key })
            return false
        } else {
            return true
        }
    }
    public static lessThan(var0: string, length: number, results: ErrorMessage[], key: string): boolean {
        if (!StringValidator.isString(var0, results, key) || var0.length >= length) {
            results.push({ type: "lessThan", message: 'STRING_LESS_THAN', key: key })
            return false
        } else {
            return true
        }
    }
    public static greaterThan(var0: string, length: number, results: ErrorMessage[], key: string): boolean {
        if (!StringValidator.isString(var0, results, key) || var0.length <= length) {
            results.push({ type: "greaterThan", message: 'STRING_GREATER_THAN', key: key })
            return false
        } else {
            return true
        }
    }
    public static notEmpty(var0: string | undefined | null, results: ErrorMessage[], key: string): boolean {
        if (!StringValidator.isString(var0, results, key) || var0 === '') {
            results.push({ type: "notEmptyString", message: 'STRING_NOT_EMPTY_STRING', key: key })
            return false;
        } else {
            return true;
        }
    }
    public static notBlank(var0: string | undefined | null, results: ErrorMessage[], key: string): boolean {
        if (!StringValidator.isString(var0, results, key) || var0.trim() === '') {
            results.push({ type: "notBlankString", message: 'STRING_NOT_BLAND_STRING', key: key })
            return false;
        } else {
            return true;
        }
    }
    public static hasValidWhiteListValue(var0: string | undefined | null, results: ErrorMessage[], key: string): boolean {
        if (!StringValidator.isString(var0, results, key) || var0 === '' || !StringValidator.whiteListValueOnly(var0, results, key)) {
            results.push({ type: "hasStringValue", message: 'STRING_HAS_VALUE', key: key })
            return false;
        } else {
            return true;
        }
    }
    public static belongsTo(var0: string | undefined | null, list: string[], results: ErrorMessage[], key: string): boolean {
        if (!StringValidator.isString(var0, results, key) || !list.includes(var0)) {
            results.push({ type: "belongsTo", message: 'STRING_BELONGS_TO', key: key })
            return false;
        } else {
            return true;
        }
    }
    public static isHttpUrl(var0: any, results: ErrorMessage[], key: string): boolean {
        let regex = new RegExp(/https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&\/=]*)/)
        let regex2 = new RegExp(/^https?:\/\/localhost:[0-9]{1,5}\/([-a-zA-Z0-9()@:%_\+.~#?&\/=]*)/)
        if (!StringValidator.isString(var0, results, key) || !(regex2.test(var0) || regex.test(var0))) {
            results.push({ type: "isUrl", message: 'STRING_IS_URL', key: key })
            return false;
        } else {
            return true;
        }
    }
    public static isOrigin(var0: any, results: ErrorMessage[], key: string): boolean {
        let regex = new RegExp(/https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&\/=]*)/)
        let regex2 = new RegExp(/^https?:\/\/localhost:[0-9]{1,5}/)
        if (!StringValidator.isString(var0, results, key) || !(regex2.test(var0) || regex.test(var0))) {
            results.push({ type: "isOrigin", message: 'STRING_IS_ORIGIN', key: key })
            return false;
        } else {
            return true;
        }
    }
}
export class DefaultValidator {
    public static notNull(var0: string | undefined | null, results: ErrorMessage[], key: string): boolean {
        if (var0 === null) {
            results.push({ type: "notNull", message: 'DEFAULT_NOT_NULL', key: key })
            return false;
        } else {
            return true;
        }
    }
    public static notUndefined(var0: string | undefined | null, results: ErrorMessage[], key: string): boolean {
        if (var0 === undefined) {
            results.push({ type: "notUndefined", message: 'DEFAULT_NOT_NULL', key: key })
            return false;
        } else {
            return true;
        }
    }
    public static hasValue(var0: any, results: ErrorMessage[], key: string): boolean {
        if (var0 === undefined || var0 === null) {
            results.push({ type: "hasValue", message: 'DEFAULT_NOT_NULL', key: key })
            return false;
        } else {
            return true;
        }
    }
    public static notExist(var0: any, results: ErrorMessage[], key: string): boolean {
        if (notNullOrUndefined(var0)) {
            results.push({ type: "shouldNotExist", message: 'DEFAULT_SHOULD_NOT_EXIST', key: key })
            return false;
        } else {
            return true;
        }
    }
}
export class BooleanValidator {
    public static isBoolean(var0: any, results: ErrorMessage[], key: string): boolean {
        if (typeof var0 !== 'boolean') {
            results.push({ type: "isBoolean", message: 'IS_BOOLEAN', key: key })
            return false;
        } else {
            return true;
        }
    }
    public static isTrue(var0: boolean | undefined | null, results: ErrorMessage[], key: string): boolean {
        if (!BooleanValidator.isBoolean(var0, results, key) || var0 !== true) {
            results.push({ type: "isTrue", message: 'IS_TRUE', key: key })
            return false;
        } else {
            return true;
        }
    }
    public static isFalse(var0: boolean | undefined | null, results: ErrorMessage[], key: string): boolean {
        if (!BooleanValidator.isBoolean(var0, results, key) || var0 !== true) {
            results.push({ type: "isFalse", message: 'IS_FALSE', key: key })
            return false;
        } else {
            return true;
        }
    }
}
export class ListValidator {
    public static belongsTo(var0: string, list: string[], results: ErrorMessage[], key: string): boolean {
        if (!ListValidator.isList(var0, results, key) || typeof list[0] !== typeof var0 || list.length === 0 || !list.includes(var0)) {
            results.push({ type: "belongsTo", message: 'LIST_BELONGS_TO', key: key })
            return false;
        } else {
            return true;
        }
    }
    public static isSubListOf(var0: string[], list: string[], results: ErrorMessage[], key: string): boolean {
        if (!ListValidator.isList(var0, results, key) || typeof list !== typeof var0 || list.length === 0 || var0.some(e => !list.includes(e))) {
            results.push({ type: "isSubListOf", message: 'LIST_IS_SUB_LIST_OF', key: key })
            return false;
        } else {
            return true;
        }
    }
    public static hasValue(var0: any[], results: ErrorMessage[], key: string): boolean {
        if (!ListValidator.isList(var0, results, key) || var0 === undefined || var0 === null || var0.length === 0) {
            results.push({ type: "hasValue", message: 'LIST_HAS_VALUE', key: key })
            return false;
        } else {
            return true;
        }
    }
    public static isEmpty(var0: any[], results: ErrorMessage[], key: string): boolean {
        if (!ListValidator.isList(var0, results, key) || var0 === undefined || var0 === null || var0.length !== 0) {
            results.push({ type: "isEmpty", message: 'LIST_IS_EMPTY', key: key })
            return false;
        } else {
            return true;
        }
    }
    public static isList(var0: any, results: ErrorMessage[], key: string): boolean {
        if (!Array.isArray(var0)) {
            results.push({ type: "isList", message: 'LIST_IS_LIST', key: key })
            return false;
        } else {
            return true;
        }
    }
    public static lengthIs(var0: any[], len: number, results: ErrorMessage[], key: string): boolean {
        if (!ListValidator.isList(var0, results, key) || var0 === undefined || var0 === null || var0.length !== len) {
            results.push({ type: "lengthIs", message: 'LIST_LENGTH_IS', key: key })
            return false;
        } else {
            return true;
        }
    }
}
export class NumberValidator {
    public static isInteger(var0: any, results: ErrorMessage[], key: string): boolean {
        if (!NumberValidator.isNumber(var0, results, key) || !Number.isInteger(var0)) {
            results.push({ type: "isInteger", message: 'NUMBER_IS_INTEGER', key: key })
            return false;
        } else {
            return true;
        }
    }
    public static greaterThan(var0: any, var1: number, results: ErrorMessage[], key: string): boolean {
        if (!NumberValidator.isNumber(var0, results, key) || var0 <= var1) {
            results.push({ type: "greaterThan", message: 'NUMBER_GREATER_THAN', key: key })
            return false;
        } else {
            return true;
        }
    }
    public static greaterThanOrEqualTo(var0: any, var1: number, results: ErrorMessage[], key: string): boolean {
        if (!NumberValidator.isNumber(var0, results, key) || var0 < var1) {
            results.push({ type: "greaterThanOrEqualTo", message: 'NUMBER_GREATER_THAN_OREQUAL_TO', key: key })
            return false;
        } else {
            return true;
        }
    }
    public static lessThan(var0: any, var1: number, results: ErrorMessage[], key: string): boolean {
        if (!NumberValidator.isNumber(var0, results, key) || var0 >= var1) {
            results.push({ type: "lessThan", message: 'NUMBER_LESS_THAN', key: key })
            return false;
        } else {
            return true;
        }
    }
    public static lessThanOrEqualTo(var0: any, var1: number, results: ErrorMessage[], key: string): boolean {
        if (!NumberValidator.isNumber(var0, results, key) || var0 > var1) {
            results.push({ type: "lessThanOrEqualTo", message: 'NUMBER_LESS_THAN_OR_EQUAL_TO', key: key })
            return false;
        } else {
            return true;
        }
    }
    public static isNumber(var0: any, results: ErrorMessage[], key: string): boolean {
        if (typeof var0 !== "number" || Number.isNaN(var0)) {
            results.push({ type: "isNumber", message: 'NUMBER_IS_NUMBER', key: key })
            return false;
        } else {
            return true;
        }
    }
}
export function hasValue(input: any): boolean {
    return input !== null && input !== undefined && input !== '';
}
export function notNullOrUndefined(value: any) {
    return value !== undefined && value !== null;
}
export const descriptionValidator = (key: string, payload: any) => {
    let results: ErrorMessage[] = [];
    if (payload[key] !== null && payload[key] !== undefined) {
        StringValidator.notEmpty(payload[key], results, key)
        StringValidator.isDescription(payload[key], results, key)
        StringValidator.lessThanOrEqualTo(payload[key], 50, results, key)
    } else {
    }
    return results
}