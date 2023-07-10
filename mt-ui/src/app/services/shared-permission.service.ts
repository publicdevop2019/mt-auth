import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { EntityCommonService } from '../clazz/entity.common-service';
import { IEndpoint } from '../clazz/validation/endpoint.interface';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
@Injectable({
  providedIn: 'root'
})
export class SharedPermissionService extends EntityCommonService<IEndpoint, IEndpoint>{
  entityRepo: string = undefined;
  role: string = '';
  constructor(httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor, deviceSvc: DeviceService) {
    super(httpProxy, interceptor, deviceSvc);
  }
  public setProjectId(projectId: string) {
    this.entityRepo = environment.serverUri + '/auth-svc/projects/' + projectId + '/permissions/shared';
  }
}
