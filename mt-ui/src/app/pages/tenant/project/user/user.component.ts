import { ChangeDetectorRef, Component } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { map, tap } from 'rxjs/operators';
import { Utility } from 'src/app/misc/utility';
import { INode } from 'src/app/components/dynamic-tree/dynamic-tree.component';
import { IProjectUser } from 'src/app/misc/interface';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
import { RESOURCE_NAME } from 'src/app/misc/constant';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { IRole } from '../my-roles/my-roles.component';
import { BannerService } from 'src/app/services/banner.service';
@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent {
  public projectId = this.routerWrapper.getProjectIdFromUrl()
  private userId = this.routerWrapper.getUserIdFromUrl()
  private url = Utility.getProjectResource(this.projectId, RESOURCE_NAME.USERS)
  private roleUrl = Utility.getProjectResource(this.projectId, RESOURCE_NAME.ROLES)
  public loadRoot;
  public changeId = Utility.getChangeId();
  private data: IProjectUser = undefined
  public loadChildren = (id: string) => this.httpSvc.readEntityByQuery(this.roleUrl, 0, 1000, "parentId:" + id).pipe(map(e => {
    e.data.forEach(ee => {
      (ee as INode).editable = true;
    })
    return e
  }))
  public formGroup: FormGroup = new FormGroup({
    email: new FormControl({ value: '', disabled: true }),
  })
  constructor(
    public routerWrapper: RouterWrapperService,
    public httpSvc: HttpProxyService,
    public banner: BannerService,
    public cdr: ChangeDetectorRef
  ) {
    this.loadRoot = this.httpSvc.readEntityByQuery<IRole>(this.roleUrl, 0, 1000, "parentId:null,types:PROJECT.USER").pipe(map(e => {
      e.data.forEach(ee => {
        if (ee.roleType === 'PROJECT') {
          (ee as INode).editable = false;
        } else {
          (ee as INode).editable = true;
        }
      })
      return e
    })).pipe(tap(() => this.cdr.markForCheck()));


    this.httpSvc.readEntityById<IProjectUser>(this.url, this.userId).subscribe(next => {
      this.formGroup.get('email').setValue(next.email);
      this.data = next;
      (next.roles || []).forEach(p => {
        if (!this.formGroup.get(p)) {
          this.formGroup.addControl(p, new FormControl('checked'))
        } else {
          this.formGroup.get(p).setValue('checked', { emitEvent: false })
        }
      })
    });

  }
  convertToPayload(): IProjectUser {
    const value = this.formGroup.value
    return {
      id: this.userId,//value is ignored
      roles: Object.keys(value).filter(e => value[e] === 'checked'),
      projectId: this.projectId,
      version: 0
    }
  }
  update() {
    this.httpSvc.updateEntity(this.url, this.userId, this.convertToPayload(), this.changeId).subscribe(next=>{
      this.banner.notify(next)
    })
  }
  removeRole(key: string) {
    this.formGroup.get(key).setValue('unchecked')
  }
  roles() {
    const api = this.formGroup.value
    return Object.keys(api).filter(e => api[e] === 'checked').filter(e => e)
  }
  parseId(id: string) {
    return this.data.roleDetails.find(e => e.id === id)?.name || id
  }
}
