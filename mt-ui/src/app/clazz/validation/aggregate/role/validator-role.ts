import { ErrorMessage, IAggregateValidator, TPlatform } from "../../validator-common";

export class RoleValidator extends IAggregateValidator {
    constructor(platform?: TPlatform) {
        super(platform)
    }
    validate(payload: any, context: string): ErrorMessage[] {
        return []
    }
}