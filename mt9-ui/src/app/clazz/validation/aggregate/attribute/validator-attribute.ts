import { descriptionValidator, ErrorMessage, IAggregateValidator, ListValidator, StringValidator, TPlatform, TValidator } from '../../validator-common';
import { IBizAttribute } from './interfaze-attribute';

export class AttributeValidator extends IAggregateValidator {
    private adminCreateAttributeCommandValidator: Map<string, TValidator> = new Map();
    private adminUpdateAttributeCommandValidator: Map<string, TValidator> = new Map();
    constructor(platform?: TPlatform) {
        super(platform)
        this.adminCreateAttributeCommandValidator.set('name', this.nameValidator);
        this.adminCreateAttributeCommandValidator.set('description', descriptionValidator);
        this.adminCreateAttributeCommandValidator.set('method', this.methodValidator);
        this.adminCreateAttributeCommandValidator.set('type', this.typeValidator);
        this.adminCreateAttributeCommandValidator.set('selectValues', this.selectValuesValidator);
        
        this.adminUpdateAttributeCommandValidator.set('name', this.nameValidator);
        this.adminUpdateAttributeCommandValidator.set('description', descriptionValidator);
        this.adminUpdateAttributeCommandValidator.set('method', this.methodValidator);
        this.adminUpdateAttributeCommandValidator.set('type', this.typeValidator);
        this.adminUpdateAttributeCommandValidator.set('selectValues', this.selectValuesValidator);
    }
    public validate(payload: IBizAttribute, context: string): ErrorMessage[] {
        if (context === 'adminCreateAttributeCommandValidator')
            return this.validationWPlatform(payload, this.adminCreateAttributeCommandValidator)
        if (context === 'adminUpdateAttributeCommandValidator')
            return this.validationWPlatform(payload, this.adminUpdateAttributeCommandValidator)
    }
    nameValidator = (key: string, payload: IBizAttribute) => {
        let results: ErrorMessage[] = [];
        StringValidator.hasValidWhiteListValue(payload[key], results, key)
        StringValidator.lessThanOrEqualTo(payload[key], 50, results, key);
        StringValidator.greaterThanOrEqualTo(payload[key], 1, results, key);
        return results
    }
    methodValidator = (key: string, payload: IBizAttribute) => {
        let results: ErrorMessage[] = [];
        StringValidator.belongsTo(payload[key], ['MANUAL', 'SELECT'], results, key)
        return results
    }
    typeValidator = (key: string, payload: IBizAttribute) => {
        let results: ErrorMessage[] = [];
        StringValidator.belongsTo(payload[key], ['PROD_ATTR', 'SALES_ATTR', 'KEY_ATTR', 'GEN_ATTR'], results, key)
        return results
    }
    selectValuesValidator = (key: string, payload: IBizAttribute) => {
        let results: ErrorMessage[] = [];
        if (payload.method === 'SELECT') {
            ListValidator.hasValue(payload[key], results, key);
            if (payload[key] && payload[key].length > 0) {
                (payload[key] as string[]).forEach((e, index) => {
                    StringValidator.isAttrValue(e, results, index + '_valueOption')
                })
            }
        }
        return results
    }
}

