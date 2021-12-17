import { IAggregateValidator, TPlatform, TValidator, ErrorMessage, StringValidator, ListValidator, notNullOrUndefined, NumberValidator, DefaultValidator, hasValue } from '../../validator-common';
import { IAttrImage, IProductDetail, IProductOptions, ISku } from './interfaze-product';


export class ProductValidator extends IAggregateValidator {
    private formId: string;
    private adminCreateProductCommandValidator: Map<string, TValidator> = new Map();
    private adminUpdateProductCommandValidator: Map<string, TValidator> = new Map();
    constructor(formId: string, platform?: TPlatform) {
        super(platform);
        this.formId = formId;
        this.adminCreateProductCommandValidator.set('name', this.nameValidator);
        this.adminCreateProductCommandValidator.set('description', this.descriptionValidator);
        this.adminCreateProductCommandValidator.set('imageUrlSmall', this.imageUrlSmallValidator);
        this.adminCreateProductCommandValidator.set('startAt', this.startAtValidator);
        this.adminCreateProductCommandValidator.set('endAt', this.endAtValidator);
        this.adminCreateProductCommandValidator.set('skus', this.skusCreateValidator);
        this.adminCreateProductCommandValidator.set('attributeSaleImages', this.attributeSaleImagesCreateValidator);
        this.adminCreateProductCommandValidator.set('imageUrlLarge', this.imageUrlLargeValidator);
        this.adminCreateProductCommandValidator.set('selectedOptions', this.selectedOptionsValidator);
        this.adminCreateProductCommandValidator.set('attributesKey', this.attributesKeyValidator);
        this.adminCreateProductCommandValidator.set('attributesGen', this.attributesGenValidator);
        this.adminCreateProductCommandValidator.set('attributesProd', this.attributesProdValidator);

        this.adminUpdateProductCommandValidator.set('name', this.nameValidator);
        this.adminUpdateProductCommandValidator.set('description', this.descriptionValidator);
        this.adminUpdateProductCommandValidator.set('imageUrlSmall', this.imageUrlSmallValidator);
        this.adminUpdateProductCommandValidator.set('startAt', this.startAtValidator);
        this.adminUpdateProductCommandValidator.set('endAt', this.endAtValidator);
        this.adminUpdateProductCommandValidator.set('skus', this.skusUpdateValidator);
        this.adminUpdateProductCommandValidator.set('attributeSaleImages', this.attributeSaleImagesCreateValidator);
        this.adminUpdateProductCommandValidator.set('imageUrlLarge', this.imageUrlLargeValidator);
        this.adminUpdateProductCommandValidator.set('selectedOptions', this.selectedOptionsValidator);
        this.adminUpdateProductCommandValidator.set('attributesKey', this.attributesKeyValidator);
        this.adminUpdateProductCommandValidator.set('attributesGen', this.attributesGenValidator);
        this.adminUpdateProductCommandValidator.set('attributesProd', this.attributesProdValidator);
    }
    public validate(payload: IProductDetail, context: string): ErrorMessage[] {
        if (context === 'adminCreateProductCommandValidator')
            return this.validationWPlatform(payload, this.adminCreateProductCommandValidator)
        if (context === 'adminUpdateProductCommandValidator')
            return this.validationWPlatform(payload, this.adminUpdateProductCommandValidator)
    }
    nameValidator = (key: string, payload: IProductDetail) => {
        let results: ErrorMessage[] = [];
        StringValidator.lessThanOrEqualTo(payload[key], 75, results, key);
        StringValidator.greaterThanOrEqualTo(payload[key], 1, results, key);
        return appendCtrlKey(results, this.formId)
    }
    attributesKeyValidator = (key: string, payload: IProductDetail) => {
        let results: ErrorMessage[] = [];
        ListValidator.hasValue(payload[key], results, key);
        return appendCtrlKey(results, this.formId)
    }
    attributesProdValidator = (key: string, payload: IProductDetail) => {
        let results: ErrorMessage[] = [];
        if (ListValidator.hasValue(payload[key], results, key)) {

        } else {
            results = []
        }
        return appendCtrlKey(results, this.formId)
    }
    attributesGenValidator = (key: string, payload: IProductDetail) => {
        let results: ErrorMessage[] = [];
        if (ListValidator.hasValue(payload[key], results, key)) {

        } else {
            results = []
        }
        return appendCtrlKey(results, this.formId)
    }
    selectedOptionsValidator = (key: string, payload: IProductDetail) => {
        let results: ErrorMessage[] = [];
        if (ListValidator.hasValue(payload[key], results, key)) {
            (payload[key] as IProductOptions[]).forEach((e, index) => {
                StringValidator.hasValidWhiteListValue(e.title, results, index + "_title");
                ListValidator.hasValue(e.options, results, index + "_options")
                e.options.forEach((ee, i) => {
                    StringValidator.hasValidWhiteListValue(ee.optionValue, results, index + "_" + i + "_optionValue");
                })
            })
        } else {
            results = [];
        }
        return appendCtrlKey(results, 'product_option')
    }

