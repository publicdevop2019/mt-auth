import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet, MatBottomSheetConfig } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
import { of } from 'rxjs';
import { IBottomSheet, SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { ICorsProfile } from 'src/app/clazz/validation/aggregate/cors/interface-cors';
import { hasValue } from 'src/app/clazz/validation/validator-common';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { CORSProfileService } from 'src/app/services/cors-profile.service';
import { DeviceService } from 'src/app/services/device.service';
import { CorsComponent } from '../cors/cors.component';
@Component({
  selector: 'app-summary-cors',
  templateUrl: './summary-cors.component.html',
  styleUrls: ['./summary-cors.component.css']
})
export class SummaryCorsComponent extends SummaryEntityComponent<ICorsProfile, ICorsProfile> implements OnDestroy {
  public formId = "corsTableColumnConfig";
  columnList = {
    id: 'ID',
    name: 'NAME',
    description: 'DESCRIPTION',
    origin: 'CORS_ORIGIN',
    edit: 'EDIT',
    clone: 'CLONE',
    delete: 'DELETE',
  }
  sheetComponent = CorsComponent;
  searchConfigs: ISearchConfig[] = [
    {
      searchLabel: 'ID',
      searchValue: 'id',
      type: 'text',
      multiple: {
        delimiter: '.'
      }
    },
  ]
  constructor(
    public entitySvc: CORSProfileService,
    public deviceSvc: DeviceService,
    public bottomSheet: MatBottomSheet,
    public fis: FormInfoService,
  ) {
    super(entitySvc, deviceSvc, bottomSheet,fis, 3);
  }
  getOption(value: string, options: IOption[]) {
    return options.find(e => e.value == value)
  }
  openBottomSheet(id?: string, clone?: boolean): void {
    let config = new MatBottomSheetConfig();
    config.autoFocus = true;
    config.panelClass = 'fix-height'
    if (hasValue(id)) {
      of(this.dataSource.data.find(e => e.id === id))
        .subscribe(next => {
          if (clone) {
            config.data = <IBottomSheet<ICorsProfile>>{ context: 'clone', from: next };
            this.bottomSheet.open(this.sheetComponent, config);
          } else {
            config.data = <IBottomSheet<ICorsProfile>>{ context: 'edit', from: next };
            this.bottomSheet.open(this.sheetComponent, config);
          }
        })
    } else {
      config.data = <IBottomSheet<ICorsProfile>>{ context: 'new', from: undefined, params: {} };
      this.bottomSheet.open(this.sheetComponent, config);
    }
  }
  removeFirst(input: string[]) {
    return input.filter((e, i) => i !== 0);
  }
}
