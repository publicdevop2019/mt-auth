import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { EntityCommonService } from '../clazz/entity.common-service';
import { ISubRequest } from '../pages/tenant/market/subscribe-request/subscribe-request.component';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
@Injectable({
  providedIn: 'root'
})
export class CreateSubRequestService extends EntityCommonService<ISubRequest, ISubRequest>{
  private ENTITY_NAME = '/auth-svc/subscriptions/requests';
  entityRepo: string = environment.serverUri + this.ENTITY_NAME;
  constructor(public httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor, deviceSvc: DeviceService) {
    super(httpProxy, interceptor, deviceSvc);
  }
  create(s: ISubRequest, changeId: string) {
    this.httpProxySvc.createEntity(this.entityRepo, s, changeId).subscribe(next => {
        this.notify(!!next)
    });
};
}