    startAtValidator = (key: string, payload: IProductDetail) => {
        let results: ErrorMessage[] = [];
        if (notNullOrUndefined(payload[key])) {
            NumberValidator.isNumber(payload[key], results, key);
            NumberValidator.isInteger(payload[key], results, key);
        }
        return appendCtrlKey(results, this.formId)
    }
    endAtValidator = (key: string, payload: IProductDetail) => {
        let results: ErrorMessage[] = [];
        if (notNullOrUndefined(payload[key])) {
            NumberValidator.isNumber(payload[key], results, key);
            NumberValidator.isInteger(payload[key], results, key);
        }
        return appendCtrlKey(results, this.formId)
    }
    imageUrlSmallValidator = (key: string, payload: IProductDetail) => {
        let results: ErrorMessage[] = [];
        StringValidator.isHttpUrl(payload[key], results, key);
        return appendCtrlKey(results, this.formId)
    }
    skusCreateValidator = (key: string, payload: IProductDetail) => {
        let results: ErrorMessage[] = [];
        ListValidator.hasValue(payload[key], results, key);
        if (Array.isArray(payload[key])) {
            (payload[key] as ISku[]).forEach((e, index) => {
                if ((!e.attributesSales) || e.attributesSales.length == 0) {
                    ListValidator.lengthIs(payload[key], 1, results, key);
                } else {
                    ListValidator.hasValue(e.attributesSales, results, key);
                }
                if (notNullOrUndefined(e.sales)) {
                    NumberValidator.isInteger(e.sales, results, index + '_sales');
                    NumberValidator.greaterThanOrEqualTo(e.price, 0, results, index + '_sales')
                }
                NumberValidator.isNumber(e.price, results, index + '_price');
                NumberValidator.greaterThan(e.price, 0, results, index + '_price')
                NumberValidator.isInteger(e.storageActual, results, index + '_storageActual')
                NumberValidator.greaterThanOrEqualTo(e.storageActual, 0, results, index + '_storageActual')
                NumberValidator.isInteger(e.storageOrder, results, index + '_storageOrder')
                NumberValidator.greaterThanOrEqualTo(e.storageOrder, 0, results, index + '_storageOrder')
                DefaultValidator.notExist(e.decreaseActualStorage, results, index + '_decreaseActualStorage')
                DefaultValidator.notExist(e.decreaseOrderStorage, results, index + '_decreaseOrderStorage')
                DefaultValidator.notExist(e.increaseActualStorage, results, index + '_increaseActualStorage')
                DefaultValidator.notExist(e.increaseOrderStorage, results, index + '_increaseOrderStorage')
            })

        } else {
            results.push({ type: 'skusNotArray', message: 'SKUS_NOT_ARRAY', key: key })
        }
        return appendCtrlKey(results, this.formId)
    }
    skusUpdateValidator = (key: string, payload: IProductDetail) => {
        let results: ErrorMessage[] = [];
        ListValidator.hasValue(payload[key], results, key);
        if (Array.isArray(payload[key])) {
            (payload[key] as ISku[]).forEach((e, index) => {
                if ((!e.attributesSales) || e.attributesSales.length == 0) {
                    ListValidator.lengthIs(payload[key], 1, results, key);
                } else {
                    ListValidator.hasValue(e.attributesSales, results, 'attributesSales');
                }
                NumberValidator.isNumber(e.price, results, index + '_price');
                NumberValidator.greaterThan(e.price, 0, results, index + '_price')
                if (notNullOrUndefined(e.sales)) {
                    NumberValidator.isInteger(e.sales, results, index + '_sales');
                    NumberValidator.greaterThanOrEqualTo(e.price, 0, results, index + '_sales')
                }
                if (notNullOrUndefined(e.storageActual)) {
                    NumberValidator.isInteger(e.storageActual, results, index + '_storageActual')
                    NumberValidator.greaterThan(e.storageActual, 0, results, index + '_storageActual')
                }
                if (notNullOrUndefined(e.storageOrder)) {
                    NumberValidator.isInteger(e.storageOrder, results, index + '_storageOrder')
                    NumberValidator.greaterThan(e.storageOrder, 0, results, index + '_storageOrder')
                }
                if (notNullOrUndefined(e.decreaseActualStorage)) {
                    NumberValidator.isInteger(e.decreaseActualStorage, results, index + '_decreaseActualStorage')
                    NumberValidator.greaterThanOrEqualTo(e.decreaseActualStorage, 0, results, index + '_decreaseActualStorage')
                }
                if (notNullOrUndefined(e.decreaseOrderStorage)) {
                    NumberValidator.isInteger(e.decreaseOrderStorage, results, index + '_decreaseOrderStorage')
                    NumberValidator.greaterThanOrEqualTo(e.decreaseOrderStorage, 0, results, index + '_decreaseOrderStorage')
                }
                if (notNullOrUndefined(e.increaseActualStorage)) {
                    NumberValidator.isInteger(e.increaseActualStorage, results, index + '_increaseActualStorage')
                    NumberValidator.greaterThanOrEqualTo(e.increaseActualStorage, 0, results, index + '_increaseActualStorage')
                }
                if (notNullOrUndefined(e.increaseOrderStorage)) {
                    NumberValidator.isInteger(e.increaseOrderStorage, results, index + '_increaseOrderStorage')
                    NumberValidator.greaterThanOrEqualTo(e.increaseOrderStorage, 0, results, index + '_increaseOrderStorage')
                }
            })

        } else {
            results.push({ type: 'skusNotArray', message: 'SKUS_NOT_ARRAY', key: key })
        }
        return appendCtrlKey(results, this.formId)
    }
    attributeSaleImagesCreateValidator = (key: string, payload: IProductDetail) => {
        let results: ErrorMessage[] = [];
        if (ListValidator.hasValue(payload[key], results, key)) {
            (payload[key] as IAttrImage[]).forEach(e => {
                StringValidator.isAttrKeyPair(e.attributeSales, results, key);
                ListValidator.hasValue(e.imageUrls, results, key)
            })
        } else {
            results = [];
        }
        return appendCtrlKey(results, this.formId)
    }
    imageUrlLargeValidator = (key: string, payload: IProductDetail) => {
        let results: ErrorMessage[] = [];
        if (ListValidator.hasValue(payload[key], results, key)) {
            (payload[key] as string[]).forEach((e, index) => {
                StringValidator.isHttpUrl(e, results, index + "_" + key);
            })
        } else {
            results = [];
        }
        return appendCtrlKey(results, this.formId)
    }
    //public display apply, diffrent rule
    descriptionValidator = (key: string, payload: IProductDetail) => {
        let results: ErrorMessage[] = [];
        if (StringValidator.hasValidWhiteListValue(payload[key], results, key)) {
            StringValidator.lessThanOrEqualTo(payload[key], 50, results, key)
        } else {
            results = [];
        }
        return appendCtrlKey(results, this.formId)
    }

}
export function appendCtrlKey(results: ErrorMessage[], id: string) {
    return results.map(e => {
        if (hasValue(e.key) && hasValue(e.formId)) {
            return e
        }
        else {
            return {
                ...e,
                formId: hasValue(e.formId) ? e.formId : id,
            }
        }
    });
}
