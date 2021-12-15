import { ChangeDetectorRef, Component, OnDestroy } from '@angular/core';
import { MatBottomSheet, MatBottomSheetConfig } from '@angular/material/bottom-sheet';
import { MatDialog } from '@angular/material/dialog';
import { PageEvent } from '@angular/material/paginator';
import { FormInfoService } from 'mt-form-builder';
import { IForm } from 'mt-form-builder/lib/classes/template.interface';
import { combineLatest, Observable, Subscription } from 'rxjs';
import { filter, mergeMap, switchMap } from 'rxjs/operators';
import { CONST_DTX_STATUS } from 'src/app/clazz/constants';
import { IBottomSheet, ISumRep, SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { hasValue } from 'src/app/clazz/validation/validator-common';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { FORM_CONFIG } from 'src/app/form-configs/task.config';
import { TaskComponent } from 'src/app/modules/mall/pages/task/task.component';
import { DeviceService } from 'src/app/services/device.service';
import { IBizTask, TaskService } from 'src/app/services/task.service';
import { ResolveConfirmDialogComponent } from '../../components/resolve-confirm-dialog/resolve-confirm-dialog.component';
@Component({
  selector: 'app-summary-task',
  templateUrl: './summary-task.component.html',
  styleUrls: ['./summary-task.component.css']
})
export class SummaryTaskComponent extends SummaryEntityComponent<IBizTask, IBizTask> implements OnDestroy {
  formId = 'summaryTask';
  formInfo: IForm = JSON.parse(JSON.stringify(FORM_CONFIG));
  private formCreatedOb: Observable<string>;
  sheetComponent = TaskComponent;
  displayedColumns: string[] = ['id', 'referenceId', 'taskStatus', 'transactionId', 'hasCancel', 'cancelStatus', 'createAt', 'retry', 'cancel'];
  isCancel: boolean = false;
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
      searchLabel: 'REFERENCE_ID',
      searchValue: 'orderId',
      type: 'text',
      multiple: {
        delimiter:'.'
      }
    },
    {
      searchLabel: 'CHANGE_ID',
      searchValue: 'changeId',
      type: 'text',
      multiple: {
        delimiter:'.'
      }
    },
    {
      searchLabel: 'DTX_STATUS',
      searchValue: 'status',
      type: 'dropdown',
      source:CONST_DTX_STATUS
    }
  ]
  constructor(
    public entitySvc: TaskService,
    public deviceSvc: DeviceService,
    public bottomSheet: MatBottomSheet,
    public dialog: MatDialog,
    private fis: FormInfoService,
  ) {
    super(entitySvc, deviceSvc, bottomSheet, 1);
    this.formCreatedOb = this.fis.formCreated(this.formId);
    const sub = this.formCreatedOb.subscribe(() => {
      const sub = this.fis.formGroupCollection[this.formId].valueChanges.subscribe(e => {
        this.entitySvc.pageNumber = 0;
        if ((e.taskName as string).includes("cancel")) {
          this.isCancel = true;
          this.displayedColumns = ['id', 'referenceId', 'taskStatus', 'resolveReason', 'transactionId', 'createAt', 'retry', 'resolve'];
        } else {
          this.isCancel = false;
          this.displayedColumns = ['id', 'referenceId', 'taskStatus', 'transactionId', 'hasCancel', 'cancelStatus', 'createAt', 'retry', 'cancel'];
        }
        this.entitySvc.updateEntityName(e.taskName);
        this.deviceSvc.refreshSummary.next();
      });
      this.fis.formGroupCollection[this.formId].setValue({ taskName: entitySvc.getEntityName() });
      this.subs.add(sub)
    })
    this.subs.add(sub)
  }
  doCancel(id: string) {
    this.entitySvc.doCancel(id).subscribe()

  }
  openResolveDialog(id: string) {
    const dialogRef = this.dialog.open(ResolveConfirmDialogComponent);
    dialogRef.afterClosed().pipe(filter(e => {
      return e;
    })).pipe(switchMap(e => this.entitySvc.doResolve(id, e))).subscribe(() => {
      this.deviceSvc.refreshSummary.next();
    });

  }
  ngOnDestroy() {
    super.ngOnDestroy();
    this.fis.reset(this.formId);
  }
  cancelledIds: { key: string, value: string }[] = [];
  updateSummaryData(inputs: ISumRep<IBizTask>) {
    super.updateSummaryData(inputs);
    if (!this.isCancel) {
      let cancelChangeIds: string[] = inputs.data.map(e => e.changeId + "_cancel");
      if (cancelChangeIds.length > 0)
        this.entitySvc.readCancelEntityByQuery(0, cancelChangeIds.length, 'changeId:' + cancelChangeIds.join('.')).subscribe(next => {
          this.cancelledIds = next.data.map(e => ({ key: e.changeId.replace('_cancel', ''), value: e.status }));
        });
    }
  }
  hasCancel(dtx: IBizTask) {
    return !!this.cancelledIds.find(e => e.key === dtx.changeId)
  }
  cancelStatus(dtx: IBizTask) {
    return this.cancelledIds.find(e => e.key === dtx.changeId)?.value
  }
  openBottomSheetExt(row: IBizTask): void {
    let config = new MatBottomSheetConfig();
    config.autoFocus = true;
    config.panelClass = 'fix-height'
    this.entitySvc.readById(row.id).subscribe(next => {
      next.cancelable = this.cancelStatus(row) !== 'SUCCESS';
      next.retryable = (!this.cancelStatus(row) || this.cancelStatus(row) === 'SUCCESS'|| this.cancelStatus(row) === 'RESOLVED');
      config.data = <IBottomSheet<IBizTask>>{ context: 'edit', from: next };
      this.bottomSheet.open(this.sheetComponent, config);
    })
  }
}
