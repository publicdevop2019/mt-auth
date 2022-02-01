import { ErrorMessage, IAggregateValidator, TPlatform } from "../../validator-common";

export class ProjectValidator extends IAggregateValidator {
    constructor(platform?: TPlatform) {
        super(platform)
    }
    validate(payload: any, context: string): ErrorMessage[] {
        throw new Error("Method not implemented.");
    }
}