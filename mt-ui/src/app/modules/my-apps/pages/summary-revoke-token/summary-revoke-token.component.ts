import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
import { CONST_ATTR_TYPE } from 'src/app/clazz/constants';
import { SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { DeviceService } from 'src/app/services/device.service';
import { IRevokeToken, RevokeTokenService } from 'src/app/services/revoke-token.service';
@Component({
  selector: 'app-summary-revoke-token',
  templateUrl: './summary-revoke-token.component.html',
  styleUrls: ['./summary-revoke-token.component.css']
})
export class SummaryRevokeTokenComponent extends SummaryEntityComponent<IRevokeToken, IRevokeToken> implements OnDestroy {
  displayedColumns: string[] = ['targetId', 'issuedAt', 'type'];
  attrTypeList=CONST_ATTR_TYPE;
  searchConfigs: ISearchConfig[] = [
    {
      searchLabel: 'TARGET_ID',
      searchValue: 'targetId',
      type: 'text',
    },
  ]
  constructor(
    public entitySvc: RevokeTokenService,
    public deviceSvc: DeviceService,
    protected bottomSheet: MatBottomSheet,
  ) {
    super(entitySvc, deviceSvc, bottomSheet,2);
  }
  getOption(value:string,options:IOption[]){
    return options.find(e=>e.value==value)
  }
}
