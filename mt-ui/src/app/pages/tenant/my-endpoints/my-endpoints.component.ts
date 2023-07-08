import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { FormInfoService } from 'mt-form-builder';
import { IOption, ISumRep } from 'mt-form-builder/lib/classes/template.interface';
import { filter, map, switchMap, take } from 'rxjs/operators';
import { CONST_HTTP_METHOD } from 'src/app/clazz/constants';
import { TenantSummaryEntityComponent } from 'src/app/clazz/tenant-summary.component';
import { uniqueObject } from 'src/app/clazz/utility';
import { IEndpoint } from 'src/app/clazz/validation/aggregate/endpoint/interfaze-endpoint';
import { BatchUpdateCorsComponent } from 'src/app/components/batch-update-cors/batch-update-cors.component';
import { EndpointAnalysisComponent } from 'src/app/components/endpoint-analysis-dialog/endpoint-analysis-dialog.component';
import { EnterReasonDialogComponent } from 'src/app/components/enter-reason-dialog/enter-reason-dialog.component';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { DeviceService } from 'src/app/services/device.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { MyClientService } from 'src/app/services/my-client.service';
import { MyEndpointService } from 'src/app/services/my-endpoint.service';
import { ProjectService } from 'src/app/services/project.service';
import * as UUID from 'uuid/v1';
import { EndpointComponent } from '../endpoint/endpoint.component';
@Component({
  selector: 'app-my-endpoints',
  templateUrl: './my-endpoints.component.html',
  styleUrls: ['./my-endpoints.component.css']
})
export class MyApisComponent extends TenantSummaryEntityComponent<IEndpoint, IEndpoint> implements OnDestroy {
  public formId = "myApiTableColumnConfig";
  columnList: any = {};
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
  projectIdSubject = this.route.paramMap.pipe(map(e => e.get('id')))
  constructor(
    public projectSvc: ProjectService,
    public httpSvc: HttpProxyService,
    public entitySvc: MyEndpointService,
    public deviceSvc: DeviceService,
    public bottomSheet: MatBottomSheet,
    public clientSvc: MyClientService,
    public fis: FormInfoService,
    public dialog: MatDialog,
    public route: ActivatedRoute,
  ) {
    super(route, projectSvc, httpSvc, entitySvc, deviceSvc, bottomSheet, fis, 3);
    const sub2 = this.canDo('VIEW_API').subscribe(b => {
      if (b.result) {
        this.doSearch({ value: '', resetPage: true })
      }
    })
    const sub4 = this.canDo('VIEW_API', 'VIEW_CLIENT').subscribe(b => {
      if (b.result) {
        //prepare search
        this.clientSvc.setProjectId(b.projectId)
        this.clientSvc.getDropdownClients(0, 1000, 'resourceIndicator:1').pipe(take(1))//TODO use paginated select component
          .subscribe(next => {
            if (next.data)
              this.searchConfigs = [...this.initSearchConfig, {
                searchLabel: 'PARENT_CLIENT',
                searchValue: 'resourceId',
                type: 'dropdown',
                multiple: {
                  delimiter: '.'
                },
                source: next.data.map(e => {
                  return {
                    label: e.name,
                    value: e.id
                  }
                })
              },];
          });
      } else {
        this.searchConfigs = [...this.initSearchConfig]
      }
    })
    this.subs.add(sub4);
    const sub3 = this.canDo('EDIT_API').subscribe(b => {
      this.columnList = b.result ? {
        id: 'ID',
        name: 'NAME',
        description: 'DESCRIPTION',
        resourceId: 'PARENT_CLIENT',
        path: 'URL',
        method: 'METHOD',
        edit: 'EDIT',
        clone: 'CLONE',
        delete: 'DELETE',
        expire: 'EXPIRE',
        expireReason: 'EXPIRE_REASON',
        report: 'VIEW_REPORT',
      } : {
        id: 'ID',
        name: 'NAME',
        description: 'DESCRIPTION',
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
  getOption(value: string, options: IOption[]) {
    return options.find(e => e.value == value)
  }
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
      return this.entitySvc.expireEndpoint(id, reason, UUID())
    })).subscribe(() => {
      this.entitySvc.notify(true)
      this.entitySvc.refreshPage()
    }, () => {
      this.entitySvc.notify(false)
    })
  }
  viewReport(id: string) {
    this.dialog.open(EndpointAnalysisComponent, { data: {endpointId:id,projectId:this.projectId} });
  }
}
