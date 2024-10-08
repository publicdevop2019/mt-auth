import { Component } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { map } from 'rxjs/operators';
import { TableHelper } from 'src/app/clazz/table-helper';
import { APP_CONSTANT, RESOURCE_NAME } from 'src/app/misc/constant';
import { IIdBasedEntity } from 'src/app/misc/interface';
import { Utility } from 'src/app/misc/utility';
import { DeviceService } from 'src/app/services/device.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { environment } from 'src/environments/environment';
export interface IMySubscription extends IIdBasedEntity {
  endpointId: string,
  endpointName: string,
  projectId: string,
  projectName: string,
  replenishRate: number,
  burstCapacity: number,
  endpointStatus: string,
}
@Component({
  selector: 'app-my-subscriptions',
  templateUrl: './my-subscriptions.component.html',
  styleUrls: ['./my-subscriptions.component.css']
})
export class MySubscriptionsComponent {
  private url = Utility.getUrl([environment.serverUri, APP_CONSTANT.MT_AUTH_ACCESS_PATH, RESOURCE_NAME.SUBSCRIPTIONS])
  columnList = {
    id: 'ID',
    projectName: 'SUB_PROJECT_NAME',
    endpointName: 'API_NAME',
    replenishRate: 'REPLENISH_RATE_APPROVE',
    burstCapacity: 'BURST_CAPACITY_APPROVE',
    endpointStatus: 'ENDPOINT_STATUS',
  }
  public tableSource: TableHelper<IMySubscription> = new TableHelper(this.columnList, 10, this.httpSvc, this.url);
  constructor(
    public deviceSvc: DeviceService,
    public translateSvc: TranslateService,
    public httpSvc: HttpProxyService,
  ) {
    this.deviceSvc.updateDocTitle('MY_SUBS_DOC_TITLE')
    this.tableSource.loadPage(0)
  }
  getReason(reason: string) {
    return this.translateSvc.get('STATUS_EXPIRED').pipe(map(e => {
      return e + reason
    }))
  }
}