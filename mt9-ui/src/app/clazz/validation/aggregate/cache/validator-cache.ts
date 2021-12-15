import { descriptionValidator, ErrorMessage, IAggregateValidator, ListValidator, NumberValidator, StringValidator, TPlatform, TValidator } from '../../validator-common';
import { ICacheProfile } from './interfaze-cache';

export class CacheValidator extends IAggregateValidator {
    private create: Map<string, TValidator> = new Map();
    constructor(platform?: TPlatform) {
        super(platform)
        this.create.set('name', this.nameValidator);
        this.create.set('description', descriptionValidator);

    }
    public validate(payload: ICacheProfile, context: string): ErrorMessage[] {
        return this.validationWPlatform(payload, this.create)
    }
    nameValidator = (key: string, payload: ICacheProfile) => {
        let results: ErrorMessage[] = [];
        StringValidator.hasValidWhiteListValue(payload[key], results, key)
        StringValidator.lessThanOrEqualTo(payload[key], 50, results, key);
        StringValidator.notBlank(payload[key], results, key);
        return results
    }
}

