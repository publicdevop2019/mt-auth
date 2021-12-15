import { descriptionValidator, ErrorMessage, IAggregateValidator, StringValidator, TPlatform, TValidator } from '../../validator-common';
import { IRoleGroup } from './interface-role-group';

export class RoleGroupValidator extends IAggregateValidator {
    private validators: Map<string, TValidator> = new Map();
    constructor(platform?: TPlatform) {
        super(platform)
        this.validators.set('name', this.nameValidator);
        this.validators.set('description', descriptionValidator);
    }
    public validate(payload: IRoleGroup, context: string): ErrorMessage[] {
        return this.validationWPlatform(payload, this.validators)
    }
    nameValidator = (key: string, payload: IRoleGroup) => {
        let results: ErrorMessage[] = [];
        StringValidator.hasValidWhiteListValue(payload[key], results, key)
        StringValidator.lessThanOrEqualTo(payload[key], 50, results, key);
        StringValidator.greaterThanOrEqualTo(payload[key], 1, results, key);
        return results
    }
}

