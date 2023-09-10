import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { ActivatedRoute, Router } from '@angular/router';
import { FormInfoService } from 'mt-form-builder';
import { IOption, ISumRep } from 'mt-form-builder/lib/classes/template.interface';
import { TenantSummaryEntityComponent } from 'src/app/clazz/tenant-summary.component';
import { Utility, uniqueObject } from 'src/app/misc/utility';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { AuthService } from 'src/app/services/auth.service';
import { DeviceService } from 'src/app/services/device.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { MyClientService } from 'src/app/services/my-client.service';
import { ProjectService } from 'src/app/services/project.service';
import { ClientComponent } from '../client/client.component';
import { APP_CONSTANT, CONST_GRANT_TYPE } from 'src/app/misc/constant';
import { IClient, IClientCreate } from 'src/app/misc/interface';
import { MatDialog } from '@angular/material/dialog';
import { ClientCreateDialogComponent } from 'src/app/components/client-create-dialog/client-create-dialog.component';
import { switchMap } from 'rxjs/operators';
import { Logger } from 'src/app/misc/logger';
import { IDomainContext } from 'src/app/clazz/summary.component';

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
    private router: Router,
    public dialog: MatDialog,
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
        this.searchConfigs = [...this.initSearchConfigs, {
          searchLabel: 'RESOURCEIDS',
          searchValue: 'resourceIds',
          type: 'dynamic',
          multiple: {
            delimiter: '.'
          },
          resourceUrl: Utility.getTenantUrl(b.projectId, APP_CONSTANT.TENANT_RESOURCE_CLIENT_DROPDOWN),
          source: []
        },
        ];
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
  createNewClient() {
    // const dialogRef = this.dialog.open(ClientCreateDialogComponent, { data: {} });
    // dialogRef.afterClosed().subscribe(next => {
    //   if (next !== undefined) {
    //     Logger.debugObj('client basic info',next)
    //     const data = <IDomainContext<IClient>>{ context: 'new', from: next, params: this.bottomSheetParams }
    //     this.router.navigate(['home', 'client-detail'], { state: data})
    //   }
    // })
    const data = <IDomainContext<IClientCreate>>{ context: 'new', from: { projectId: this.entitySvc.getProjectId() }, params: this.bottomSheetParams }
    this.router.navigate(['home', 'client-detail'], { state: data })
  }
  editClient(id: string): void {
    this.entitySvc.readById(id).subscribe(next => {
      const data = <IDomainContext<IClient>>{ context: 'edit', from: next, params: this.bottomSheetParams }
      this.router.navigate(['home', 'client-detail'], { state: data })
    })
  }
}