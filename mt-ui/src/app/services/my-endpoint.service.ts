import { Injectable } from '@angular/core';
import { IQueryProvider } from 'mt-form-builder/lib/classes/template.interface';
import { environment } from 'src/environments/environment';
import { EntityCommonService } from '../clazz/entity.common-service';
import { IEndpoint } from '../clazz/validation/aggregate/endpoint/interfaze-endpoint';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
@Injectable({
  providedIn: 'root'
})
export class MyEndpointService extends EntityCommonService<IEndpoint, IEndpoint> implements IQueryProvider{
  private ENTITY_NAME = '/auth-svc/endpoints';
  entityRepo: string = environment.serverUri + this.ENTITY_NAME;
  role: string = '';
  queryPrefix = undefined;
  constructor(httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor,deviceSvc:DeviceService) {
    super(httpProxy, interceptor,deviceSvc);
  }
  readEntityByQuery(num: number, size: number, query?: string, by?: string, order?: string, headers?: {}) {
    return this.httpProxySvc.readEntityByQuery<IEndpoint>(this.entityRepo, this.role, num, size, query ? (this.queryPrefix + ','+query) : this.queryPrefix, by, order, headers)
  };
  readByQuery(num: number, size: number, query?: string, by?: string, order?: string, header?: {}) {
    return this.httpProxySvc.readEntityByQuery<IEndpoint>(this.entityRepo, this.role, num, size, query ? (this.queryPrefix + ',' + query) : this.queryPrefix, by, order, header)
  }
}
