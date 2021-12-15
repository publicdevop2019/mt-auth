import { descriptionValidator, ErrorMessage, IAggregateValidator, StringValidator, TPlatform, TValidator } from '../../validator-common';
import { HTTP_METHODS, IEndpoint } from './interfaze-endpoint';


export class EndpointValidator extends IAggregateValidator {
    private rootCreateEndpointCommandValidator: Map<string, TValidator> = new Map();
    private rootUpdateEndpointCommandValidator: Map<string, TValidator> = new Map();
    constructor(platform?: TPlatform) {
        super(platform)
        if (platform) {
            this.platform = platform;
        }
        this.rootCreateEndpointCommandValidator.set('description', descriptionValidator);
        this.rootCreateEndpointCommandValidator.set('path', this.pathValidator);
        this.rootCreateEndpointCommandValidator.set('method', this.methodValidator);

        this.rootUpdateEndpointCommandValidator.set('description', descriptionValidator);
        this.rootUpdateEndpointCommandValidator.set('path', this.pathValidator);
        this.rootUpdateEndpointCommandValidator.set('method', this.methodValidator);
    }
    public validate(payload: IEndpoint, context: string): ErrorMessage[] {
        if (context === 'rootCreateEndpointCommandValidator')
            return this.validationWPlatform(payload, this.rootCreateEndpointCommandValidator)
        if (context === 'rootUpdateEndpointCommandValidator')
            return this.validationWPlatform(payload, this.rootUpdateEndpointCommandValidator)
    }
    pathValidator = (key: string, payload: IEndpoint) => {
        let results: ErrorMessage[] = [];
        StringValidator.isHttpPath(payload[key], results, key);
        StringValidator.lessThanOrEqualTo(payload[key], 100, results, key);
        return results
    }
    methodValidator = (key: string, payload: IEndpoint) => {
        let results: ErrorMessage[] = [];
        if (!payload.websocket) {
            StringValidator.hasValidWhiteListValue(payload[key], results, key);
            StringValidator.belongsTo(payload[key], HTTP_METHODS.map(e => e.value), results, key);
            return results
        }
        return results
    }
}
