import { ChangeDetectorRef, Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { PageEvent } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute } from '@angular/router';
import { FormInfoService } from 'mt-form-builder';
import { IForm, IOption, ISumRep } from 'mt-form-builder/lib/classes/template.interface';
import { pid } from 'process';
import { map, tap } from 'rxjs/operators';
import { IIdBasedEntity, SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { TenantSummaryEntityComponent } from 'src/app/clazz/tenant-summary.component';
import { INode } from 'src/app/components/dynamic-tree/dynamic-tree.component';
import { ISearchConfig, ISearchEvent } from 'src/app/components/search/search.component';
import { FORM_CONFIG } from 'src/app/form-configs/view-less.config';
import { RoleComponent } from 'src/app/pages/tenant/role/role.component';
import { DeviceService } from 'src/app/services/device.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { MyRoleService } from 'src/app/services/my-role.service';
import { ProjectService } from 'src/app/services/project.service';
export interface INewRole extends IIdBasedEntity {
  name: string,
  originalName?: string,
  parentId?: string,
  tenantId?: string,
  systemCreate?: boolean,
  projectId: string,
  roleType?: 'USER' | 'CLIENT' | 'PROJECT' | 'CLIENT_ROOT',
  apiPermissionIds: string[],
  permissionDetails?: {id:string,name:string}[],
  commonPermissionIds: string[],
  externalPermissionIds?: string[],
  description?: string
}
@Component({
  selector: 'app-my-roles',
  templateUrl: './my-roles.component.html',
  styleUrls: ['./my-roles.component.css']
})
export class MyRolesComponent extends TenantSummaryEntityComponent<INewRole, INewRole> implements OnDestroy {
  public formId = "roleTableColumnConfig";
  formId2 = 'summaryRoleCustomerView';
  formInfo: IForm = JSON.parse(JSON.stringify(FORM_CONFIG));
  viewType: "LIST_VIEW" | "DYNAMIC_TREE_VIEW" = "LIST_VIEW";
  columnList: any={};
  sheetComponent = RoleComponent;
  public loadRoot;
  public loadChildren;
  sysDataSource: MatTableDataSource<INewRole>;
  sysTotoalItemCount = 0;
  sysPageNumber: number = 0;
  sysPageSize: number = 10;
  searchConfigs: ISearchConfig[] = [
    {
      searchLabel: 'ID',
      searchValue: 'id',
      type: 'text',
      multiple: {
        delimiter: '.'
      }
    },
  ]
  constructor(
    public entitySvc: MyRoleService,
    public httpProxySvc: HttpProxyService,
    public projectSvc: ProjectService,
    public deviceSvc: DeviceService,
    public httpSvc: HttpProxyService,
    public fis: FormInfoService,
    public bottomSheet: MatBottomSheet,
    public route: ActivatedRoute,
  ) {
    super(route, projectSvc, httpSvc, entitySvc, deviceSvc, bottomSheet, fis, 2);
    const sub=this.projectId.subscribe(pId => {
      this.entitySvc.setProjectId(pId);
      this.loadRoot = this.entitySvc.readEntityByQuery(0, 1000, "parentId:null,types:USER.PROJECT")
      .pipe(map(e => {
        e.data.forEach(ee => {
          if (pId === '0P8HE307W6IO') {
            (ee as INode).enableI18n = true;
          }
        })
        return e
      }));
      this.loadChildren = (id: string) => this.entitySvc.readEntityByQuery(0, 1000, "parentId:" + id).pipe(map(e => {
        e.data.forEach(ee => {
          if (pId === '0P8HE307W6IO') {
            (ee as INode).enableI18n = true;
          }
        })
        return e
      }));
    });
    this.subs.add(sub)
    this.fis.formCreated(this.formId2).subscribe(() => {
      const sub = this.fis.formGroupCollection[this.formId2].valueChanges.subscribe(e => {
        this.viewType = e.view;
      });
      if (!this.fis.formGroupCollection[this.formId2].get('view').value) {
        this.fis.formGroupCollection[this.formId2].get('view').setValue(this.viewType);
      }
      this.subs.add(sub)
    });
    const sub2 = this.canDo('VIEW_ROLE').subscribe(b => {
      if (b.result) {
        this.doSearch({ value: 'types:USER', resetPage: true })
        this.entitySvc.readEntityByQuery(this.sysPageNumber, this.sysPageSize, 'types:CLIENT.PROJECT').subscribe(next => {
          this.updateSysSummaryData(next)
        })
      }
    })
    const sub3 = this.canDo('EDIT_ROLE').subscribe(b => {
      this.columnList = b.result? {
        id: 'ID',
        name: 'NAME',
        description: 'DESCRIPTION',
        tenantId: 'TENANT_ID',
        roleType: 'TYPE',
        edit: 'EDIT',
        clone: 'CLONE',
        delete: 'DELETE',
      }:{
        id: 'ID',
        name: 'NAME',
        description: 'DESCRIPTION',
        tenantId: 'TENANT_ID',
        roleType: 'TYPE',
      }
    })
    this.subs.add(sub2)
    this.subs.add(sub3)
  }
  getOption(value: string, options: IOption[]) {
    return options.find(e => e.value == value)
  }
  ngOnDestroy(): void {
    this.fis.reset(this.formId)
    this.fis.reset(this.formId2)
    super.ngOnDestroy()
  }
  editable(row: INewRole) {
    return row.roleType !== 'CLIENT_ROOT' && row.roleType !== 'PROJECT'
  }
  doSearchWrapperCommon(config: ISearchEvent) {
    config.value = "types:USER"
    this.doSearch(config)
  }
  private updateSysSummaryData(next: ISumRep<INewRole>) {
    if (next.data) {
      this.sysDataSource = new MatTableDataSource(next.data);
      this.sysTotoalItemCount = next.totalItemCount;
    } else {
      this.sysDataSource = new MatTableDataSource([]);
      this.sysTotoalItemCount = 0;
    }
  }
  sysPageHandler(e: PageEvent) {
    this.sysPageNumber = e.pageIndex;
    this.entitySvc.readEntityByQuery(this.sysPageNumber, this.sysPageSize, 'types:CLIENT.PROJECT').subscribe(next => {
      this.updateSysSummaryData(next);
    });
  }
}