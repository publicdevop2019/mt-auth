import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { ActivatedRoute } from '@angular/router';
import { FormInfoService } from 'mt-form-builder';
import { IForm, IOption } from 'mt-form-builder/lib/classes/template.interface';
import { IIdBasedEntity, SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { TenantSummaryEntityComponent } from 'src/app/clazz/tenant-summary.component';
import { ISearchConfig } from 'src/app/components/search/search.component';
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
  permissionIds: string[],
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
  public loadRoot
  public loadChildren
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
      this.loadRoot = this.entitySvc.readEntityByQuery(0, 1000, "parentId:null")
      this.loadChildren = (id: string) => this.entitySvc.readEntityByQuery(0, 1000, "parentId:" + id)
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
        this.doSearch({ value: '', resetPage: true })
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
}