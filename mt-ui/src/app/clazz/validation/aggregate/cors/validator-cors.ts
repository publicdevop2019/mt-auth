import { K, R } from '@angular/cdk/keycodes';
import { BooleanValidator, descriptionValidator, ErrorMessage, IAggregateValidator, ListValidator, NumberValidator, StringValidator, TPlatform, TValidator } from '../../validator-common';
import { ICorsProfile } from './interface-cors';

export class CORSProfileValidator extends IAggregateValidator {
    private validators: Map<string, TValidator> = new Map();
    constructor(platform?: TPlatform) {
        super(platform)
        this.validators.set('name', this.nameValidator);
        this.validators.set('description', descriptionValidator);
        this.validators.set('allowOrigin', this.allowOrigin);
        this.validators.set('allowCredentials', this.allowCredential);
        this.validators.set('maxAge', this.maxAge);
    }
    public validate(payload: ICorsProfile, context: string): ErrorMessage[] {
        console.dir(payload)
        return this.validationWPlatform(payload, this.validators)
    }
    allowOrigin = (key: string, payload: ICorsProfile) => {
        let results: ErrorMessage[] = [];
        (payload[key] as string[]).forEach(e=>{
            StringValidator.isOrigin(e,results,key)
        })
        return results
    }
    maxAge = (key: string, payload: ICorsProfile) => {
        let results: ErrorMessage[] = [];
        NumberValidator.isNumber(payload[key],results,key)
        return results
    }
    allowCredential = (key: string, payload: ICorsProfile) => {
        let results: ErrorMessage[] = [];
        BooleanValidator.isBoolean(payload[key],results,key)
        return results
    }
    nameValidator = (key: string, payload: ICorsProfile) => {
        let results: ErrorMessage[] = [];
        StringValidator.hasValidWhiteListValue(payload[key], results, key)
        StringValidator.lessThanOrEqualTo(payload[key], 50, results, key);
        StringValidator.greaterThanOrEqualTo(payload[key], 1, results, key);
        return results
    }
}

