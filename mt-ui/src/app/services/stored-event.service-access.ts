import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { EntityCommonService } from '../clazz/entity.common-service';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
import { IStoredEvent } from './stored-event.service';
@Injectable({
    providedIn: 'root'
})
export class StoredEventAccessService extends EntityCommonService<IStoredEvent, IStoredEvent> {
    retry(id: string) {
        return this.httpProxySvc.retry(this.entityRepo, id)
    }
    entityRepo: string = environment.serverUri + '/auth-svc/mngmt/events';
    constructor(httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor, deviceSvc: DeviceService) {
        super(httpProxy, interceptor, deviceSvc);
    }
}
