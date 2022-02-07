import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
import { combineLatest } from 'rxjs';
import { take } from 'rxjs/operators';
import { CONST_GRANT_TYPE } from 'src/app/clazz/constants';
import { ISumRep, SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { IClient } from 'src/app/clazz/validation/aggregate/client/interfaze-client';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { ClientService } from 'src/app/services/client.service';
import { DeviceService } from 'src/app/services/device.service';
import { ClientComponent } from '../../../../pages/tenant/client/client.component';
@Component({
  selector: 'app-summary-client',
  templateUrl: './summary-client.component.html',
})
export class SummaryClientComponent extends SummaryEntityComponent<IClient, IClient> implements OnDestroy{
  public formId = "clientTableColumnConfig";
  columnList = {
    name: 'NAME',
    id: 'ID',
    description: 'DESCRIPTION',
    resourceIndicator: 'RESOURCE_INDICATOR',
    grantTypeEnums: 'GRANTTYPE_ENUMS',
    accessTokenValiditySeconds: 'ACCESS_TOKEN_VALIDITY_SECONDS',
    resourceIds: 'RESOURCEIDS',
    edit: 'EDIT',
    token: 'REVOKE_TOKEN',
    delete: 'DELETE',
  }
  sheetComponent = ClientComponent;
  public grantTypeList: IOption[] = CONST_GRANT_TYPE;
  resourceClientList: IOption[] = [];
  searchConfigs: ISearchConfig[] = [
  ]
  private initSearchConfigs: ISearchConfig[] = [
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
  ]
  constructor(
    public entitySvc: ClientService,
    public fis: FormInfoService,
    public deviceSvc: DeviceService,
    public bottomSheet: MatBottomSheet,
  ) {
    super(entitySvc, deviceSvc, bottomSheet,fis, 3);
    combineLatest([this.entitySvc.readEntityByQuery(0, 1000, 'resourceIndicator:1')]).pipe(take(1))//@todo use paginated select component
      .subscribe(next => {
        if (next) {
          this.searchConfigs = [...this.initSearchConfigs, {
            searchLabel: 'RESOURCEIDS',
            searchValue: 'resourceIds',
            type: 'dropdown',
            multiple: {
              delimiter: '.'
            },
            source: next[0].data.map(e => {
              return {
                label: e.name,
                value: e.id
              }
            })
          },
          ];
        }
      });
  }
  updateSummaryData(next: ISumRep<IClient>) {
    super.updateSummaryData(next);
    let var0 = new Set(next.data.flatMap(e => e.resourceIds).filter(ee => ee));
    let var1 = new Array(...var0);
    if (var1.length > 0) {
      this.entitySvc.readEntityByQuery(0, var1.length, "clientId:" + var1.join('.')).subscribe(next => {
        this.resourceClientList = next.data.map(e => <IOption>{ label: e.name, value: e.id });
      })
    }
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