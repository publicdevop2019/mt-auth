import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { MatDialog } from '@angular/material/dialog';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
import { ISumRep, SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { DeviceService } from 'src/app/services/device.service';
import { ResourceOwnerService } from 'src/app/services/resource-owner.service';
import { ResourceOwnerComponent } from '../resource-owner/resource-owner.component';
import { OperationConfirmDialogComponent } from 'src/app/components/operation-confirm-dialog/operation-confirm-dialog.component';
import { filter, take } from 'rxjs/operators';
import * as UUID from 'uuid/v1';
import { IResourceOwner } from 'src/app/clazz/validation/aggregate/user/interfaze-user';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { RoleService } from 'src/app/services/role.service';
import { combineLatest } from 'rxjs';
@Component({
  selector: 'app-summary-resource-owner',
  templateUrl: './summary-resource-owner.component.html',
})
export class SummaryResourceOwnerComponent extends SummaryEntityComponent<IResourceOwner, IResourceOwner> implements OnDestroy {
  displayedColumns: string[] = ['id', 'email', 'grantedAuthorities', 'locked', 'createdAt', 'edit', 'token', 'delete'];
  sheetComponent = ResourceOwnerComponent;
  public roleList: IOption[] = [];
  searchConfigs: ISearchConfig[] = [
    {
      searchLabel: 'ID',
      searchValue: 'id',
      type: 'text',
      multiple: {
        delimiter:'.'
      }
    },
    {
      searchLabel: 'EMAIL',
      searchValue: 'email',
      type: 'text',
      multiple: {
        delimiter:'.'
      }
    },
  ]
  constructor(
    public entitySvc: ResourceOwnerService,
    public deviceSvc: DeviceService,
    public roleSvc: RoleService,
    public bottomSheet: MatBottomSheet,
    public dialog: MatDialog,
  ) {
    super(entitySvc, deviceSvc, bottomSheet, 2);
    combineLatest([this.roleSvc.readEntityByQuery(0,1000,'type:USER')]).pipe(take(1))//@todo use paginated select component
    .subscribe(next => {
      if (next){
        this.searchConfigs = [...this.searchConfigs,
        {
          searchLabel: 'GRANTED_AUTHORITIES',
          searchValue: 'grantedAuthorities',
          type: 'dropdown',
          multiple: {
            delimiter:'.'
          },
          source:next[0].data.map(e=>{return {
            label:e.name,
            value:e.id
          }})
        },
      ];
      }
    });
  }
  updateSummaryData(next: ISumRep<IResourceOwner>) {
    super.updateSummaryData(next);
    let var2 = new Set(next.data.flatMap(e => e.grantedAuthorities).filter(ee => ee));
    let var3 = new Array(...var2);
    if (var3.length > 0) {
      this.roleSvc.readEntityByQuery(0, var3.length, "id:" + var3.join('.')).subscribe(next => {
        this.roleList = next.data.map(e => <IOption>{ label: e.name, value: e.id });
      })
    }
  }
  revokeResourceOwnerToken(id: string) {
    this.entitySvc.revokeResourceOwnerToken(id);
  }
  getAuthorityList(inputs: string[]) {
    
    return inputs.map(e => <IOption>{ label: this.roleList.find(ee => ee.value === e)?this.roleList.find(ee => ee.value === e).label:'NOT_FOUND', value: e })
  }
  doBatchLock(){
    const dialogRef = this.dialog.open(OperationConfirmDialogComponent);
    let ids = this.selection.selected.map(e => e.id)
    dialogRef.afterClosed().pipe(filter(result => result)).subscribe(() => this.entitySvc.batchUpdateUserStatus(ids, 'LOCK', UUID()));
  }
  doBatchUnlock(){
    const dialogRef = this.dialog.open(OperationConfirmDialogComponent);
    let ids = this.selection.selected.map(e => e.id)
    dialogRef.afterClosed().pipe(filter(result => result)).subscribe(() => this.entitySvc.batchUpdateUserStatus(ids, 'UNLOCK', UUID()));
  }
}
