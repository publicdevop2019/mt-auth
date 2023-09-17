import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet, MatBottomSheetConfig } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
import { IDomainContext } from 'src/app/clazz/summary.component';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { MyCacheService } from 'src/app/services/my-cache.service';
import { DeviceService } from 'src/app/services/device.service';
import { TenantSummaryEntityComponent } from 'src/app/clazz/tenant-summary.component';
import { ActivatedRoute } from '@angular/router';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { ProjectService } from 'src/app/services/project.service';
import { of } from 'rxjs';
import { CacheComponent } from '../cache/cache.component';
import { ICacheProfile, ICorsProfile } from 'src/app/misc/interface';
import { Utility } from 'src/app/misc/utility';
@Component({
  selector: 'app-my-cache',
  templateUrl: './my-cache.component.html',
  styleUrls: ['./my-cache.component.css']
})
export class MyCacheComponent extends TenantSummaryEntityComponent<ICacheProfile, ICacheProfile> implements OnDestroy {
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
    public entitySvc: MyCacheService,
    public deviceSvc: DeviceService,
    public bottomSheet: MatBottomSheet,
    public fis: FormInfoService,
    public route: ActivatedRoute,
    public projectSvc: ProjectService,
    public httpSvc: HttpProxyService,
  ) {
    super(route, projectSvc, httpSvc, entitySvc, deviceSvc, bottomSheet, fis, 3);
    const sub = this.canDo('VIEW_CACHE').subscribe(b => {
      if (b.result) {
        this.doSearch({ value: '', resetPage: true })
      }
    })
    this.initTableSetting();
    const sub2=this.deviceSvc.refreshSummary.subscribe(() => {
      this.doRefresh()
    })
    this.subs.add(sub);
    this.subs.add(sub2);
  }
  getOption(value: string, options: IOption[]) {
    return options.find(e => e.value == value)
  }
  doRefresh() {
    this.doSearch({ value: '', resetPage: false })
  }
  getData(id: string) {
    return this.dataSource.data.find(e => e.id === id)
  }
  openBottomSheet(id?: string, clone?: boolean): void {
    let config = new MatBottomSheetConfig();
    config.autoFocus = true;
    config.panelClass = 'fix-height'
    if (Utility.hasValue(id)) {
      of(this.dataSource.data.find(e => e.id === id))
        .subscribe(next => {
          if (clone) {
            config.data = <IDomainContext<ICacheProfile>>{ context: 'clone', from: next };
            this.bottomSheet.open(this.sheetComponent, config);
          } else {
            config.data = <IDomainContext<ICacheProfile>>{ context: 'edit', from: next };
            this.bottomSheet.open(this.sheetComponent, config);
          }
        })
    } else {
      config.data = <IDomainContext<ICorsProfile>>{ context: 'new', from: undefined, params: {} };
      this.bottomSheet.open(this.sheetComponent, config);
    }
  }
}
