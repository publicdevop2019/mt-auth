import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { EntityCommonService } from '../clazz/entity.common-service';
import { IClient } from '../clazz/validation/aggregate/client/interfaze-client';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
@Injectable({
  providedIn: 'root'
})
export class MyClientService extends EntityCommonService<IClient, IClient> {
  private AUTH_SVC_NAME = '/auth-svc';
  private ENTITY_NAME = '/clients';
  entityRepo: string = environment.serverUri + this.AUTH_SVC_NAME + this.ENTITY_NAME;
  queryPrefix = undefined;
  constructor(private httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor,deviceSvc:DeviceService) {
    super(httpProxy, interceptor,deviceSvc);
  }
  readEntityByQuery(num: number, size: number, query?: string, by?: string, order?: string, headers?: {}) {
    return this.httpProxySvc.readEntityByQuery<IClient>(this.entityRepo, this.role, num, size, query ? (this.queryPrefix + ','+query) : this.queryPrefix, by, order, headers)
  };
}
