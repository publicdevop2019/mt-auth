import { Component } from '@angular/core';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { ProjectService } from 'src/app/services/project.service';
import { ICacheProfile } from 'src/app/misc/interface';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
import { take } from 'rxjs/operators';
import { TableHelper } from 'src/app/clazz/table-helper';
import { PermissionHelper } from 'src/app/clazz/permission-helper';
import { Utility, getUrl } from 'src/app/misc/utility';
import { environment } from 'src/environments/environment';
import { APP_CONSTANT } from 'src/app/misc/constant';
import { BannerService } from 'src/app/services/banner.service';
@Component({
  selector: 'app-my-cache',
  templateUrl: './my-cache.component.html',
  styleUrls: ['./my-cache.component.css']
})
export class MyCacheComponent {
  projectId = this.route.getProjectIdFromUrl()
  url = getUrl([environment.serverUri, APP_CONSTANT.MT_AUTH_ACCESS_PATH, 'projects', this.projectId, 'cache']);
  columnList = {
    id: 'ID',
    name: 'NAME',
    description: 'DESCRIPTION',
    edit: 'EDIT',
    delete: 'DELETE',
  }
  tableSource: TableHelper<ICacheProfile> = new TableHelper(this.columnList, 10, this.httpSvc, this.url);
  permissionHelper: PermissionHelper = new PermissionHelper(this.projectSvc.permissionDetail)
  constructor(
    public bannerSvc: BannerService,
    public route: RouterWrapperService,
    public projectSvc: ProjectService,
    public httpSvc: HttpProxyService,
  ) {
    this.permissionHelper.canDo(this.projectId, httpSvc.currentUserAuthInfo.permissionIds, 'VIEW_CACHE').pipe(take(1)).subscribe(b => {
      if (b.result) {
        this.tableSource.loadPage(0)
      }
    })
  }
  edit(id: string) {
    const data = this.tableSource.data.data.find(e => e.id === id)
    this.route.navProjectCacheConfigsDetail(id, data)
  }
  create() {
    this.route.navProjectNewCacheConfigsDetail()
  }
  delete(id: string) {
    this.httpSvc.deleteEntityById(this.url, id, Utility.getChangeId()).subscribe(() => {
      this.bannerSvc.notify(true)
      this.tableSource.refresh()
    }, () => {
      this.bannerSvc.notify(false)
    })
  }
}
