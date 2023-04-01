import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { ChangeDetectorRef, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { MatMenuTrigger } from '@angular/material/menu';
import { ActivatedRoute } from '@angular/router';
import { FormInfoService } from 'mt-form-builder';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
import { debounce, debounceTime, take } from 'rxjs/operators';
import { TenantSummaryEntityComponent } from 'src/app/clazz/tenant-summary.component';
import { IProjectAdmin, IProjectUser } from 'src/app/clazz/validation/aggregate/user/interfaze-user';
import { DeviceService } from 'src/app/services/device.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { MyAdminService } from 'src/app/services/my-admin.service';
import { MyUserService } from 'src/app/services/my-user.service';
import { ProjectService } from 'src/app/services/project.service';

@Component({
  selector: 'app-my-admin',
  templateUrl: './my-admin.component.html',
  styleUrls: ['./my-admin.component.css']
})
export class MyAdminComponent extends TenantSummaryEntityComponent<IProjectAdmin, IProjectAdmin> implements OnDestroy, OnInit {
  public formId = "myAdminTableColumnConfig";
  email = new FormControl('', []);
  columnList: any = {
    id: 'ID',
    name: 'NAME',
    email: 'EMAIL',
    delete: 'DELETE',
  };
  private searchPageNumber = 0;
  private searchPageSize = 10;
  loading: boolean = false;
  separatorKeysCodes: number[] = [ENTER, COMMA];
  allLoaded: boolean = false;
  options: IOption[] = [];
  newAdmins: IOption[] = [];
  @ViewChild(MatMenuTrigger) trigger: MatMenuTrigger;
  constructor(
    public entitySvc: MyAdminService,
    public userSvc: MyUserService,
    public projectSvc: ProjectService,
    public fis: FormInfoService,
    public httpSvc: HttpProxyService,
    public deviceSvc: DeviceService,
    public bottomSheet: MatBottomSheet,
    public route: ActivatedRoute,
    public cdRef: ChangeDetectorRef,
  ) {
    super(route, projectSvc, httpSvc, entitySvc, deviceSvc, bottomSheet, fis, 0);
    const sub = this.projectId.subscribe(id => {
      this.userSvc.setProjectId(id)
      this.doRefresh();
    });
    this.subs.add(sub);

    this.email.valueChanges.pipe(debounceTime(1000)).subscribe((next) => {
      this.options = []
      this.searchPageNumber = 0;
      this.allLoaded = false;
      this.searchAdmin(next)
    });
    this.deviceSvc.refreshSummary.subscribe(() => {
      this.doRefresh();
    })
  }
  private searchAdmin(email: string) {
    this.loading = true;
    this.userSvc.readEntityByQuery(this.searchPageNumber, this.searchPageSize, email ? ('emailLike:' + email) : undefined, undefined, undefined, { 'loading': false }).subscribe((result) => {
      this.loading = false;
      const temp = result.data.map(e => {
        return <IOption>{
          label: e.email,
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
      console.dir(this.allLoaded)
    })
  }
  ngOnInit(): void {
    this.doRefresh()
  }
  doRefresh() {
    const search = {
      value: '',
      resetPage: false
    }
    this.doSearch(search);
  }
  updateEmail(event: InputEvent) {
    this.email.setValue((event.target as any).value)
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
        this.searchAdmin(this.email.value)
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
    this.userSvc.addAdmin(this.newAdmins[0].value as string).subscribe(() => {
      this.doRefresh()
    })
  }
}