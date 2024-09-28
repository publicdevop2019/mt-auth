import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { Component, ElementRef, ViewChild } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatMenuTrigger } from '@angular/material/menu';
import { combineLatest } from 'rxjs';
import { debounceTime } from 'rxjs/operators';
import { TableHelper } from 'src/app/clazz/table-helper';
import { RESOURCE_NAME } from 'src/app/misc/constant';
import { IOption, IProjectAdmin, IProjectUser } from 'src/app/misc/interface';
import { Utility } from 'src/app/misc/utility';
import { DeviceService } from 'src/app/services/device.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { RouterWrapperService } from 'src/app/services/router-wrapper';

@Component({
  selector: 'app-my-admin',
  templateUrl: './my-admin.component.html',
  styleUrls: ['./my-admin.component.css']
})
export class MyAdminComponent {
  private projectId = this.route.getProjectIdFromUrl()
  private adminUrl = Utility.getProjectResource(this.projectId, RESOURCE_NAME.ADMINS)
  private tenantUserUrl = Utility.getProjectResource(this.projectId, RESOURCE_NAME.USERS)
  searchValue = new FormControl('', []);
  searchKey = new FormControl('', []);
  columnList: any = {
    id: 'ID',
    name: 'NAME',
    email: 'EMAIL',
    delete: 'DELETE',
  };
  searchConfigs: { searchLabel: string, searchValue: string }[] = [
    {
      searchLabel: 'EMAIL',
      searchValue: 'email',
    },
    {
      searchLabel: 'MOBILE_NUMBER',
      searchValue: 'mobile',
    },
    {
      searchLabel: 'USERNAME',
      searchValue: 'username',
    },
  ]
  private searchPageNumber = 0;
  private searchPageSize = 10;
  loading: boolean = false;
  separatorKeysCodes: number[] = [ENTER, COMMA];
  allLoaded: boolean = false;
  options: IOption[] = [];
  newAdmins: IOption[] = [];
  @ViewChild(MatMenuTrigger) trigger: MatMenuTrigger;
  public tableSource: TableHelper<IProjectAdmin> = new TableHelper(this.columnList, 10, this.httpSvc, this.adminUrl);
  constructor(
    public httpSvc: HttpProxyService,
    public route: RouterWrapperService,
    public deviceSvc: DeviceService,
  ) {
    this.deviceSvc.updateDocTitle('TENANT_ADMIN_DOC_TITLE')
    this.tableSource.loadPage(0)
    this.searchValue.valueChanges.pipe(debounceTime(1000)).subscribe((next) => {
      this.options = []
      this.searchPageNumber = 0;
      this.allLoaded = false;
      this.searchAdminCandidate(next)
    });
  }
  private searchAdminCandidate(searchValue: string) {
    this.loading = true;
    this.httpSvc.readEntityByQuery<IProjectUser>(this.tenantUserUrl, this.searchPageNumber, this.searchPageSize, searchValue ? (this.searchKey.value + ':' + searchValue) : undefined, undefined, undefined, { 'loading': false }).subscribe((result) => {
      this.loading = false;
      const temp = result.data.map(e => {
        return <IOption>{
          label: e.displayName,
          value: e.id
        }
      });
      this.options = this.options.concat(temp)
      if (result.totalItemCount === this.options.length) {
        this.allLoaded = true;
      }
      if (!this.allLoaded) {
        this.searchPageNumber++;
      }
    })
  }
  doRefresh() {
    this.tableSource.refresh()
  }
  updateSearchValue(event: InputEvent) {
    this.searchValue.setValue((event.target as any).value)
  }
  ref: ElementRef;
  @ViewChild('ghostRef') set ghostRef(ghostRef: ElementRef) {
    if (ghostRef) { // initially setter gets called with undefined
      this.ref = ghostRef;
      this.observer.observe(this.ref.nativeElement);
    }
  }
  private _visibilityConfig = {
    threshold: 0
  };
  private observer = new IntersectionObserver((entries, self) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        this.searchAdminCandidate(this.searchValue.value)
      }
    });
  }, this._visibilityConfig);

  onAutoCompleteClickHandler(selected: IOption): void {
    if (this.newAdmins.length > 0)
      return;
    this.newAdmins.push(selected);
  }
  remove(item: IOption): void {
    this.newAdmins = this.newAdmins.filter(e => e.value !== item.value)
  }
  doAdd() {
    this.httpSvc.addAdmin(this.projectId, this.newAdmins[0].value as string, Utility.getChangeId()).subscribe(() => {
      this.doRefresh()
    })
  }
  public delete(id: string) {
    this.httpSvc.deleteEntityById(this.adminUrl, id, Utility.getChangeId()).subscribe(() => {
      this.deviceSvc.notify(true)
      this.tableSource.refresh()
    }, () => {
      this.deviceSvc.notify(false)
    })
  }
}