import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatBottomSheet, MatBottomSheetConfig } from '@angular/material/bottom-sheet';
import { ActivatedRoute } from '@angular/router';
import { FormInfoService } from 'mt-form-builder';
import { IForm, IOption } from 'mt-form-builder/lib/classes/template.interface';
import { Observable, combineLatest, of } from 'rxjs';
import { take } from 'rxjs/operators';
import { SummaryEntityComponent, IBottomSheet, IIdBasedEntity } from 'src/app/clazz/summary.component';
import { IPermission } from 'src/app/clazz/validation/aggregate/permission/interface-permission';
import { hasValue } from 'src/app/clazz/validation/validator-common';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { FORM_CONFIG } from 'src/app/form-configs/view.config';
import { RoleComponent } from 'src/app/pages/tenant/role/role.component';
import { DeviceService } from 'src/app/services/device.service';
import { NewRoleService } from 'src/app/services/new-role.service';
import { OrgService } from 'src/app/services/org.service';
import { ProjectService } from 'src/app/services/project.service';
export interface IOrg extends IIdBasedEntity{
  parentId?:string
}
@Component({
  selector: 'app-my-orgs',
  templateUrl: './my-orgs.component.html',
  styleUrls: ['./my-orgs.component.css']
})
export class MyOrgsComponent extends SummaryEntityComponent<IOrg, IOrg> implements OnDestroy {
  public formId = "orgTableColumnConfig";
  formId2 = 'summaryOrgCustomerView';
  formInfo: IForm = JSON.parse(JSON.stringify(FORM_CONFIG));
  viewType: "TREE_VIEW" | "LIST_VIEW" | "DYNAMIC_TREE_VIEW" = "LIST_VIEW";
  
  public projectId: string;
  private formCreatedOb2: Observable<string>;
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
  public loadRoot = this.entitySvc.readByQuery(0, 1000, "parentId:null")
  public loadChildren = (id: string) => this.entitySvc.readByQuery(0, 1000, "parentId:" + id)
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
    public entitySvc: OrgService,
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
    this.formCreatedOb2 = this.fis.formCreated(this.formId2);
    
    combineLatest([this.formCreatedOb2]).pipe(take(1)).subscribe(()=>{
      const sub = this.fis.formGroupCollection[this.formId2].valueChanges.subscribe(e => {
        this.viewType = e.view;
        if (this.viewType === 'TREE_VIEW') {
          this.entitySvc.readEntityByQuery(0, 1000).subscribe(next => {
            super.updateSummaryData(next)
          });
        } 
      });
      if(!this.fis.formGroupCollection[this.formId2].get('view').value){
        this.fis.formGroupCollection[this.formId2].get('view').setValue(this.viewType);
      }
      this.subs.add(sub)
    })
  }
  getOption(value: string, options: IOption[]) {
    return options.find(e => e.value == value)
  }
  openBottomSheet(id?: string, clone?: boolean): void {
    let config = new MatBottomSheetConfig();
    config.autoFocus = true;
    config.panelClass = 'fix-height'
    if (hasValue(id)) {
      of(this.dataSource.data.find(e => e.id === id))
        .subscribe(next => {
          if (clone) {
            config.data = <IBottomSheet<IPermission>>{ context: 'clone', from: next };
            this.bottomSheet.open(this.sheetComponent, config);
          } else {
            config.data = <IBottomSheet<IPermission>>{ context: 'edit', from: next };
            this.bottomSheet.open(this.sheetComponent, config);
          }
        })
    } else {
      config.data = <IBottomSheet<IPermission>>{ context: 'new', from: { projectId: this.projectId, name: '', id: '', version: 0 }};
      this.bottomSheet.open(this.sheetComponent, config);
    }
  }
}