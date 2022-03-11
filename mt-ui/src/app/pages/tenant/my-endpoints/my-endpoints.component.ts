import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { FormInfoService } from 'mt-form-builder';
import { IOption, ISumRep } from 'mt-form-builder/lib/classes/template.interface';
import { CONST_HTTP_METHOD } from 'src/app/clazz/constants';
import { SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { IEndpoint } from 'src/app/clazz/validation/aggregate/endpoint/interfaze-endpoint';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { BatchUpdateCorsComponent } from 'src/app/components/batch-update-cors/batch-update-cors.component';
import { EndpointComponent } from 'src/app/pages/tenant/endpoint/endpoint.component';
import { DeviceService } from 'src/app/services/device.service';
import { MyClientService } from 'src/app/services/my-client.service';
import { MyEndpointService } from 'src/app/services/my-endpoint.service';
import { ProjectService } from 'src/app/services/project.service';
import { uniqueObject } from 'src/app/clazz/utility';

@Component({
  selector: 'app-my-endpoints',
  templateUrl: './my-endpoints.component.html',
  styleUrls: ['./my-endpoints.component.css']
})
export class MyApisComponent extends SummaryEntityComponent<IEndpoint, IEndpoint> implements OnDestroy {
  public formId = "myApiTableColumnConfig";
  columnList = {
    id: 'ID',
    name: 'NAME',
    description: 'DESCRIPTION',
    resourceId: 'PARENT_CLIENT',
    path: 'URL',
    method: 'METHOD',
    edit: 'EDIT',
    clone: 'CLONE',
    delete: 'DELETE',
  }
  sheetComponent = EndpointComponent;
  public projectId: string;
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
    public entitySvc: MyEndpointService,
    public deviceSvc: DeviceService,
    public bottomSheet: MatBottomSheet,
    public clientSvc: MyClientService,
    public fis: FormInfoService,
    public dialog: MatDialog,
    private route: ActivatedRoute,
  ) {
    super(entitySvc, deviceSvc, bottomSheet, fis, 3);
    const sub=this.route.paramMap.subscribe(queryMaps => {
      this.projectId = queryMaps.get('id')
      this.entitySvc.setProjectId(this.projectId);
      this.clientSvc.setProjectId(this.projectId)
      this.bottomSheetParams['projectId'] = this.projectId;
      this.deviceSvc.refreshSummary.next()
    });
    this.subs.add(sub)
    this.clientSvc.readEntityByQuery(0, 1000, 'resourceIndicator:1')//@todo use paginated select component
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

  }
  updateSummaryData(next: ISumRep<IEndpoint>) {
    super.updateSummaryData(next);
    this.allClientList = uniqueObject(next.data.map(e => <IOption>{ label: e.resourceName, value: e.resourceId }), 'id');
  }
  getOption(value: string, options: IOption[]) {
    return options.find(e => e.value == value)
  }
  batchOperation() {
    const dialogRef = this.dialog.open(BatchUpdateCorsComponent, {
      width: '500px',
      data: {
        data: this.selection.selected.map(e => ({ id: e.id, description: e.description }))
      },
    });
    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
    });
  }
}
