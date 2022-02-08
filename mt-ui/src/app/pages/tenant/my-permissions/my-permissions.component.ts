import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet, MatBottomSheetConfig } from '@angular/material/bottom-sheet';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { FormInfoService } from 'mt-form-builder';
import { IForm, IOption } from 'mt-form-builder/lib/classes/template.interface';
import { combineLatest, Observable, of } from 'rxjs';
import { map, mapTo, mergeMapTo, switchMap, switchMapTo, take } from 'rxjs/operators';
import { IBottomSheet, ISumRep, SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { IPermission } from 'src/app/clazz/validation/aggregate/permission/interface-permission';
import { IProjectSimple } from 'src/app/clazz/validation/aggregate/project/interface-project';
import { hasValue } from 'src/app/clazz/validation/validator-common';
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
  allEndpoints: IOption[] = [];
  allProjects: IOption[] = [];
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
      this.loadRoot = this.entitySvc.readEntityByQuery(0, 1000, "parentId:null").pipe(switchMap(data => {
        const var0 = data.data.find(ee => ee.name === 'API_ACCESS')
        const var1 = data.data.find(ee => ee.type === 'PROJECT')
        if (!var0 && !var1) {
          return of(data);
        }
        const obs = []
        if (var0) {
          this.apiRootId = var0.id;
          obs.push(this.translate.get('API_ACCESS'))
        }
        if (var1) {
          obs.push(this.projectSvc.readByQuery(0, 1, 'id:' + var1.name))
        }
        return combineLatest(obs).pipe(switchMap((next: (string | ISumRep<IProjectSimple>)[]) => {
          if (var0) {
            var0.name = next[0] as string;
            if (var1) {
              var1.name = (next[1] as ISumRep<IProjectSimple>).data[0].name;
            }
          } else {
            var1.name = (next[0] as ISumRep<IProjectSimple>).data[0].name;
          }
          return of(data)
        }))
      }))
      this.loadChildren = (id: string) => {
        if (id === this.apiRootId) {
          return this.entitySvc.readEntityByQuery(0, 1000, "parentId:" + id).pipe(switchMap(childNodes => {
            const epIds = childNodes.data.map(e => e.name)
            return this.epSvc.readEntityByQuery(0, epIds.length, 'id:' + epIds.join('.')).pipe(switchMap(resp => {
              childNodes.data.forEach(e => {
                const var0 = resp.data.find(ee => ee.id === e.name);
                e.name = var0 ? var0.name : e.name
              })
              return of(childNodes)
            }))
          }))
        } else {
          return this.entitySvc.readEntityByQuery(0, 1000, "parentId:" + id)
        }
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
  updateSummaryData(next: ISumRep<IPermission>) {
    super.updateSummaryData(next);
    const apiIds = next.data.filter(e => e.type === 'API').map(e => e.name);
    if (apiIds.length > 0) {
      this.epSvc.readEntityByQuery(0, apiIds.length, "id:" + apiIds.join('.')).subscribe(next => {
        this.allEndpoints = next.data.map(e => <IOption>{ label: e.name, value: e.id });
      })
    }
    const projectIds = next.data.filter(e => e.type === 'PROJECT').map(e => e.name);
    if (projectIds.length > 0) {
      this.projectSvc.readByQuery(0, projectIds.length, "id:" + projectIds.join('.')).subscribe(next => {
        this.allProjects = next.data.map(e => <IOption>{ label: e.name, value: e.id });
      })
    }
  }
  ngOnDestroy() {
    this.fis.resetAll()
  };
  getOption(value: string, options: IOption[]) {
    return options.find(e => e.value == value)
  }
  getEndpointName(input: string) {
    if (input)
      return this.allEndpoints.find(e => e.value === input)?.label
    return input

  }
  getProjectName(input: string) {
    if (input)
      return this.allProjects.find(e => e.value === input)?.label
    return input

  }
}