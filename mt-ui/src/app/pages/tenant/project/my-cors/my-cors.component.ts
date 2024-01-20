import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { TenantSummaryEntityComponent } from 'src/app/clazz/tenant-summary.component';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { MyCorsProfileService } from 'src/app/services/my-cors-profile.service';
import { DeviceService } from 'src/app/services/device.service';
import { CorsComponent } from '../cors/cors.component';
import { ActivatedRoute } from '@angular/router';
import { ProjectService } from 'src/app/services/project.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { ICorsProfile } from 'src/app/misc/interface';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
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
    public router: ActivatedRoute,
    public route: RouterWrapperService,
    public projectSvc: ProjectService,
    public httpSvc: HttpProxyService,
  ) {
    super(router, route, projectSvc, httpSvc, entitySvc, bottomSheet, fis);
    const sub = this.canDo('VIEW_CORS').subscribe(b => {
      if (b.result) {
        this.doSearch({ value: '', resetPage: true })
      }
    })
    const sub2 = this.deviceSvc.refreshSummary.subscribe(() => {
      this.doRefresh()
    })
    this.subs.add(sub);
    this.subs.add(sub2);
    this.initTableSetting();
  }
  removeFirst(input: string[]) {
    return input.filter((e, i) => i !== 0);
  }
  doRefresh() {
    this.doSearch({ value: '', resetPage: false })
  }
  edit(id: string) {
    const data = this.dataSource.data.find(e => e.id === id)
    this.route.navProjectCorsConfigsDetail(id, data)
  }
  create() {
    this.route.navProjectNewCorsConfigsDetail()
  }
}
