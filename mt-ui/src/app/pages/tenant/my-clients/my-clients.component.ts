import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatBottomSheet, MatBottomSheetConfig } from '@angular/material/bottom-sheet';
import { ActivatedRoute } from '@angular/router';
import { FormInfoService } from 'mt-form-builder';
import { IOption, ISumRep } from 'mt-form-builder/lib/classes/template.interface';
import { combineLatest, of } from 'rxjs';
import { take } from 'rxjs/operators';
import { CONST_GRANT_TYPE } from 'src/app/clazz/constants';
import { IBottomSheet, SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { IClient } from 'src/app/clazz/validation/aggregate/client/interfaze-client';
import { hasValue } from 'src/app/clazz/validation/validator-common';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { ClientComponent } from 'src/app/pages/tenant/client/client.component';
import { ClientService } from 'src/app/services/client.service';
import { DeviceService } from 'src/app/services/device.service';
import { MyClientService } from 'src/app/services/my-client.service';
import { ProjectService } from 'src/app/services/project.service';

@Component({
  selector: 'app-my-clients',
  templateUrl: './my-clients.component.html',
  styleUrls: ['./my-clients.component.css']
})
export class MyClientsComponent extends SummaryEntityComponent<IClient, IClient> implements OnDestroy{
  public formId = "myClientTableColumnConfig";
  columnList = {
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
  }
  sheetComponent = ClientComponent;
  public projectId: string;
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
    public projectSvc: ProjectService,
    public fis: FormInfoService,
    public deviceSvc: DeviceService,
    public bottomSheet: MatBottomSheet,
    private route: ActivatedRoute,
  ) {
    super(entitySvc, deviceSvc, bottomSheet,fis, 3);
    this.route.paramMap.pipe(take(1)).subscribe(queryMaps => {
      this.projectId = queryMaps.get('id')
      this.entitySvc.queryPrefix = 'projectIds:'+this.projectId;
    });
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
  getList(inputs: string[]) {
    return inputs.map(e => <IOption>{ label: e, value: e })
  }
  getResourceList(inputs?: string[]) {
    return this.resourceClientList.filter(e => inputs?.includes(e.value + ''))
  }
  openBottomSheet(id?: string, clone?: boolean): void {
    let config = new MatBottomSheetConfig();
    config.autoFocus = true;
    config.panelClass = 'fix-height'
    if (hasValue(id)) {
      of(this.dataSource.data.find(e => e.id === id))
        .subscribe(next => {
          if (clone) {
            config.data = <IBottomSheet<IClient>>{ context: 'clone', from: next };
            this.bottomSheet.open(this.sheetComponent, config);
          } else {
            config.data = <IBottomSheet<IClient>>{ context: 'edit', from: next };
            this.bottomSheet.open(this.sheetComponent, config);
          }
        })
    } else {
      config.data = <IBottomSheet<IClient>>{ context: 'new', from: { projectId: this.projectId, name: '', id: '', version: 0 } };
      this.bottomSheet.open(this.sheetComponent, config);
    }
  }
  removeFirst(input: string[]) {
    return input.filter((e, i) => i !== 0);
  }
}