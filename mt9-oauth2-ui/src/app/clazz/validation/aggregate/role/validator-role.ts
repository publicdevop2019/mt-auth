import { TYPE_ROLE } from 'src/app/clazz/constants';
import { descriptionValidator, ErrorMessage, IAggregateValidator, StringValidator, TPlatform, TValidator } from '../../validator-common';
import { IRole } from './interface-role';

export class RoleValidator extends IAggregateValidator {
    private validators: Map<string, TValidator> = new Map();
    constructor(platform?: TPlatform) {
        super(platform)
        this.validators.set('name', this.nameValidator);
        this.validators.set('description', descriptionValidator);
        this.validators.set('type', this.typeValidator);
    }
    public validate(payload: IRole, context: string): ErrorMessage[] {
        return this.validationWPlatform(payload, this.validators)
    }
    typeValidator = (key: string, payload: IRole) => {
        let results: ErrorMessage[] = [];
        StringValidator.belongsTo(payload[key], TYPE_ROLE.map(e => e.value as string), results, key);
        return results
    }
    nameValidator = (key: string, payload: IRole) => {
        let results: ErrorMessage[] = [];
        StringValidator.hasValidWhiteListValue(payload[key], results, key)
        StringValidator.lessThanOrEqualTo(payload[key], 50, results, key);
        StringValidator.greaterThanOrEqualTo(payload[key], 1, results, key);
        return results
    }
}

