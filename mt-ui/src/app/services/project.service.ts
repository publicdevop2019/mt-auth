import { Injectable } from '@angular/core';
import { Observable, ReplaySubject, Subject } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { EntityCommonService } from '../clazz/entity.common-service';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
import { IProjectSimple, IProjectDashboard } from '../misc/interface';
export interface IProjectUiPermission {
  projectId: string
  permissionInfo: IPermissionOptions[];
}
interface IPermissionOptions {
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
  permissionDetail: ReplaySubject<IProjectUiPermission> = new ReplaySubject();
  constructor(public httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor, deviceSvc: DeviceService) {
    super(httpProxy, interceptor, deviceSvc);
  }
  findTenantProjects(num: number, size: number, headers?: {}) {
    return this.httpProxySvc.readEntityByQuery<IProjectSimple>(environment.serverUri + '/auth-svc/projects/tenant', num, size, undefined, undefined, undefined, headers)
  };
  findUiPermission(projectId:string) {
    return this.httpProxySvc.getUIPermission(projectId)
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
  showMgmtPanel() {
    return !!this.totalProjects.find(e => e.id === '0P8HE307W6IO')
  }
  hasTenantProjects() {
    return this.httpProxy.currentUserAuthInfo.tenantIds.length > 0;
  }
}
