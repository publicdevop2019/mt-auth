import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { ActivatedRoute } from '@angular/router';
import { FormInfoService } from 'mt-form-builder';
import { IOption, ISumRep } from 'mt-form-builder/lib/classes/template.interface';
import { take } from 'rxjs/operators';
import { CONST_GRANT_TYPE } from 'src/app/clazz/constants';
import { TenantSummaryEntityComponent } from 'src/app/clazz/tenant-summary.component';
import { uniqueObject } from 'src/app/clazz/utility';
import { IClient } from 'src/app/clazz/validation/client.interface';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { AuthService } from 'src/app/services/auth.service';
import { DeviceService } from 'src/app/services/device.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { MyClientService } from 'src/app/services/my-client.service';
import { ProjectService } from 'src/app/services/project.service';
import { ClientComponent } from '../client/client.component';

@Component({
  selector: 'app-my-clients',
  templateUrl: './my-clients.component.html',
  styleUrls: ['./my-clients.component.css']
})
export class MyClientsComponent extends TenantSummaryEntityComponent<IClient, IClient> implements OnDestroy {
  public formId = "myClientTableColumnConfig";
  columnList: any = {};
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
    public entitySvc: MyClientService,
    public authSvc: AuthService,
    public projectSvc: ProjectService,
    public fis: FormInfoService,
    public httpSvc: HttpProxyService,
    public deviceSvc: DeviceService,
    public bottomSheet: MatBottomSheet,
    public route: ActivatedRoute,
  ) {
    super(route, projectSvc, httpSvc, entitySvc, deviceSvc, bottomSheet, fis, 3);
    (!this.authSvc.advancedMode) && this.deviceSvc.refreshSummary.subscribe(() => {
      const search = {
        value: '',
        resetPage: false
      }
      this.doSearch(search);
    })
    const sub2 = this.canDo('VIEW_CLIENT').subscribe(b => {
      if (b.result) {
        this.doSearch({ value: '', resetPage: true })
        //prepare search
        this.entitySvc.getDropdownClients(0, 1000, 'resourceIndicator:1').pipe(take(1))//TODO use paginated select component
          .subscribe(next => {
            if (next) {
              this.searchConfigs = [...this.initSearchConfigs, {
                searchLabel: 'RESOURCEIDS',
                searchValue: 'resourceIds',
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
              },
              ];
            }
          });
      }
    })
    this.subs.add(sub2)
    const sub3 = this.canDo('EDIT_CLIENT').subscribe(b => {
      this.columnList = b.result ? {
        name: 'NAME',
        id: 'ID',
        description: 'DESCRIPTION',
        resourceIndicator: 'RESOURCE_INDICATOR',
        grantTypeEnums: 'GRANTTYPE_ENUMS',
        types: 'TYPES',
        accessTokenValiditySeconds: 'ACCESS_TOKEN_VALIDITY_SECONDS',
        resourceIds: 'RESOURCEIDS',
        edit: 'EDIT',
        delete: 'DELETE',
      } : {
        name: 'NAME',
        id: 'ID',
        description: 'DESCRIPTION',
        resourceIndicator: 'RESOURCE_INDICATOR',
        grantTypeEnums: 'GRANTTYPE_ENUMS',
        types: 'TYPES',
        accessTokenValiditySeconds: 'ACCESS_TOKEN_VALIDITY_SECONDS',
        resourceIds: 'RESOURCEIDS',
      }
      this.initTableSetting();
    })
    this.subs.add(sub3)
  }
  updateSummaryData(next: ISumRep<IClient>) {
    super.updateSummaryData(next);
    this.resourceClientList = uniqueObject(next.data.filter(ee => ee.resources).flatMap(e => e.resources), 'id').map(e => <IOption>{ label: e.name, value: e.id })
  }
  getList(inputs: string[]) {
    return inputs.map(e => <IOption>{ label: e, value: e })
  }
  getResourceList(inputs?: string[]) {
    return this.resourceClientList.filter(e => inputs?.includes(e.value + ''))
  }
  removeFirst(input: string[]) {
    return input.filter((e, i) => i !== 0);
  }
}