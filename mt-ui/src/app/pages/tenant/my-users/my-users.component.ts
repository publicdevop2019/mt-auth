import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { FormInfoService } from 'mt-form-builder';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
import { take } from 'rxjs/operators';
import { SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { IProjectUser } from 'src/app/clazz/validation/aggregate/user/interfaze-user';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { DeviceService } from 'src/app/services/device.service';
import { MyUserService } from 'src/app/services/my-user.service';
import { ProjectService } from 'src/app/services/project.service';
import { UserComponent } from '../user/user.component';
@Component({
  selector: 'app-my-users',
  templateUrl: './my-users.component.html',
  styleUrls: ['./my-users.component.css']
})
export class MyUsersComponent extends SummaryEntityComponent<IProjectUser, IProjectUser> implements OnDestroy {
  public formId = "myUserTableColumnConfig";
  public projectId: string;
  columnList = {
    id: 'ID',
    email: 'EMAIL',
    edit: 'EDIT',
  }
  sheetComponent = UserComponent;
  public roleList: IOption[] = [];
  searchConfigs: ISearchConfig[] = [
    {
      searchLabel: 'ID',
      searchValue: 'id',
      type: 'text',
      multiple: {
        delimiter: '.'
      }
    },
    {
      searchLabel: 'EMAIL',
      searchValue: 'email',
      type: 'text',
      multiple: {
        delimiter: '.'
      }
    },
  ]
  constructor(
    public entitySvc: MyUserService,
    public deviceSvc: DeviceService,
    public fis: FormInfoService,
    public bottomSheet: MatBottomSheet,
    public dialog: MatDialog,
    public projectSvc: ProjectService,
    private route: ActivatedRoute,
  ) {
    super(entitySvc, deviceSvc, bottomSheet, fis, 2);
    const sub=this.route.paramMap.subscribe(queryMaps => {
      this.projectId = queryMaps.get('id')
      this.entitySvc.setProjectId(this.projectId)
      this.bottomSheetParams['projectId']=this.projectId
      this.deviceSvc.refreshSummary.next()
    });
    this.subs.add(sub)
  }
}
