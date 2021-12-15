import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
import { combineLatest } from 'rxjs';
import { take } from 'rxjs/operators';
import { CONST_GRANT_TYPE } from 'src/app/clazz/constants';
import { ISumRep, SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { IClient } from 'src/app/clazz/validation/aggregate/client/interfaze-client';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { ClientService } from 'src/app/services/client.service';
import { DeviceService } from 'src/app/services/device.service';
import { RoleService } from 'src/app/services/role.service';
import { ClientComponent } from '../client/client.component';
@Component({
  selector: 'app-summary-client',
  templateUrl: './summary-client.component.html',
})
export class SummaryClientComponent extends SummaryEntityComponent<IClient, IClient> implements OnDestroy {
  displayedColumns: string[] = ['name','id', 'description', 'resourceIndicator', 'grantTypeEnums', 'accessTokenValiditySeconds', 'grantedAuthorities', 'resourceIds', 'edit', 'token', 'delete'];
  sheetComponent = ClientComponent;
  public grantTypeList: IOption[] = CONST_GRANT_TYPE;
  public roleList: IOption[] = [];
  resourceClientList: IOption[] = [];
  searchConfigs: ISearchConfig[] = [
  ]
  private initSearchConfigs: ISearchConfig[] = [
    {
      searchLabel: 'ID',
      searchValue: 'id',
      type: 'text',
      multiple: {
        delimiter:'.'
      }
    },
    {
      searchLabel: 'NAME',
      searchValue: 'name',
      type: 'text',
      multiple: {
        delimiter:'.'
      }
    },
    {
      searchLabel: 'GRANTTYPE_ENUMS',
      searchValue: 'grantTypeEnums',
      type: 'dropdown',
      multiple: {
        delimiter:'$'
      },
      source:CONST_GRANT_TYPE
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
    public roleSvc: RoleService,
    public deviceSvc: DeviceService,
    public bottomSheet: MatBottomSheet,
  ) {
    super(entitySvc, deviceSvc, bottomSheet, 3);
    combineLatest([this.entitySvc.readEntityByQuery(0, 1000, 'resourceIndicator:1'),this.roleSvc.readEntityByQuery(0,1000,'type:CLIENT')]).pipe(take(1))//@todo use paginated select component
    .subscribe(next => {
      if (next){
        this.searchConfigs = [...this.initSearchConfigs,{
          searchLabel: 'RESOURCEIDS',
          searchValue: 'resourceIds',
          type: 'dropdown',
          multiple: {
            delimiter:'.'
          },
          source:next[0].data.map(e=>{return {
            label:e.name,
            value:e.id
          }})
        },
        {
          searchLabel: 'GRANTED_AUTHORITIES',
          searchValue: 'grantedAuthorities',
          type: 'dropdown',
          multiple: {
            delimiter:'.'
          },
          source:next[1].data.map(e=>{return {
            label:e.name,
            value:e.id
          }})
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
    let var2 = new Set(next.data.flatMap(e => e.grantedAuthorities).filter(ee => ee));
    let var3 = new Array(...var2);
    if (var3.length > 0) {
      this.roleSvc.readEntityByQuery(0, var3.length, "id:" + var3.join('.')).subscribe(next => {
        this.roleList = next.data.map(e => <IOption>{ label: e.name, value: e.id });
      })
    }
  }
  revokeClientToken(clientId: number) {
    this.entitySvc.revokeClientToken(clientId);
  }
  getList(inputs: string[]) {
    return inputs.map(e => <IOption>{ label: e, value: e })
  }
  getAuthorityList(inputs: string[]) {
    return this.roleList.filter(e => inputs?.includes(e.value + ''))
  }
  getResourceList(inputs?: string[]) {
    return this.resourceClientList.filter(e => inputs?.includes(e.value + ''))
  }
}