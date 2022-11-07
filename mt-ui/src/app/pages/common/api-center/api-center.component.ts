import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { MatDialog } from '@angular/material/dialog';
import { FormInfoService } from 'mt-form-builder';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
import { CONST_HTTP_METHOD } from 'src/app/clazz/constants';
import { SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { IEndpoint } from 'src/app/clazz/validation/aggregate/endpoint/interfaze-endpoint';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { DeviceService } from 'src/app/services/device.service';
import { ClientService } from 'src/app/services/mngmt-client.service';
import { SharedEndpointService } from 'src/app/services/shared-endpoint.service';
import { MngmtEndpointComponent } from '../../mgnmt/endpoint/endpoint.component';
import { SubRequestComponent } from '../sub-request/sub-request.component';
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
    description: 'DESCRIPTION',
    projectId: 'API_PROJECT_ID',
    path: 'URL',
    method: 'METHOD',
    action: 'SUBSCRIBE',
  }
  httpMethodList = CONST_HTTP_METHOD;
  sheetComponent = SubRequestComponent;
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
  searchConfigs: ISearchConfig[] =this.initSearchConfig
  constructor(
    public entitySvc: SharedEndpointService,
    public deviceSvc: DeviceService,
    public bottomSheet: MatBottomSheet,
    public clientSvc: ClientService,
    public fis: FormInfoService,
    public dialog: MatDialog
  ) {
    super(entitySvc, deviceSvc, bottomSheet,fis, 3);
  }
  getOption(value: string, options: IOption[]) {
    return options.find(e => e.value == value)
  }
}
