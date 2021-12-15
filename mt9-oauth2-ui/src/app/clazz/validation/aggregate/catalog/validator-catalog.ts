import { ErrorMessage, IAggregateValidator, ListValidator, NumberValidator, StringValidator, TPlatform, TValidator } from '../../validator-common';
import { ICatalog } from './interfaze-catalog';

export class CatalogValidator extends IAggregateValidator {
    private adminCreateCatalogCommandValidator: Map<string, TValidator> = new Map();
    private adminUpdateCatalogCommandValidator: Map<string, TValidator> = new Map();
    constructor(platform?: TPlatform) {
        super(platform)
        this.adminCreateCatalogCommandValidator.set('name', this.nameValidator);
        this.adminCreateCatalogCommandValidator.set('parentId', this.parentIdValidator);
        this.adminCreateCatalogCommandValidator.set('attributes', this.attributesValidator);
        this.adminCreateCatalogCommandValidator.set('catalogType', this.catalogTypeValidator);

        this.adminUpdateCatalogCommandValidator.set('name', this.nameValidator);
        this.adminUpdateCatalogCommandValidator.set('parentId', this.parentIdValidator);
        this.adminUpdateCatalogCommandValidator.set('attributes', this.attributesValidator);
        this.adminUpdateCatalogCommandValidator.set('catalogType', this.catalogTypeValidator);
    }
    public validate(payload: ICatalog, context: string): ErrorMessage[] {
        if (context === 'adminCreateCatalogCommandValidator')
            return this.validationWPlatform(payload, this.adminCreateCatalogCommandValidator)
        if (context === 'adminUpdateCatalogCommandValidator')
            return this.validationWPlatform(payload, this.adminUpdateCatalogCommandValidator)
    }
    nameValidator = (key: string, payload: ICatalog) => {
        let results: ErrorMessage[] = [];
        StringValidator.hasValidWhiteListValue(payload[key], results, key)
        StringValidator.lessThanOrEqualTo(payload[key], 50, results, key);
        StringValidator.notBlank(payload[key], results, key);
        return results
    }
    parentIdValidator = (key: string, payload: ICatalog) => {
        let results: ErrorMessage[] = [];
        if (payload[key]) {
            StringValidator.notEmpty(payload[key], results, key)
        }
        return results
    }
    attributesValidator = (key: string, payload: ICatalog) => {
        let results: ErrorMessage[] = [];
        ListValidator.hasValue(payload[key], results, key)
        return results
    }
    catalogTypeValidator = (key: string, payload: ICatalog) => {
        let results: ErrorMessage[] = [];
        StringValidator.belongsTo(payload[key], ['BACKEND', 'FRONTEND'], results, key)
        return results
    }
}

