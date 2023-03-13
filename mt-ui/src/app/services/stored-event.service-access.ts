import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { EntityCommonService } from '../clazz/entity.common-service';
import { IStoredEvent } from '../pages/mgmt/summary-stored-event-access/summary-stored-event-access.component';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
@Injectable({
    providedIn: 'root'
})
export class StoredEventAccessService extends EntityCommonService<IStoredEvent, IStoredEvent> {
    retry(id: string) {
        return this.httpProxySvc.retry(this.entityRepo, id)
    }
    entityRepo: string = environment.serverUri + '/auth-svc/mngmt/events';
    eventRepo: string = environment.serverUri + '/auth-svc/mngmt/events';
    auditRepo: string = environment.serverUri + '/auth-svc/mngmt/events/audit';
    queryPrefix: string = undefined;
    constructor(httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor, deviceSvc: DeviceService) {
        super(httpProxy, interceptor, deviceSvc);
    }
    readEntityByQuery(num: number, size: number, query?: string, by?: string, order?: string, headers?: {}) {
        if (this.queryPrefix && query) {
            query = this.queryPrefix + ',' + query
        } else {
            if (this.queryPrefix) {
                query = this.queryPrefix
            }
        }
        return super.readEntityByQuery(num, size, query, by, order, headers)
    };
}
