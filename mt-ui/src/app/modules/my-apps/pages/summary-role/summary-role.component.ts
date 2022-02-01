import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet, MatBottomSheetConfig } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
import { of } from 'rxjs';
import { IBottomSheet, SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { IRole } from 'src/app/clazz/validation/aggregate/role/interface-role';
import { hasValue } from 'src/app/clazz/validation/validator-common';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { DeviceService } from 'src/app/services/device.service';
import { RoleService } from 'src/app/services/role.service';
import { RoleComponent } from '../../../../pages/role/role.component';
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
    public entitySvc: RoleService,
    public deviceSvc: DeviceService,
    public fis: FormInfoService,
    public bottomSheet: MatBottomSheet,
  ) {
    super(entitySvc, deviceSvc, bottomSheet,fis, 2);
  }
  getOption(value: string, options: IOption[]) {
    return options.find(e => e.value == value)
  }
  openBottomSheet(id?: string, clone?: boolean): void {
    let config = new MatBottomSheetConfig();
    config.autoFocus = true;
    config.panelClass = 'fix-height'
    if (hasValue(id)) {
      of(this.dataSource.data.find(e=>e.id===id))
          .subscribe(next => {
        if (clone) {
          config.data = <IBottomSheet<IRole>>{ context: 'clone', from: next };
          this.bottomSheet.open(this.sheetComponent, config);
        } else {
          config.data = <IBottomSheet<IRole>>{ context: 'edit', from: next };
          this.bottomSheet.open(this.sheetComponent, config);
        }
      })
    } else {
      config.data = <IBottomSheet<IRole>>{ context: 'new', from: undefined, events: {} };
      this.bottomSheet.open(this.sheetComponent, config);
    }
  }
}
