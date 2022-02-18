import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { EntityCommonService } from '../clazz/entity.common-service';
import { IEndpoint } from '../clazz/validation/aggregate/endpoint/interfaze-endpoint';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
@Injectable({
  providedIn: 'root'
})
export class EndpointService extends EntityCommonService<IEndpoint, IEndpoint>{
  private ENTITY_NAME = '/auth-svc/mngmt/endpoints';
  entityRepo: string = environment.serverUri + this.ENTITY_NAME;
  role: string = '';
  constructor(httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor,deviceSvc:DeviceService) {
    super(httpProxy, interceptor,deviceSvc);
  }
}
