import { Injectable } from '@angular/core';
import { TenantEntityService } from '../clazz/tenant-entity.service';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
import { IRole } from '../pages/tenant/project/my-roles/my-roles.component';
@Injectable({
  providedIn: 'root'
})
export class MyRoleService extends TenantEntityService<IRole, IRole>{
  protected entityName: string = "roles";
  constructor(httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor, deviceSvc: DeviceService) {
    super(httpProxy, interceptor, deviceSvc);
  }
}
