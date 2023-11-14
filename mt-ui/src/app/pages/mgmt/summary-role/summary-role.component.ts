import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
import { SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { DeviceService } from 'src/app/services/device.service';
import { MyRoleService } from 'src/app/services/my-role.service';
import { RoleComponent } from '../../tenant/project/role/role.component';
import { IRole } from '../../tenant/project/my-roles/my-roles.component';
@Component({
  selector: 'app-summary-role',
  templateUrl: './summary-role.component.html',
  styleUrls: ['./summary-role.component.css']
})
export class SummaryRoleComponent extends SummaryEntityComponent<IRole, IRole> implements OnDestroy {
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
    public entitySvc: MyRoleService,
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
