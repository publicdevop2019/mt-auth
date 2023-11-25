import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { ActivatedRoute } from '@angular/router';
import { FormInfoService } from 'mt-form-builder';
import { SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { IProjectSimple } from 'src/app/misc/interface';
import { DeviceService } from 'src/app/services/device.service';
import { ProjectService } from 'src/app/services/project.service';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
@Component({
  selector: 'app-summary-project',
  templateUrl: './summary-project.component.html',
  styleUrls: ['./summary-project.component.css']
})
export class SummaryProjectComponent extends SummaryEntityComponent<IProjectSimple, IProjectSimple> implements OnDestroy {
  public formId = "projectTableColumnConfig";
  columnList = {
    id: 'ID',
    name: 'NAME',
    createdAt: 'PROJECT_CREATED_AT',
    createdBy: 'CREATED_BY',
  }
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
    public entitySvc: ProjectService,
    public activated: ActivatedRoute,
    public router: RouterWrapperService,
    public bottomSheet: MatBottomSheet,
    public fis: FormInfoService,
  ) {
    super(entitySvc,activated, router, bottomSheet,fis, 3);
    this.initTableSetting();
    this.doSearch({ value: '', resetPage: false })
  }
  doRefresh() {
    this.doSearch({ value: '', resetPage: false })
  }
}
