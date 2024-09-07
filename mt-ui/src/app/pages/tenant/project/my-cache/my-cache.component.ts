import { Component } from '@angular/core';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { ProjectService } from 'src/app/services/project.service';
import { ICacheProfile } from 'src/app/misc/interface';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
import { take } from 'rxjs/operators';
import { TableHelper } from 'src/app/clazz/table-helper';
import { PermissionHelper } from 'src/app/clazz/permission-helper';
import { Utility } from 'src/app/misc/utility';
import { RESOURCE_NAME } from 'src/app/misc/constant';
import { DeviceService } from 'src/app/services/device.service';
@Component({
  selector: 'app-my-cache',
  templateUrl: './my-cache.component.html',
  styleUrls: ['./my-cache.component.css']
})
export class MyCacheComponent {
  public projectId = this.route.getProjectIdFromUrl()
  private url = Utility.getProjectResource(this.projectId, RESOURCE_NAME.CACHE)
  public columnList = {
    id: 'ID',
    name: 'NAME',
    description: 'DESCRIPTION',
    edit: 'EDIT',
    delete: 'DELETE',
  }
  public tableSource: TableHelper<ICacheProfile> = new TableHelper(this.columnList, 10, this.httpSvc, this.url);
  public permissionHelper: PermissionHelper = new PermissionHelper(this.projectSvc.permissionDetail)
  constructor(
    public deviceSvc: DeviceService,
    public route: RouterWrapperService,
    public projectSvc: ProjectService,
    public httpSvc: HttpProxyService,
  ) {
    this.deviceSvc.updateDocTitle('TENANT_CACHE_DOC_TITLE')
    this.permissionHelper.canDo(this.projectId, httpSvc.currentUserAuthInfo.permissionIds, 'API_MGMT').pipe(take(1)).subscribe(b => {
      if (b.result) {
        this.tableSource.loadPage(0)
      }
    })
  }
  public edit(id: string) {
    const data = this.tableSource.dataSource.data.find(e => e.id === id)
    this.route.navProjectCacheConfigsDetail(id, data)
  }
  public create() {
    this.route.navProjectNewCacheConfigsDetail()
  }
  public delete(id: string) {
    this.httpSvc.deleteEntityById(this.url, id, Utility.getChangeId()).subscribe(() => {
      this.deviceSvc.notify(true)
      this.tableSource.refresh()
    }, () => {
      this.deviceSvc.notify(false)
    })
  }
}
