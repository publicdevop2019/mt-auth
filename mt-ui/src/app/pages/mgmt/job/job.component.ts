import { V } from '@angular/cdk/keycodes';
import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { TranslateService } from '@ngx-translate/core';
import { DateTime } from 'luxon';
import { FormInfoService } from 'mt-form-builder';
import { ICheckboxControl } from 'mt-form-builder/lib/classes/template.interface';
import { map, switchMapTo } from 'rxjs/operators';
import { TABLE_SETTING_KEY } from 'src/app/clazz/constants';
import { copyOf } from 'src/app/clazz/utility';
import { TableColumnConfigComponent } from 'src/app/components/table-column-config/table-column-config.component';
import { FORM_TABLE_COLUMN_CONFIG } from 'src/app/form-configs/table-column.config';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { CustomHttpInterceptor } from 'src/app/services/interceptors/http.interceptor';
import { TimeService } from 'src/app/services/time.service';
export interface IJob {
  id: string
  name: string
  lastExecution: number
  lastStatus: string
  type: string
  failureCount: number
  maxLockAcquireFailureAllowed: number
  failureReason: string
  failureAllowed: number
  minimumIdleTimeAllowed: number
  notifiedAdmin: boolean
}
@Component({
  selector: 'app-job',
  templateUrl: './job.component.html',
  styleUrls: ['./job.component.css']
})
export class JobComponent implements OnInit, OnDestroy {
  public formId = "jobTableColumnConfig";
  columnList = {
    name: 'NAME',
    lastStatus: 'LAST_STATUS',
    type: 'TYPE',
    failureCount: 'FAILURE_COUNT',
    failureReason: 'FAILURE_REASON',
    failureAllowed: 'FAILURE_ALLOWED',
    notifiedAdmin: 'NOTIFIED',
    lastExecution: 'LAST_EXE_TIME',
    minimumIdleTimeAllowed: 'MINIMUM_IDLE_TIME_ALLOWED',
    maxLockAcquireFailureAllowed: 'MAX_LOCK_ACQUIRE_FAILURE_COUNT',
    action: 'JOB_ACTION',
  }
  dataSource: MatTableDataSource<IJob> = new MatTableDataSource();
  batchJobConfirmed: boolean;
  intervalRef: any;
  constructor(
    public fis: FormInfoService,
    private interceptor: CustomHttpInterceptor,
    public httpProxy: HttpProxyService,
    private translate: TranslateService,
    private timeSvc: TimeService
  ) {
    this.getStatus()
    this.intervalRef = setInterval(() => {
      this.getStatus()
    }, 5000)
    this.initTableSetting();
  }
  ngOnDestroy(): void {
    clearInterval(this.intervalRef)
  }
  getStatus() {
    this.httpProxy.getJobStatus().subscribe(next => {
      this.dataSource.data = next;
    }, () => {
      clearInterval(this.intervalRef)
    })
  }
  ngOnInit(): void {
  }
  parseDate(value: number) {
    return this.timeSvc.getUserFriendlyTimeDisplay(value)
  }
  resetValidationJob() {
    this.httpProxy.resetValidationJob().subscribe(() => {
      this.interceptor.openSnackbar('OPERATION_SUCCESS');
    }, () => {
      this.interceptor.openSnackbar('OPERATION_FAILED')
    })
  }
  resetJob(id: string) {
    this.httpProxy.resetJob(id).subscribe(() => {
      this.interceptor.openSnackbar('OPERATION_SUCCESS');
    }, () => {
      this.interceptor.openSnackbar('OPERATION_FAILED')
    })
  }
  isTemplate(row: IJob) {
    return row.type === 'SINGLE' && row.name.search(new RegExp(/\d+/)) === -1;
  }
  isSingle(row: IJob) {
    return row.type === 'SINGLE';
  }
  getName(row: IJob) {
    if (row.type === 'SINGLE' && !this.isTemplate(row)) {
      const index = row.name.search(new RegExp(/\d+/));
      const nextName = row.name.substring(0, index - 1)
      const instanceId = row.name.substring(index, row.name.length)
      return this.translate.get(nextName).pipe(map(e => {
        return e + '_' + instanceId
      }))
    } else {
      return this.translate.get(row.name)
    }
  }
  getNotify(data: boolean) {
    return data ? 'YES' : 'NO'
  }
  getColumnLabelValue() {
    return Object.keys(this.columnList).map(e => ({ label: this.columnList[e], value: e }))
  }
  displayedColumns() {
    if (this.fis.formGroups[this.formId]) {
      const orderKeys = ['select', ...Object.keys(this.columnList)];
      const value = this.fis.formGroups[this.formId].get(TABLE_SETTING_KEY).value as string[]
      return orderKeys.filter(e => value.includes(e))
    } else {
      return Object.keys(this.columnList)
    }
  };
  private initTableSetting() {
    const deepCopy = copyOf(FORM_TABLE_COLUMN_CONFIG)
    const settingKey = deepCopy.inputs[0].key;
    const options = this.getColumnLabelValue();
    (deepCopy.inputs[0] as ICheckboxControl).options = options;
    this.fis.init(deepCopy, this.formId)
    this.fis.formGroups[this.formId].get(settingKey).setValue(options.map(e => e.value))
  }
}
