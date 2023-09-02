import { Injectable } from '@angular/core';
import { Observable, ReplaySubject, Subject } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { EntityCommonService } from '../clazz/entity.common-service';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
import { IProjectSimple, IProjectDashboard } from '../misc/interface';
export interface IProjectPermissionInfo {
  projectPermissionInfo: IProjectPermission[]
}
export interface IProjectPermission {
  projectId: string
  permissionInfo: IPermissionInfo[];
}
interface IPermissionInfo {
  name: string
  id: string
}
@Injectable({
  providedIn: 'root'
})
export class ProjectService extends EntityCommonService<IProjectSimple, IProjectSimple>{
  private PRODUCT_SVC_NAME = '/auth-svc';
  private ENTITY_NAME = '/mgmt/projects';
  public totalProjects: IProjectSimple[] = [];
  public viewProject: IProjectSimple = undefined;
  entityRepo: string = environment.serverUri + this.PRODUCT_SVC_NAME + this.ENTITY_NAME;
  permissionDetail: ReplaySubject<IProjectPermission[]> = new ReplaySubject();
  constructor(public httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor, deviceSvc: DeviceService) {
    super(httpProxy, interceptor, deviceSvc);
  }
  findTenantProjects(num: number, size: number, headers?: {}) {
    return this.httpProxySvc.readEntityByQuery<IProjectSimple>(environment.serverUri + '/auth-svc/projects/tenant', num, size, undefined, undefined, undefined, headers)
  };
  findUIPermission() {
    return this.httpProxySvc.getUIPermission()
  };
  create(s: IProjectSimple, changeId: string) {
    return this.httpProxySvc.createEntity(environment.serverUri + '/auth-svc/projects', s, changeId, { 'loading': 'false' })
  };
  ready(projectId: string) {
    return this.httpProxySvc.checkPorjectReady(projectId)
  };
  getMyProject(projectId: string) {
    return this.httpProxySvc.readEntityById<IProjectDashboard>(environment.serverUri + '/auth-svc/projects', projectId)
  };
  resolveNameById(id: Observable<string>) {
    return id.pipe(map(ee => this.totalProjects.find(e => e.id === ee)?.name))
  }
  hasPermission(permissions: IProjectPermission[], projectId: string, name: string) {
    const pId = permissions.find(e => e.projectId === projectId)?.permissionInfo.find(e => e.name === name)?.id
    if (pId) {
      return this.httpProxy.currentUserAuthInfo.permissionIds.includes(pId)
    } else {
      return false
    }
  }
  showMgmtPanel() {
    return !!this.totalProjects.find(e => e.id === '0P8HE307W6IO')
  }
  hasTenantProjects() {
    return this.httpProxy.currentUserAuthInfo.tenantIds.length > 0;
  }
}
