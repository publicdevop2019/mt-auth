import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { MatDialog } from '@angular/material/dialog';
import { FormInfoService } from 'mt-form-builder';
import { IOption, ISumRep } from 'mt-form-builder/lib/classes/template.interface';
import { filter, switchMap } from 'rxjs/operators';
import { TenantSummaryEntityComponent } from 'src/app/clazz/tenant-summary.component';
import { Utility, uniqueObject } from 'src/app/misc/utility';
import { BatchUpdateCorsComponent } from 'src/app/components/batch-update-cors/batch-update-cors.component';
import { EndpointAnalysisComponent } from 'src/app/components/endpoint-analysis-dialog/endpoint-analysis-dialog.component';
import { EnterReasonDialogComponent } from 'src/app/components/enter-reason-dialog/enter-reason-dialog.component';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { DeviceService } from 'src/app/services/device.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { MyClientService } from 'src/app/services/my-client.service';
import { MyEndpointService } from 'src/app/services/my-endpoint.service';
import { ProjectService } from 'src/app/services/project.service';
import { EndpointComponent } from '../endpoint/endpoint.component';
import { IEndpoint } from 'src/app/misc/interface';
import { APP_CONSTANT, CONST_HTTP_METHOD } from 'src/app/misc/constant';
import { EndpointCreateDialogComponent } from 'src/app/components/endpoint-create-dialog/endpoint-create-dialog.component';
import { IDomainContext } from 'src/app/clazz/summary.component';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
import { ActivatedRoute } from '@angular/router';
import { Logger } from 'src/app/misc/logger';
@Component({
  selector: 'app-my-endpoints',
  templateUrl: './my-endpoints.component.html',
  styleUrls: ['./my-endpoints.component.css']
})
export class MyApisComponent extends TenantSummaryEntityComponent<IEndpoint, IEndpoint> implements OnDestroy {
  public formId = "myApiTableColumnConfig";
  columnList: any = {};
  params = {};
  sheetComponent = EndpointComponent;
  httpMethodList = CONST_HTTP_METHOD;
  public allClientList: IOption[];
  private initSearchConfig: ISearchConfig[] = [
    {
      searchLabel: 'ID',
      searchValue: 'id',
      type: 'text',
      multiple: {
        delimiter: '.'
      }
    },
    {
      searchLabel: 'METHOD',
      searchValue: 'method',
      type: 'dropdown',
      source: CONST_HTTP_METHOD
    },
  ]
  searchConfigs: ISearchConfig[] = []
  constructor(
    public projectSvc: ProjectService,
    public httpSvc: HttpProxyService,
    public entitySvc: MyEndpointService,
    public deviceSvc: DeviceService,
    public bottomSheet: MatBottomSheet,
    public clientSvc: MyClientService,
    public fis: FormInfoService,
    public dialog: MatDialog,
    public route: RouterWrapperService,
    public router: ActivatedRoute,
  ) {
    super(router,route, projectSvc, httpSvc, entitySvc, bottomSheet, fis);
    this.clientSvc.setProjectId(this.route.getProjectIdFromUrl())
    this.params['projectId'] = this.route.getProjectIdFromUrl();
    const sub2 = this.canDo('VIEW_API').subscribe(b => {
      if (b.result) {
        this.doSearch({ value: '', resetPage: true })
        Logger.debug(this.entitySvc.getProjectId())
      }
    })
    const sub4 = this.canDo('VIEW_API', 'VIEW_CLIENT').subscribe(b => {
      if (b.result) {
        //prepare search
        this.clientSvc.setProjectId(b.projectId)
        this.searchConfigs = [...this.initSearchConfig, {
          searchLabel: 'PARENT_CLIENT',
          searchValue: 'resourceId',
          type: 'dynamic',
          resourceUrl: Utility.getTenantUrl(b.projectId, APP_CONSTANT.TENANT_RESOURCE_CLIENT_DROPDOWN),
          multiple: {
            delimiter: '.'
          },
          source: []
        },];
      } else {
        this.searchConfigs = [...this.initSearchConfig]
      }
    })
    this.subs.add(sub4);
    this.subs.add(sub2);
    const sub3 = this.canDo('EDIT_API').subscribe(b => {
      this.columnList = b.result ? {
        id: 'ID',
        name: 'NAME',
        resourceId: 'PARENT_CLIENT',
        path: 'URL',
        method: 'METHOD',
        edit: 'EDIT',
        delete: 'DELETE',
        expire: 'EXPIRE',
        expireReason: 'EXPIRE_REASON',
        report: 'VIEW_REPORT',
      } : {
        id: 'ID',
        name: 'NAME',
        resourceId: 'PARENT_CLIENT',
        path: 'URL',
        method: 'METHOD',
      }
      this.initTableSetting();
    })
    this.subs.add(sub3);
    this.subs.add(sub2);

  }
  updateSummaryData(next: ISumRep<IEndpoint>) {
    super.updateSummaryData(next);
    this.allClientList = uniqueObject(next.data.map(e => <IOption>{ label: e.resourceName, value: e.resourceId }), 'value');
  }

  createNewEndpoint() {
    const dialogRef = this.dialog.open(EndpointCreateDialogComponent, { data: {} });
    dialogRef.afterClosed().subscribe(next => {
      if (next !== undefined) {
        const data = <IDomainContext<IEndpoint>>{ context: 'new', from: next, params: this.params }
        this.route.navProjectEndpointDetail(data)
      }
    })
  }
  editEndpoint(id: string): void {
    this.entitySvc.readById(id).subscribe(next => {
      const data = <IDomainContext<IEndpoint>>{ context: 'edit', from: next, params: this.params }
      this.route.navProjectEndpointDetail(data)
    })
  }
  getOption(value: string, options: IOption[]) {
    return options.find(e => e.value == value)
  }
  //TODO keep it for now until batch operation added
  batchOperation() {
    this.dialog.open(BatchUpdateCorsComponent, {
      width: '500px',
      data: {
        data: this.selection.selected.map(e => ({ id: e.id, description: e.description }))
      },
    });
  }
  doExpireById(id: string) {
    const dialogRef = this.dialog.open(EnterReasonDialogComponent, { data: {} });
    dialogRef.afterClosed().pipe(filter(e => e)).pipe(switchMap((reason: string) => {
      return this.entitySvc.expireEndpoint(id, reason, Utility.getChangeId())
    })).subscribe(() => {
      this.entitySvc.notify(true)
      this.entitySvc.refreshPage()
    }, () => {
      this.entitySvc.notify(false)
    })
  }
  viewReport(id: string) {
    this.dialog.open(EndpointAnalysisComponent, { data: { endpointId: id, projectId: this.route.getProjectIdFromUrl() } });
  }
}
