import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
import { ISumRep, SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { uniqueObject } from 'src/app/misc/utility';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { DeviceService } from 'src/app/services/device.service';
import { MgmtClientService } from 'src/app/services/mgmt-client.service';
import { MgmtClientComponent } from '../client/client.component';
import { APP_CONSTANT, CONST_GRANT_TYPE } from 'src/app/misc/constant';
import { IClient } from 'src/app/misc/interface';
@Component({
  selector: 'app-summary-client',
  templateUrl: './summary-client.component.html',
})
export class SummaryClientComponent extends SummaryEntityComponent<IClient, IClient> implements OnDestroy {
  public formId = "mgmtClientTableColumnConfig";
  columnList = {
    name: 'NAME',
    id: 'ID',
    description: 'DESCRIPTION',
    resourceIndicator: 'RESOURCE_INDICATOR',
    grantTypeEnums: 'GRANTTYPE_ENUMS',
    accessTokenValiditySeconds: 'ACCESS_TOKEN_VALIDITY_SECONDS',
    resourceIds: 'RESOURCEIDS',
    more: 'MORE',
    token: 'REVOKE_TOKEN',
  }
  sheetComponent = MgmtClientComponent;
  public grantTypeList: IOption[] = CONST_GRANT_TYPE;
  resourceClientList: IOption[] = [];
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
      searchLabel: 'NAME',
      searchValue: 'name',
      type: 'text',
      multiple: {
        delimiter: '.'
      }
    },
    {
      searchLabel: 'GRANTTYPE_ENUMS',
      searchValue: 'grantTypeEnums',
      type: 'dropdown',
      multiple: {
        delimiter: '$'
      },
      source: CONST_GRANT_TYPE
    },
    {
      searchLabel: 'RESOURCE_INDICATOR',
      searchValue: 'resourceIndicator',
      type: 'boolean',
    },
    {
      searchLabel: 'ACCESS_TOKEN_VALIDITY_SECONDS',
      searchValue: 'accessTokenValiditySeconds',
      type: 'range',
    },
    {
      searchLabel: 'RESOURCEIDS',
      searchValue: 'resourceIds',
      type: 'dynamic',
      resourceUrl: APP_CONSTANT.MGMT_RESOURCE_CLIENT_DROPDOWN,
      multiple: {
        delimiter: '.'
      },
      source: []
    }
  ]
  constructor(
    public entitySvc: MgmtClientService,
    public fis: FormInfoService,
    public deviceSvc: DeviceService,
    public bottomSheet: MatBottomSheet,
  ) {
    super(entitySvc, deviceSvc, bottomSheet, fis, 3);
    this.initTableSetting();
  }
  updateSummaryData(next: ISumRep<IClient>) {
    super.updateSummaryData(next);
    this.resourceClientList = uniqueObject(next.data.filter(ee => ee.resources).flatMap(e => e.resources), 'id').map(e => <IOption>{ label: e.name, value: e.id })
  }
  revokeClientToken(clientId: number) {
    this.entitySvc.revokeClientToken(clientId);
  }
  getList(inputs: string[]) {
    return inputs.map(e => <IOption>{ label: e, value: e })
  }
  getResourceList(inputs?: string[]) {
    return this.resourceClientList.filter(e => inputs?.includes(e.value + ''))
  }
}