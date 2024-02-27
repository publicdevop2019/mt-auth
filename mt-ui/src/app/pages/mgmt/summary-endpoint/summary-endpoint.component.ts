import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { MatDialog } from '@angular/material/dialog';
import { FormInfoService } from 'mt-form-builder';
import { ISumRep, SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { uniqueObject } from 'src/app/misc/utility';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { EndpointService } from 'src/app/services/endpoint.service';
import { MgmtClientService } from 'src/app/services/mgmt-client.service';
import { MgmtEndpointComponent } from '../endpoint/endpoint.component';
import { IEndpoint, IOption } from 'src/app/misc/interface';
import { APP_CONSTANT, CONST_HTTP_METHOD } from 'src/app/misc/constant';
import { ActivatedRoute } from '@angular/router';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
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
  searchConfigs: ISearchConfig[] = [
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
    {
      searchLabel: 'PARENT_CLIENT',
      searchValue: 'resourceId',
      resourceUrl: APP_CONSTANT.MGMT_RESOURCE_CLIENT_DROPDOWN,
      type: 'dynamic',
      multiple: {
        delimiter: '.'
      },
      source:[]
    }
  ]
  constructor(
    public entitySvc: EndpointService,
    public activated: ActivatedRoute,
    public router: RouterWrapperService,
    public bottomSheet: MatBottomSheet,
    public clientSvc: MgmtClientService,
    public fis: FormInfoService,
    public dialog: MatDialog
  ) {
    super(entitySvc, activated,router, bottomSheet, fis, 3);
      this.initTableSetting();
  }
  updateSummaryData(next: ISumRep<IEndpoint>) {
    super.updateSummaryData(next);
    this.allClientList = uniqueObject(next.data.map(e => <IOption>{ label: e.resourceName, value: e.resourceId }), 'value');
  }
  getOption(row: IEndpoint) {
    return <IOption>{ label: row.resourceName, value: row.resourceId }
  }
}
