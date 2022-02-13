import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { EntityCommonService } from '../clazz/entity.common-service';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
export interface IStoredEvent {
    id: string,
    eventBody: string,
    timestamp: number,
    name: string,
    domainId: string,
    internal: string,
    topic: string,
    version: number
}
@Injectable({
    providedIn: 'root'
})
export class StoredEventService extends EntityCommonService<IStoredEvent, IStoredEvent> {
    retry(id: string) {
        return this.httpProxySvc.retry(this.entityRepo, id)
    }
    private SVC_NAME = '/saga-svc';
    private ENTITY_NAME = '/events';
    entityRepo: string = environment.serverUri + this.SVC_NAME + this.ENTITY_NAME;
    constructor(httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor, deviceSvc: DeviceService) {
        super(httpProxy, interceptor, deviceSvc);
    }
    getServiceName() {
        return this.SVC_NAME;
    }
    setServiceName(next: string) {
        this.entityRepo = environment.serverUri + next + this.ENTITY_NAME;
        return this.SVC_NAME = next;
    }
    resetServiceName() {
        this.setServiceName('/saga-svc')
    }
    getRelatedEvents(num: number, size: number, query: string, entityRepo: string) {
        return this.httpProxySvc.readEntityByQuery<IStoredEvent>(environment.serverUri + entityRepo + this.ENTITY_NAME, num, size, query)
    };
}
