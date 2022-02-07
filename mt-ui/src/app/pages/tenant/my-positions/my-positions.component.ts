import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatBottomSheet, MatBottomSheetConfig } from '@angular/material/bottom-sheet';
import { ActivatedRoute } from '@angular/router';
import { FormInfoService } from 'mt-form-builder';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
import { Observable, combineLatest, of } from 'rxjs';
import { take } from 'rxjs/operators';
import { IBottomSheet, IIdBasedEntity, SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { IPermission } from 'src/app/clazz/validation/aggregate/permission/interface-permission';
import { hasValue } from 'src/app/clazz/validation/validator-common';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { RoleComponent } from 'src/app/pages/tenant/role/role.component';
import { DeviceService } from 'src/app/services/device.service';
import { NewRoleService } from 'src/app/services/new-role.service';
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
    public entitySvc: NewRoleService,
    public projectSvc: ProjectService,
    public deviceSvc: DeviceService,
    public fis: FormInfoService,
    public bottomSheet: MatBottomSheet,
    private route: ActivatedRoute,
  ) {
    super(entitySvc, deviceSvc, bottomSheet, fis, 2);
    this.route.paramMap.pipe(take(1)).subscribe(queryMaps => {
      this.projectId = queryMaps.get('id')
    });
  }
  getOption(value: string, options: IOption[]) {
    return options.find(e => e.value == value)
  }
}