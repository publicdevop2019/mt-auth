import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet, MatBottomSheetConfig } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
import { of } from 'rxjs';
import { IBottomSheet, SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { ICacheProfile } from 'src/app/clazz/validation/aggregate/cache/interfaze-cache';
import { hasValue } from 'src/app/clazz/validation/validator-common';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { CacheService } from 'src/app/services/cache.service';
import { DeviceService } from 'src/app/services/device.service';
import { CacheComponent } from '../cache/cache.component';
@Component({
  selector: 'app-summary-cache',
  templateUrl: './summary-cache.component.html',
  styleUrls: ['./summary-cache.component.css']
})
export class SummaryCacheComponent extends SummaryEntityComponent<ICacheProfile, ICacheProfile> implements OnDestroy {
  public formId = "cacheTableColumnConfig";
  columnList = {
    id: 'ID',
    name: 'NAME',
    description: 'DESCRIPTION',
    edit: 'EDIT',
    clone: 'CLONE',
    delete: 'DELETE',
  }
  sheetComponent = CacheComponent;
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
    public entitySvc: CacheService,
    public deviceSvc: DeviceService,
    public bottomSheet: MatBottomSheet,
    public fis: FormInfoService,
  ) {
    super(entitySvc, deviceSvc, bottomSheet, fis,3);
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
            config.data = <IBottomSheet<ICacheProfile>>{ context: 'clone', from: next };
            this.bottomSheet.open(this.sheetComponent, config);
          } else {
            config.data = <IBottomSheet<ICacheProfile>>{ context: 'edit', from: next };
            this.bottomSheet.open(this.sheetComponent, config);
          }
        })
    } else {
      config.data = <IBottomSheet<ICacheProfile>>{ context: 'new', from: undefined, events: {} };
      this.bottomSheet.open(this.sheetComponent, config);
    }
  }
}
