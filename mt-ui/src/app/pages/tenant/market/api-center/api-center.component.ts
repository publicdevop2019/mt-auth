import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet, MatBottomSheetConfig } from '@angular/material/bottom-sheet';
import { MatDialog } from '@angular/material/dialog';
import { FormInfoService } from 'mt-form-builder';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
import { CONST_HTTP_METHOD } from 'src/app/clazz/constants';
import { IBottomSheet, SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { IEndpoint } from 'src/app/clazz/validation/aggregate/endpoint/interfaze-endpoint';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { DeviceService } from 'src/app/services/device.service';
import { MgmtClientService } from 'src/app/services/mgmt-client.service';
import { SharedEndpointService } from 'src/app/services/shared-endpoint.service';
import { SubscribeRequestComponent } from '../subscribe-request/subscribe-request.component';
import { hasValue } from 'src/app/clazz/validation/validator-common';
@Component({
  selector: 'app-api-center',
  templateUrl: './api-center.component.html',
  styleUrls: ['./api-center.component.css']
})
export class ApiCenterComponent extends SummaryEntityComponent<IEndpoint, IEndpoint> implements OnDestroy {
  public formId = "sharedEndpointTableColumnConfig";
  columnList = {
    id: 'ID',
    name: 'NAME',
    type: 'TYPE',
    description: 'DESCRIPTION',
    projectId: 'API_PROJECT_ID',
    path: 'URL',
    method: 'METHOD',
    action: 'SUBSCRIBE',
  }
  httpMethodList = CONST_HTTP_METHOD;
  sheetComponent = SubscribeRequestComponent;
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
  searchConfigs: ISearchConfig[] = this.initSearchConfig
  constructor(
    public entitySvc: SharedEndpointService,
    public deviceSvc: DeviceService,
    public bottomSheet: MatBottomSheet,
    public clientSvc: MgmtClientService,
    public fis: FormInfoService,
    public dialog: MatDialog
  ) {
    super(entitySvc, deviceSvc, bottomSheet, fis, 3);
  }
  getOption(value: string, options: IOption[]) {
    return options.find(e => e.value == value)
  }
  openBottomSheet(id?: string): void {
    const config = new MatBottomSheetConfig();
    config.autoFocus = true;
    config.panelClass = 'fix-height'
    const endpoint = this.dataSource.data.find(e => e.id === id)!
    config.data = <IBottomSheet<IEndpoint>>{ context: 'new', from: endpoint, params: this.bottomSheetParams };
    this.bottomSheet.open(this.sheetComponent, config);
  }
}
