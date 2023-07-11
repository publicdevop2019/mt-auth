import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { MatDialog } from '@angular/material/dialog';
import { FormInfoService } from 'mt-form-builder';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
import { CONST_HTTP_METHOD } from 'src/app/clazz/constants';
import { ISumRep, SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { uniqueObject } from 'src/app/clazz/utility';
import { IEndpoint } from 'src/app/clazz/endpoint.interface';
import { BatchUpdateCorsComponent } from 'src/app/components/batch-update-cors/batch-update-cors.component';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { DeviceService } from 'src/app/services/device.service';
import { EndpointService } from 'src/app/services/endpoint.service';
import { MgmtClientService } from 'src/app/services/mgmt-client.service';
import { MgmtEndpointComponent } from '../endpoint/endpoint.component';
@Component({
  selector: 'app-summary-endpoint',
  templateUrl: './summary-endpoint.component.html',
  styleUrls: ['./summary-endpoint.component.css']
})
export class SummaryEndpointComponent extends SummaryEntityComponent<IEndpoint, IEndpoint> implements OnDestroy {
  public formId = "mgmtEndpointTableColumnConfig";
  columnList = {
    id: 'ID',
    name: 'NAME',
    description: 'DESCRIPTION',
    resourceId: 'PARENT_CLIENT',
    path: 'URL',
    method: 'METHOD',
    more: 'MORE',
  }
  sheetComponent = MgmtEndpointComponent;
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
    public entitySvc: EndpointService,
    public deviceSvc: DeviceService,
    public bottomSheet: MatBottomSheet,
    public clientSvc: MgmtClientService,
    public fis: FormInfoService,
    public dialog: MatDialog
  ) {
    super(entitySvc, deviceSvc, bottomSheet, fis, 3);
    this.clientSvc.getDropdownClients(0, 1000, 'resourceIndicator:1')//TODO use paginated select component
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
      this.initTableSetting();
  }
  updateSummaryData(next: ISumRep<IEndpoint>) {
    super.updateSummaryData(next);
    this.allClientList = uniqueObject(next.data.map(e => <IOption>{ label: e.resourceName, value: e.resourceId }), 'value');
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
  }
}
