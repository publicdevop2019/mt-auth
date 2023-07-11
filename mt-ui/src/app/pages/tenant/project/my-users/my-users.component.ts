import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { FormInfoService } from 'mt-form-builder';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
import { take } from 'rxjs/operators';
import { SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { TenantSummaryEntityComponent } from 'src/app/clazz/tenant-summary.component';
import { IProjectUser } from 'src/app/clazz/user.interface';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { DeviceService } from 'src/app/services/device.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { MyUserService } from 'src/app/services/my-user.service';
import { ProjectService } from 'src/app/services/project.service';
import { UserComponent } from '../user/user.component';
@Component({
  selector: 'app-my-users',
  templateUrl: './my-users.component.html',
  styleUrls: ['./my-users.component.css']
})
export class MyUsersComponent extends TenantSummaryEntityComponent<IProjectUser, IProjectUser> implements OnDestroy {
  public formId = "myUserTableColumnConfig";
  columnList :any={};
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
      type: 'text',
      multiple: {
        delimiter: '.'
      }
    },
  ]
  constructor(
    public entitySvc: MyUserService,
    public deviceSvc: DeviceService,
    public httpSvc: HttpProxyService,
    public fis: FormInfoService,
    public bottomSheet: MatBottomSheet,
    public dialog: MatDialog,
    public projectSvc: ProjectService,
    public route: ActivatedRoute,
  ) {
    super(route, projectSvc, httpSvc, entitySvc, deviceSvc, bottomSheet, fis, 2);
    const sub1 = this.canDo('EDIT_TENANT_USER').subscribe(b => {
      this.columnList = b.result? {
        id: 'ID',
        email: 'EMAIL',
        edit: 'EDIT',
      }:{
        id: 'ID',
        email: 'EMAIL',
      }
      this.initTableSetting();
    })
    const sub2 = this.canDo('VIEW_TENANT_USER').subscribe(b => {
      if (b.result) {
        this.doSearch({ value: '', resetPage: true })
      }
    })
    this.subs.add(sub1)
    this.subs.add(sub2)
  }
  ngOnDestroy(): void {
    this.fis.reset(this.formId)
    super.ngOnDestroy();
  }
}
