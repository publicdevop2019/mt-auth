import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet, MatBottomSheetConfig } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { of } from 'rxjs';
import { IBottomSheet } from 'src/app/clazz/summary.component';
import { TenantSummaryEntityComponent } from 'src/app/clazz/tenant-summary.component';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { MyCorsProfileService } from 'src/app/services/my-cors-profile.service';
import { DeviceService } from 'src/app/services/device.service';
import { CorsComponent } from '../cors/cors.component';
import { ActivatedRoute } from '@angular/router';
import { ProjectService } from 'src/app/services/project.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { ICorsProfile } from 'src/app/misc/interface';
import { Utility } from 'src/app/misc/utility';
@Component({
  selector: 'app-my-cors',
  templateUrl: './my-cors.component.html',
  styleUrls: ['./my-cors.component.css']
})
export class MyCorsComponent extends TenantSummaryEntityComponent<ICorsProfile, ICorsProfile> implements OnDestroy {
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
    public entitySvc: MyCorsProfileService,
    public deviceSvc: DeviceService,
    public bottomSheet: MatBottomSheet,
    public fis: FormInfoService,
    public route: ActivatedRoute,
    public projectSvc: ProjectService,
    public httpSvc: HttpProxyService,
  ) {
    super(route, projectSvc, httpSvc, entitySvc, deviceSvc, bottomSheet, fis, 3);
    const sub = this.canDo('VIEW_CORS').subscribe(b => {
      if (b.result) {
        this.doSearch({ value: '', resetPage: true })
      }
    })
    this.subs.add(sub);
    this.initTableSetting();
  }
  openBottomSheet(id?: string, clone?: boolean): void {
    let config = new MatBottomSheetConfig();
    config.autoFocus = true;
    config.panelClass = 'fix-height'
    if (Utility.hasValue(id)) {
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
