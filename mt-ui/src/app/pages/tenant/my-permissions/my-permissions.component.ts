import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { FormInfoService } from 'mt-form-builder';
import { IForm, IOption } from 'mt-form-builder/lib/classes/template.interface';
import { combineLatest, Observable, of } from 'rxjs';
import { switchMap, take } from 'rxjs/operators';
import { ISumRep, SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { IPermission } from 'src/app/clazz/validation/aggregate/permission/interface-permission';
import { IProjectSimple } from 'src/app/clazz/validation/aggregate/project/interface-project';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { FORM_CONFIG } from 'src/app/form-configs/view-less.config';
import { DeviceService } from 'src/app/services/device.service';
import { EndpointService } from 'src/app/services/endpoint.service';
import { PermissionService } from 'src/app/services/permission.service';
import { ProjectService } from 'src/app/services/project.service';
import { PermissionComponent } from '../permission/permission.component';

@Component({
  selector: 'app-my-permissions',
  templateUrl: './my-permissions.component.html',
  styleUrls: ['./my-permissions.component.css']
})
export class MyPermissionsComponent extends SummaryEntityComponent<IPermission, IPermission> implements OnDestroy {
  public formId = "permissionTableColumnConfig";
  formId2 = 'summaryPermissionCustomerView';
  formInfo: IForm = JSON.parse(JSON.stringify(FORM_CONFIG));
  viewType: "LIST_VIEW" | "DYNAMIC_TREE_VIEW" = "LIST_VIEW";
  public projectId: string;
  public apiRootId: string;
  private formCreatedOb2: Observable<string>;
  columnList = {
    id: 'ID',
    name: 'NAME',
    description: 'DESCRIPTION',
    type: 'TYPE',
    edit: 'EDIT',
    clone: 'CLONE',
    delete: 'DELETE',
  }
  sheetComponent = PermissionComponent;
  public loadRoot;
  public loadChildren = (id: string) => {
    if (id === this.apiRootId) {
      return this.entitySvc.readEntityByQuery(0, 1000, "parentId:" + id).pipe(switchMap(data => {
        const epIds = data.data.map(e => e.name)
        return this.epSvc.readEntityByQuery(0, epIds.length, 'ids:' + epIds.join('.')).pipe(switchMap(resp => {
          data.data.forEach(e => e.name = resp.data.find(ee => ee.id === e.name).description)
          return of(data)
        }))
      }))
    } else {
      return this.entitySvc.readEntityByQuery(0, 1000, "parentId:" + id)
    }
  }
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
    public entitySvc: PermissionService,
    public epSvc: EndpointService,
    public projectSvc: ProjectService,
    public deviceSvc: DeviceService,
    public fis: FormInfoService,
    public bottomSheet: MatBottomSheet,
    private route: ActivatedRoute,
    private translate: TranslateService
  ) {
    super(entitySvc, deviceSvc, bottomSheet, fis, 2);
    this.route.paramMap.pipe(take(1)).subscribe(queryMaps => {
      this.projectId = queryMaps.get('id')
      this.bottomSheetParams['projectId'] = this.projectId;
      this.entitySvc.queryPrefix = 'projectIds:' + this.projectId;
      this.loadRoot = this.entitySvc.readEntityByQuery(0, 1000, "parentId:null");
      this.loadChildren = (id: string) => {
        return this.entitySvc.readEntityByQuery(0, 1000, "parentId:" + id)
      }
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
  ngOnDestroy() {
    this.fis.resetAll()
  };
  getOption(value: string, options: IOption[]) {
    return options.find(e => e.value == value)
  }
}