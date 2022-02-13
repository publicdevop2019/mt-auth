import { Injectable } from '@angular/core';
import { TenantEntityService } from '../clazz/tenant-entity.service';
import { IEndpoint } from '../clazz/validation/aggregate/endpoint/interfaze-endpoint';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
@Injectable({
  providedIn: 'root'
})
export class MyEndpointService extends TenantEntityService<IEndpoint, IEndpoint>{
  entityName: string = 'endpoints';
  constructor(httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor,deviceSvc:DeviceService) {
    super(httpProxy, interceptor,deviceSvc);
  }
}
