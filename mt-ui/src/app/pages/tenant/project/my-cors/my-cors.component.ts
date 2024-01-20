import { Component } from '@angular/core';
import { ProjectService } from 'src/app/services/project.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { ICorsProfile } from 'src/app/misc/interface';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
import { RESOURCE_NAME } from 'src/app/misc/constant';
import { Utility } from 'src/app/misc/utility';
import { TableHelper } from 'src/app/clazz/table-helper';
import { PermissionHelper } from 'src/app/clazz/permission-helper';
import { take } from 'rxjs/operators';
import { BannerService } from 'src/app/services/banner.service';
@Component({
  selector: 'app-my-cors',
  templateUrl: './my-cors.component.html',
  styleUrls: ['./my-cors.component.css']
})
export class MyCorsComponent{
  public projectId = this.route.getProjectIdFromUrl()
  private url = Utility.getProjectResource(this.projectId, RESOURCE_NAME.CORS)
  columnList = {
    id: 'ID',
    name: 'NAME',
    description: 'DESCRIPTION',
    origin: 'CORS_ORIGIN',
    edit: 'EDIT',
    delete: 'DELETE',
  }
  public tableSource: TableHelper<ICorsProfile> = new TableHelper(this.columnList, 10, this.httpSvc, this.url);
  public permissionHelper: PermissionHelper = new PermissionHelper(this.projectSvc.permissionDetail)
  constructor(
    public route: RouterWrapperService,
    public projectSvc: ProjectService,
    public httpSvc: HttpProxyService,
    public bannerSvc: BannerService,
  ) {
    this.permissionHelper.canDo(this.projectId, httpSvc.currentUserAuthInfo.permissionIds, 'VIEW_CORS').pipe(take(1)).subscribe(b => {
      if (b.result) {
        this.tableSource.loadPage(0)
      }
    })
  }
  removeFirst(input: string[]) {
    return input.filter((e, i) => i !== 0);
  }
  edit(id: string) {
    const data = this.tableSource.dataSource.data.find(e => e.id === id)
    this.route.navProjectCorsConfigsDetail(id, data)
  }
  create() {
    this.route.navProjectNewCorsConfigsDetail()
  }
  public delete(id: string) {
    this.httpSvc.deleteEntityById(this.url, id, Utility.getChangeId()).subscribe(() => {
      this.bannerSvc.notify(true)
      this.tableSource.refresh()
    }, () => {
      this.bannerSvc.notify(false)
    })
  }
}
