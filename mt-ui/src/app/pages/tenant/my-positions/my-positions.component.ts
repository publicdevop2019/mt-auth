import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { ActivatedRoute } from '@angular/router';
import { FormInfoService } from 'mt-form-builder';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
import { take } from 'rxjs/operators';
import { IIdBasedEntity, SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { RoleComponent } from 'src/app/pages/tenant/role/role.component';
import { DeviceService } from 'src/app/services/device.service';
import { MyRoleService } from 'src/app/services/my-role.service';
import { ProjectService } from 'src/app/services/project.service';
export interface IPosition extends IIdBasedEntity{

}
@Component({
  selector: 'app-my-positions',
  templateUrl: './my-positions.component.html',
  styleUrls: ['./my-positions.component.css']
})
export class MyPositionsComponent extends SummaryEntityComponent<IPosition, IPosition> implements OnDestroy {
  public formId = "positionTableColumnConfig";
  public projectId: string;
  columnList = {
    id: 'ID',
    name: 'NAME',
    description: 'DESCRIPTION',
    type: 'TYPE',
    edit: 'EDIT',
    clone: 'CLONE',
    delete: 'DELETE',
  }
  sheetComponent = RoleComponent;
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
    public entitySvc: MyRoleService,
    public projectSvc: ProjectService,
    public deviceSvc: DeviceService,
    public fis: FormInfoService,
    public bottomSheet: MatBottomSheet,
    private route: ActivatedRoute,
  ) {
    super(entitySvc, deviceSvc, bottomSheet, fis, 2);
    const sub=this.route.paramMap.subscribe(queryMaps => {
      this.projectId = queryMaps.get('id')
      this.deviceSvc.refreshSummary.next()
    });
    this.subs.add(sub)
  }
  getOption(value: string, options: IOption[]) {
    return options.find(e => e.value == value)
  }
}