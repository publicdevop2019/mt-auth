import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { ActivatedRoute } from '@angular/router';
import { FormInfoService } from 'mt-form-builder';
import { TenantSummaryEntityComponent } from 'src/app/clazz/tenant-summary.component';
import { ISearchConfig, ISearchEvent } from 'src/app/components/search/search.component';
import { DeviceService } from 'src/app/services/device.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { MyPermissionService } from 'src/app/services/my-permission.service';
import { ProjectService } from 'src/app/services/project.service';
import { IEndpoint, IPermission } from 'src/app/misc/interface';
import { FormGroup, FormControl } from '@angular/forms';
import { IQueryProvider } from 'mt-form-builder/lib/classes/template.interface';
import { Validator } from 'src/app/misc/validator';
import { Utility } from 'src/app/misc/utility';
import { MyEndpointService } from 'src/app/services/my-endpoint.service';
import { RouterWrapperService } from 'src/app/services/router-wrapper';

@Component({
  selector: 'app-my-permissions',
  templateUrl: './my-permissions.component.html',
  styleUrls: ['./my-permissions.component.css']
})
export class MyPermissionsComponent extends TenantSummaryEntityComponent<IPermission, IPermission> implements OnDestroy {
  public changeId = Utility.getChangeId();
  public allowError = false;
  public nameErrorMsg: string = undefined;
  fg = new FormGroup({
    name: new FormControl(''),
    projectId: new FormControl(''),
    parentId: new FormControl(''),
    apiId: new FormControl([]),
  });
  columnList: any = {};
  parentIdOption = [];
  apiOptions = [];
  constructor(
    public entitySvc: MyPermissionService,
    public epSvc: MyEndpointService,
    public projectSvc: ProjectService,
    public deviceSvc: DeviceService,
    public httpSvc: HttpProxyService,
    public fis: FormInfoService,
    public bottomSheet: MatBottomSheet,
    public router: ActivatedRoute,
    public route: RouterWrapperService,
  ) {
    super(router, route, projectSvc, httpSvc, entitySvc, bottomSheet, fis);
    this.deviceSvc.refreshSummary.subscribe(() => {
      const search = {
        value: 'types:COMMON',
        resetPage: false
      }
      this.doSearch(search);
    })
    this.entitySvc.setProjectId(this.route.getProjectIdFromUrl());
    this.epSvc.setProjectId(this.route.getProjectIdFromUrl());
    const sub2 = this.canDo('VIEW_PERMISSION').subscribe(b => {
      if (b.result) {
        this.doSearch({ value: 'types:COMMON', resetPage: true })
      }
    })
    const sub3 = this.canDo('EDIT_PERMISSION').subscribe(b => {
      const temp = b.result ? {
        id: 'ID',
        name: 'PERM_NAME',
        type: 'TYPE',
        delete: 'DELETE',
      } : {
        id: 'ID',
        name: 'PERM_NAME',
        type: 'TYPE',
      }
      this.columnList = temp
      this.initTableSetting();
    })
    this.subs.add(sub2)
    this.subs.add(sub3)
    this.fg.valueChanges.subscribe(() => {
      if (this.allowError) {
        this.validateCreateForm()
      }
    })
  }
  ngOnDestroy() {
    this.fis.reset(this.formId)
    super.ngOnDestroy()
  };
  doSearchWrapperCommon(config: ISearchEvent) {
    config.value = "types:COMMON"
    this.doSearch(config)
  }
  displayedApiColumns() {
    return ['id', 'name']
  };
  getParentPerm(): IQueryProvider {
    return {
      readByQuery: (num: number, size: number, query?: string, by?: string, order?: string, header?: {}) => {
        return this.httpSvc.readEntityByQuery<IPermission>(this.entitySvc.entityRepo, num, size, `types:COMMON`, by, order, header)
      }
    } as IQueryProvider
  }
  getEndpoints(): IQueryProvider {
    return {
      readByQuery: (num: number, size: number, query?: string, by?: string, order?: string, header?: {}) => {
        return this.httpSvc.readEntityByQuery<IEndpoint>(this.epSvc.entityRepo, num, size, query, by, order, header)
      }
    } as IQueryProvider
  }
  convertToPayload(): IPermission {
    return {
      id: '',//value is ignored
      parentId: this.fg.get('parentId').value ? this.fg.get('parentId').value : null,
      name: this.fg.get('name').value,
      projectId: this.entitySvc.getProjectId(),
      linkedApiIds: this.fg.get('apiId').value || [],
      version: 0
    }
  }
  create() {
    this.allowError = true
    if (this.validateCreateForm()) {
      this.entitySvc.create(this.convertToPayload(), this.changeId)
    }
  }

  private validateCreateForm() {
    const var0 = Validator.exist(this.fg.get('name').value)
    this.nameErrorMsg = var0.errorMsg
    return !var0.errorMsg
  }
}