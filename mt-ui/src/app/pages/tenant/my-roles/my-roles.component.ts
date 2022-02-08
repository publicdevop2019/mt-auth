import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet, MatBottomSheetConfig } from '@angular/material/bottom-sheet';
import { ActivatedRoute } from '@angular/router';
import { FormInfoService } from 'mt-form-builder';
import { IForm, IOption } from 'mt-form-builder/lib/classes/template.interface';
import { combineLatest, Observable, of } from 'rxjs';
import { take } from 'rxjs/operators';
import { IBottomSheet, IIdBasedEntity, ISumRep, SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { hasValue } from 'src/app/clazz/validation/validator-common';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { FORM_CONFIG } from 'src/app/form-configs/view-less.config';
import { RoleComponent } from 'src/app/pages/tenant/role/role.component';
import { ClientService } from 'src/app/services/client.service';
import { DeviceService } from 'src/app/services/device.service';
import { NewRoleService } from 'src/app/services/new-role.service';
import { ProjectService } from 'src/app/services/project.service';
export interface INewRole extends IIdBasedEntity {
  name: string,
  parentId?: string,
  tenantId?: string,
  projectId: string,
  roleType?: 'USER'|'CLIENT',
  permissionIds: string[],
  description?: string
}
@Component({
  selector: 'app-my-roles',
  templateUrl: './my-roles.component.html',
  styleUrls: ['./my-roles.component.css']
})
export class MyRolesComponent extends SummaryEntityComponent<INewRole, INewRole> implements OnDestroy {
  public formId = "roleTableColumnConfig";
  formId2 = 'summaryRoleCustomerView';
  formInfo: IForm = JSON.parse(JSON.stringify(FORM_CONFIG));
  viewType: "LIST_VIEW" | "DYNAMIC_TREE_VIEW" = "LIST_VIEW";

  public projectId: string;
  private formCreatedOb2: Observable<string>;
  columnList = {
    id: 'ID',
    name: 'NAME',
    description: 'DESCRIPTION',
    tenantId: 'TENANT_ID',
    roleType: 'TYPE',
    edit: 'EDIT',
    clone: 'CLONE',
    delete: 'DELETE',
  }
  sheetComponent = RoleComponent;
  public loadRoot = this.entitySvc.readByQuery(0, 1000, "parentId:null")
  public loadChildren = (id: string) => this.entitySvc.readByQuery(0, 1000, "parentId:" + id)
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
  allProjects: IOption[]=[];
  allClients: IOption[]=[];
  constructor(
    public entitySvc: NewRoleService,
    public projectSvc: ProjectService,
    public clientSvc: ClientService,
    public deviceSvc: DeviceService,
    public fis: FormInfoService,
    public bottomSheet: MatBottomSheet,
    private route: ActivatedRoute,
  ) {
    super(entitySvc, deviceSvc, bottomSheet, fis, 2);
    this.route.paramMap.pipe(take(1)).subscribe(queryMaps => {
      this.projectId = queryMaps.get('id')
      this.entitySvc.queryPrefix = 'projectIds:' + this.projectId;
      this.bottomSheetParams['projectId']=this.projectId;
    });
    this.formCreatedOb2 = this.fis.formCreated(this.formId2);

    combineLatest([this.formCreatedOb2]).pipe(take(1)).subscribe(() => {
      const sub = this.fis.formGroupCollection[this.formId2].valueChanges.subscribe(e => {
        this.viewType = e.view;
      });
      if (!this.fis.formGroupCollection[this.formId2].get('view').value) {
        this.fis.formGroupCollection[this.formId2].get('view').setValue(this.viewType);
      }
      this.subs.add(sub)
    })
  }
  getOption(value: string, options: IOption[]) {
    return options.find(e => e.value == value)
  }
  updateSummaryData(next: ISumRep<INewRole>) {
    super.updateSummaryData(next);
    let ids = next.data.map(e => e.tenantId);
    let var0 = new Set(ids);
    let var1 = new Array(...var0).filter(e => e);
    if (var1.length > 0) {
      this.projectSvc.resolveTenantId(0, var1.length, "id:" + var1.join('.')).subscribe(next => {
        this.allProjects = next.data.map(e => <IOption>{ label: e.name, value: e.id });
      })
    }
    let clientIds = next.data.filter(e=>e.roleType==='CLIENT').map(e => e.name);
    if (clientIds.length > 0) {
      this.clientSvc.readEntityByQuery(0, clientIds.length, "id:" + clientIds.join('.')).subscribe(next => {
        this.allClients = next.data.map(e => <IOption>{ label: e.name, value: e.id });
      })
    }
  }
  getProjectName(input: string) {
    if (input)
      return this.allProjects.find(e => e.value === input)?.label
    return input

  }
  getClientName(input: string) {
    if (input)
      return this.allClients.find(e => e.value === input)?.label
    return input

  }
}