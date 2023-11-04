import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { PageEvent } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute } from '@angular/router';
import { FormInfoService } from 'mt-form-builder';
import { IForm } from 'mt-form-builder/lib/classes/template.interface';
import { of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { ISumRep } from 'src/app/clazz/summary.component';
import { TenantSummaryEntityComponent } from 'src/app/clazz/tenant-summary.component';
import { INode } from 'src/app/components/dynamic-tree/dynamic-tree.component';
import { ISearchConfig, ISearchEvent } from 'src/app/components/search/search.component';
import { FORM_CONFIG } from 'src/app/form-configs/view-less.config';
import { AuthService } from 'src/app/services/auth.service';
import { DeviceService } from 'src/app/services/device.service';
import { EndpointService } from 'src/app/services/endpoint.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { MyPermissionService } from 'src/app/services/my-permission.service';
import { ProjectService } from 'src/app/services/project.service';
import { PermissionComponent } from '../permission/permission.component';
import { IPermission } from 'src/app/misc/interface';

@Component({
  selector: 'app-my-permissions',
  templateUrl: './my-permissions.component.html',
  styleUrls: ['./my-permissions.component.css']
})
export class MyPermissionsComponent extends TenantSummaryEntityComponent<IPermission, IPermission> implements OnDestroy {
  public formId = "permissionTableColumnConfig";
  viewFormId = 'summaryPermissionCustomerView';
  viewFormInfo: IForm = FORM_CONFIG;
  viewType: "LIST_VIEW" | "DYNAMIC_TREE_VIEW" = "LIST_VIEW";
  public apiRootId: string;
  columnList: any = {};
  sheetComponent = PermissionComponent;
  apiDataSource: MatTableDataSource<IPermission>;
  apiTotoalItemCount = 0;
  apiPageNumber: number = 0;
  apiPageSize: number = 11;
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
    public entitySvc: MyPermissionService,
    public epSvc: EndpointService,
    public projectSvc: ProjectService,
    public deviceSvc: DeviceService,
    public httpSvc: HttpProxyService,
    public fis: FormInfoService,
    public bottomSheet: MatBottomSheet,
    public route: ActivatedRoute,
    private authSvc: AuthService,
  ) {
    super(route, projectSvc, httpSvc, entitySvc, deviceSvc, bottomSheet, fis, 5);
    this.deviceSvc.refreshSummary.subscribe(() => {
      const search = {
        value: 'types:COMMON,parentId:null',
        resetPage: false
      }
      this.doSearch(search);
    })
    const sub = this.projectId.subscribe(next => {
      this.entitySvc.setProjectId(next);
      this.loadRoot = this.entitySvc.readEntityByQuery(0, 1000, "types:COMMON,parentId:null")
      this.loadChildren = (id: string) => {
        return this.entitySvc.readEntityByQuery(0, 1000, "parentId:" + id).pipe(map(e => {
          e.data.forEach(ee => {
            if (next === '0P8HE307W6IO') {
              (ee as INode).enableI18n = true;
            }
          })
          return e
        }));
      }
    });
    const sub2 = this.canDo('VIEW_PERMISSION').subscribe(b => {
      if (b.result) {
        this.doSearch({ value: 'types:COMMON', resetPage: true })
        this.entitySvc.readEntityByQuery(this.apiPageNumber, this.apiPageSize, 'types:API').subscribe(next => {
          this.updateApiSummaryData(next)
        })
      }
    })
    const sub3 = this.canDo('EDIT_PERMISSION').subscribe(b => {
      const temp = b.result ? {
        id: 'ID',
        name: 'NAME',
        type: 'TYPE',
        edit: 'EDIT',
        clone: 'CLONE',
        delete: 'DELETE',
      } : {
        id: 'ID',
        name: 'NAME',
        type: 'TYPE',
      }
      this.columnList = temp
      this.initTableSetting();
    })
    this.subs.add(sub)
    this.subs.add(sub2)
    this.subs.add(sub3)
    this.fis.init(this.viewFormInfo, this.viewFormId)
    this.fis.formGroups[this.viewFormId].get('view').setValue(this.viewType);
    const sub4 = this.fis.formGroups[this.viewFormId].valueChanges.subscribe(e => {
      this.viewType = e.view;
    });
    this.subs.add(sub4)
  }
  ngOnDestroy() {
    this.fis.reset(this.formId)
    this.fis.reset(this.viewFormId)
    super.ngOnDestroy()
  };
  doSearchWrapperCommon(config: ISearchEvent) {
    config.value = "types:COMMON"
    this.doSearch(config)
  }
  private updateApiSummaryData(next: ISumRep<IPermission>) {
    if (next.data) {
      this.apiDataSource = new MatTableDataSource(next.data);
      this.apiTotoalItemCount = next.totalItemCount;
    } else {
      this.apiDataSource = new MatTableDataSource([]);
      this.apiTotoalItemCount = 0;
    }
  }
  displayedApiColumns() {
    return ['id', 'name']
  };
  apiPageHandler(e: PageEvent) {
    this.apiPageNumber = e.pageIndex;
    this.entitySvc.readEntityByQuery(this.apiPageNumber, this.apiPageSize, 'types:API').subscribe(next => {
      this.updateApiSummaryData(next);
    });
  }
}