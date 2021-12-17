import { descriptionValidator, ErrorMessage, IAggregateValidator, ListValidator, StringValidator, TPlatform, TValidator } from '../../validator-common';
import { IBizFilter, IFilterItem } from './interfaze-filter';

export class FilterValidator extends IAggregateValidator {
    private adminCreateFilterCommandValidator: Map<string, TValidator> = new Map();
    private adminUpdateFilterCommandValidator: Map<string, TValidator> = new Map();
    constructor(platform?: TPlatform) {
        super(platform)
        if (platform) {
            this.platform = platform;
        }
        this.adminCreateFilterCommandValidator.set('description', descriptionValidator);
        this.adminCreateFilterCommandValidator.set('catalogs', this.catalogsValidator);
        this.adminCreateFilterCommandValidator.set('filters', this.filtersValidator);

        this.adminUpdateFilterCommandValidator.set('description', descriptionValidator);
        this.adminUpdateFilterCommandValidator.set('catalogs', this.catalogsValidator);
        this.adminUpdateFilterCommandValidator.set('filters', this.filtersValidator);
    }
    public validate(payload: IBizFilter, context: string): ErrorMessage[] {
        if (context === 'adminCreateFilterCommandValidator')
            return this.validationWPlatform(payload, this.adminCreateFilterCommandValidator)
        if (context === 'adminUpdateFilterCommandValidator')
            return this.validationWPlatform(payload, this.adminUpdateFilterCommandValidator)
    }
    catalogsValidator = (key: string, payload: any) => {
        let results: ErrorMessage[] = [];
        ListValidator.hasValue(payload[key], results, key)
        return results
    }
    filtersValidator = (key: string, payload: any) => {
        let results: ErrorMessage[] = [];
        ListValidator.hasValue(payload[key], results, key)
        if (payload[key] && payload[key].length > 0) {
            (payload[key] as IFilterItem[]).forEach((e, index) => {
                StringValidator.hasValidWhiteListValue(e.name, results, index + '_filterItemName');
                ListValidator.hasValue(e.values, results, index + '_filterItemValue');
                (e.values as string[]).forEach((ee, ind) => {
                    StringValidator.hasValidWhiteListValue(ee, results, index + '_' + ind + '_filterItemValueList')
                })
            })
        }
        return results
    }
}

