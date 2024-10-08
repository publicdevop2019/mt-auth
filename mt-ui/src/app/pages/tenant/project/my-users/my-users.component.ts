import { Component } from '@angular/core';
import { ISearchConfig, ISearchEvent } from 'src/app/components/search/search.component';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { ProjectService } from 'src/app/services/project.service';
import { UserComponent } from '../user/user.component';
import { IOption, IProjectUser } from 'src/app/misc/interface';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
import { PermissionHelper } from 'src/app/clazz/permission-helper';
import { TableHelper } from 'src/app/clazz/table-helper';
import { Utility } from 'src/app/misc/utility';
import { RESOURCE_NAME } from 'src/app/misc/constant';
import { take } from 'rxjs/operators';
import { DeviceService } from 'src/app/services/device.service';
@Component({
  selector: 'app-my-users',
  templateUrl: './my-users.component.html',
  styleUrls: ['./my-users.component.css']
})
export class MyUsersComponent {
  columnList: any = {};
  public projectId = this.route.getProjectIdFromUrl()
  private url = Utility.getProjectResource(this.projectId, RESOURCE_NAME.USERS)
  public tableSource: TableHelper<IProjectUser> = new TableHelper(this.columnList, 10, this.httpSvc, this.url);
  public permissionHelper: PermissionHelper = new PermissionHelper(this.projectSvc.permissionDetail)
  sheetComponent = UserComponent;
  public roleList: IOption[] = [];
  searchConfigs: ISearchConfig[] = [
    {
      searchLabel: 'ID',
      searchValue: 'id',
      type: 'text',
      multiple: {
        delimiter: '.'
      }
    },
    {
      searchLabel: 'EMAIL',
      searchValue: 'email',
      type: 'text'
    },
    {
      searchLabel: 'MOBILE_NUMBER',
      searchValue: 'mobile',
      type: 'text'
    },
    {
      searchLabel: 'USERNAME',
      searchValue: 'username',
      type: 'text'
    },
  ]
  constructor(
    public httpSvc: HttpProxyService,
    public projectSvc: ProjectService,
    public route: RouterWrapperService,
    public deviceSvc: DeviceService,
  ) {
    this.deviceSvc.updateDocTitle('TENANT_USER_DOC_TITLE')
    this.permissionHelper.canDo(this.projectId, httpSvc.currentUserAuthInfo.permissionIds, 'USER_MGMT').pipe(take(1)).subscribe(b => {
      this.tableSource.columnConfig = b.result ? {
        id: 'ID',
        name: 'DISPLAY_NAME',
        edit: 'EDIT',
      } : {
        id: 'ID',
        name: 'DISPLAY_NAME',
      }
    })
    this.permissionHelper.canDo(this.projectId, httpSvc.currentUserAuthInfo.permissionIds, 'USER_MGMT').pipe(take(1)).subscribe(b => {
      if (b.result) {
        this.tableSource.loadPage(0)
      }
    })
  }
  editUser(id: string) {
    this.route.navProjectUserDetail(id)
  }
  doSearch(config: ISearchEvent) {
    this.tableSource.query = config.value;
    this.tableSource.loadPage(0)
  }
}
