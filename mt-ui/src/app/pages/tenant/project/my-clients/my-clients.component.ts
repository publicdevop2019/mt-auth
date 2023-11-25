import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { DeviceService } from 'src/app/services/device.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { MyClientService } from 'src/app/services/my-client.service';
import { IProjectUiPermission, ProjectService } from 'src/app/services/project.service';
import { IClient, IClientCreate } from 'src/app/misc/interface';
import { IDomainContext, ISumRep } from 'src/app/clazz/summary.component';
import { map, take } from 'rxjs/operators';
import { MatDialog } from '@angular/material/dialog';
import { ClientCreateDialogComponent } from 'src/app/components/client-create-dialog/client-create-dialog.component';
import { Logger } from 'src/app/misc/logger';
import { Observable, Subscription, combineLatest } from 'rxjs';
import { MatTableDataSource } from '@angular/material/table';
import { PageEvent } from '@angular/material/paginator';
import { Utility } from 'src/app/misc/utility';
import { RouterWrapperService } from 'src/app/services/router-wrapper';

@Component({
  selector: 'app-my-clients',
  templateUrl: './my-clients.component.html',
  styleUrls: ['./my-clients.component.css']
})
export class MyClientsComponent implements OnDestroy {
  columnList: any = {};
  dataSource: MatTableDataSource<IClient>;
  totoalItemCount = 0;
  pageSize = 10;
  params = {};
  protected subs: Subscription = new Subscription()
  constructor(
    public clientSvc: MyClientService,
    public projectSvc: ProjectService,
    public fis: FormInfoService,
    public httpSvc: HttpProxyService,
    public deviceSvc: DeviceService,
    public bottomSheet: MatBottomSheet,
    private router: RouterWrapperService,
    public dialog: MatDialog,
  ) {
    this.clientSvc.setProjectId(this.router.getProjectIdFromUrl())
    Logger.debug(this.clientSvc.getProjectId())
    this.params['projectId'] = this.router.getProjectIdFromUrl();
    const sub2 = this.deviceSvc.refreshSummary.subscribe(() => {
      this.doSearch();
    });
    this.subs.add(sub2);
    this.canDo('EDIT_CLIENT').pipe(take(1)).subscribe(b => {
      this.columnList = b.result ? {
        name: 'NAME',
        types: 'TYPES',
        edit: 'EDIT',
        delete: 'DELETE',
      } : {
        name: 'NAME',
        types: 'TYPES',
      }
    })
    this.doSearch();
  }
  createNewClient() {
    const dialogRef = this.dialog.open(ClientCreateDialogComponent, { data: {} });
    dialogRef.afterClosed().subscribe(next => {
      if (next !== undefined) {
        Logger.debugObj('client basic info', next)
        const data = <IDomainContext<IClientCreate>>{ context: 'new', from: next, params: this.params }
        this.router.navProjectClientsDetail({ state: data })
      }
    })
  }
  editClient(id: string): void {
    this.clientSvc.readById(id).subscribe(next => {
      const data = <IDomainContext<IClient>>{ context: 'edit', from: next, params: this.params }
      this.router.navProjectClientsDetail({ state: data })
    })
  }
  removeFirst(input: string[]) {
    return input.filter((e, i) => i !== 0);
  }
  //TODO refactor, move to utility
  canDo(...name: string[]) {
    return combineLatest([this.projectSvc.permissionDetail]).pipe(map(e => {
      return this.hasPermission(e[0], this.router.getProjectIdFromUrl(), name)
    }))
  }
  //TODO refactor, move to utility
  extractResult(result: Observable<{ result: boolean, projectId: string }>) {
    return result.pipe(map(e => e.result))
  }
  doSearch() {
    Logger.debug(this.clientSvc.entityRepo)
    this.clientSvc.readEntityByQuery(this.clientSvc.pageNumber, this.pageSize, undefined, undefined, undefined).subscribe(next => {
      this.updateSummaryData(next);
    })
  }
  pageHandler(e: PageEvent) {
    this.clientSvc.pageNumber = e.pageIndex;
    this.clientSvc.readEntityByQuery(this.clientSvc.pageNumber, this.pageSize, undefined, undefined, undefined).subscribe(next => {
      this.updateSummaryData(next);
    });
  }
  protected updateSummaryData(next: ISumRep<IClient>) {
    if (next.data) {
      this.dataSource = new MatTableDataSource(next.data);
      this.totoalItemCount = next.totalItemCount;
    } else {
      this.dataSource = new MatTableDataSource([]);
      this.totoalItemCount = 0;
    }
  }
  private hasPermission(permissions: IProjectUiPermission, projectId: string, name: string[]) {
    const pId = permissions.permissionInfo.filter(e => name.includes(e.name)).map(e => e.id)
    if (pId.length > 0) {
      return {
        result: !(pId.filter(e => !this.httpSvc.currentUserAuthInfo.permissionIds.includes(e)).length > 0),
        projectId: projectId
      }
    } else {
      return {
        result: false,
        projectId: projectId
      }
    }
  }
  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }
  displayedColumns() {
    return Object.keys(this.columnList)
  };
  doDeleteById(id: string) {
    this.clientSvc.deleteById(id, Utility.getChangeId())
  }
}