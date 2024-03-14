import { Injectable } from '@angular/core';
import { Observable, ReplaySubject } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { HttpProxyService } from './http-proxy.service';
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
export class ProjectService{
  public totalProjects: IProjectSimple[] = [];
  public viewProject: IProjectSimple = undefined;
  permissionDetail: ReplaySubject<IProjectUiPermission> = new ReplaySubject();
  constructor(public httpProxy: HttpProxyService) {
  }
  findTenantProjects(num: number, size: number, headers?: {}) {
    return this.httpProxy.readEntityByQuery<IProjectSimple>(environment.serverUri + '/auth-svc/projects/tenant', num, size, undefined, undefined, undefined, headers)
  };
  findUiPermission(projectId:string) {
    return this.httpProxy.getUIPermission(projectId)
  };
  create(s: IProjectSimple, changeId: string) {
    return this.httpProxy.createEntity(environment.serverUri + '/auth-svc/projects', s, changeId, { 'loading': 'false' })
  };
  ready(projectId: string) {
    return this.httpProxy.checkPorjectReady(projectId)
  };
  getMyProject(projectId: string) {
    return this.httpProxy.readEntityById<IProjectDashboard>(environment.serverUri + '/auth-svc/projects', projectId)
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
