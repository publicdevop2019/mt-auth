import { Injectable } from '@angular/core';
import { TenantEntityService } from '../clazz/tenant-entity.service';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
import { INewRole } from '../pages/tenant/project/my-roles/my-roles.component';
@Injectable({
  providedIn: 'root'
})
export class MyRoleService extends TenantEntityService<INewRole, INewRole>{
  protected entityName: string = "roles";
  constructor(httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor, deviceSvc: DeviceService) {
    super(httpProxy, interceptor, deviceSvc);
  }
}
