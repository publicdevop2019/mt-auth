import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { IProjectSimple } from 'src/app/clazz/validation/aggregate/project/interface-project';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { DeviceService } from 'src/app/services/device.service';
import { ProjectService } from 'src/app/services/project.service';
import { CorsComponent } from '../cors/cors.component';
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
    public entitySvc: ProjectService,
    public deviceSvc: DeviceService,
    public bottomSheet: MatBottomSheet,
    public fis: FormInfoService,
  ) {
    super(entitySvc, deviceSvc, bottomSheet,fis, 3);
  }
}
