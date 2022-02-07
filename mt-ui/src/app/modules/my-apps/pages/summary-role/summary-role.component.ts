import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet, MatBottomSheetConfig } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
import { of } from 'rxjs';
import { IBottomSheet, SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { hasValue } from 'src/app/clazz/validation/validator-common';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { INewRole } from 'src/app/pages/tenant/my-roles/my-roles.component';
import { DeviceService } from 'src/app/services/device.service';
import { NewRoleService } from 'src/app/services/new-role.service';
import { RoleComponent } from '../../../../pages/tenant/role/role.component';
@Component({
  selector: 'app-summary-role',
  templateUrl: './summary-role.component.html',
  styleUrls: ['./summary-role.component.css']
})
export class SummaryRoleComponent extends SummaryEntityComponent<INewRole, INewRole> implements OnDestroy {
  public formId = "roleTableColumnConfig";
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
        delimiter:'.'
      }
    },
  ]
  constructor(
    public entitySvc: NewRoleService,
    public deviceSvc: DeviceService,
    public fis: FormInfoService,
    public bottomSheet: MatBottomSheet,
  ) {
    super(entitySvc, deviceSvc, bottomSheet,fis, 2);
  }
  getOption(value: string, options: IOption[]) {
    return options.find(e => e.value == value)
  }
}
