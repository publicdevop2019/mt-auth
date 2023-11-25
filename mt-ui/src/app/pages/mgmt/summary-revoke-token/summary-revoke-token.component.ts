import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { ActivatedRoute } from '@angular/router';
import { FormInfoService } from 'mt-form-builder';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
import { SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { DeviceService } from 'src/app/services/device.service';
import { IRevokeToken, RevokeTokenService } from 'src/app/services/revoke-token.service';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
@Component({
  selector: 'app-summary-revoke-token',
  templateUrl: './summary-revoke-token.component.html',
  styleUrls: ['./summary-revoke-token.component.css']
})
export class SummaryRevokeTokenComponent extends SummaryEntityComponent<IRevokeToken, IRevokeToken> implements OnDestroy {
  searchConfigs: ISearchConfig[] = [
    {
      searchLabel: 'TARGET_ID',
      searchValue: 'targetId',
      type: 'text',
    },
  ]
  public formId = "tokenTableColumnConfig";
  columnList = {
    targetId: 'TARGET_ID',
    issuedAt: 'ISSUE_AT',
    type: 'TYPE',
  }
  constructor(
    public entitySvc: RevokeTokenService,
    public activated: ActivatedRoute,
    public router: RouterWrapperService,
    public fis: FormInfoService,
    protected bottomSheet: MatBottomSheet,
  ) {
    super(entitySvc, activated,router, bottomSheet,fis,2);
    this.initTableSetting();
  }
  getOption(value:string,options:IOption[]){
    return options.find(e=>e.value==value)
  }
}
