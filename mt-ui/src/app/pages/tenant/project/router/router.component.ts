import { Component } from '@angular/core';
import { Utility } from 'src/app/misc/utility';
import { Validator } from 'src/app/misc/validator';
import { IRouter } from 'src/app/misc/interface';
import { FormGroup, FormControl } from '@angular/forms';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { RESOURCE_NAME } from 'src/app/misc/constant';
import { DeviceService } from 'src/app/services/device.service';
@Component({
  selector: 'app-router',
  templateUrl: './router.component.html',
  styleUrls: ['./router.component.css']
})
export class RouterComponent {
  context: 'NEW' | 'EDIT' = 'NEW';
  fg = new FormGroup({
    id: new FormControl({ value: '', disabled: true }),
    name: new FormControl(''),
    description: new FormControl(''),
    path: new FormControl(''),
    externalUrl: new FormControl(''),
  });
  nameErrorMsg: string = undefined;
  pathErrorMsg: string = undefined;
  externalUrlErrorMsg: string = undefined;
  changeId: string = Utility.getChangeId();
  data: IRouter = undefined;
  enableError: boolean = false;
  private projectId = this.router.getProjectIdFromUrl()
  private url = Utility.getProjectResource(this.projectId, RESOURCE_NAME.ROUTER)
  constructor(
    public httpSvc: HttpProxyService,
    public router: RouterWrapperService,
    public deviceSvc: DeviceService,
  ) {
    this.deviceSvc.updateDocTitle('TENANT_ROUTER_DOC_TITLE')
    const configId = this.router.getRouterConfigIdFromUrl();
    if (configId === 'template') {
    } else {
      this.context = 'EDIT'
      if (this.router.getData() === undefined) {
        this.router.navProjectHome()
      }
      this.data = this.router.getData() as IRouter;
      this.fg.get('id').setValue(this.data.id)
      this.fg.get('name').setValue(this.data.name)
      this.fg.get('description').setValue(this.data.description)
      this.fg.get('path').setValue(this.data.path)
      this.fg.get('externalUrl').setValue(this.data.externalUrl)
    }
    this.fg.valueChanges.subscribe(() => {
      if (this.enableError) {
        this.validateForm()
      }
    })
  }
  update() {
    this.enableError = true
    if (this.validateForm()) {
      this.httpSvc.updateEntity(this.url, this.data.id, this.convertToPayload(), this.changeId).subscribe(next => {
        this.deviceSvc.notify(next)
      })
    }
  }
  create() {
    this.enableError = true
    if (this.validateForm()) {
      this.httpSvc.createEntity(this.url, this.convertToPayload(), this.changeId).subscribe(next => {
        this.deviceSvc.notify(!!next)
      })
    }
  }
  private convertToPayload(): IRouter {
    return {
      id: this.fg.get('id').value,
      name: this.fg.get('name').value,
      description: Utility.hasValue(this.fg.get('description').value) ? this.fg.get('description').value : undefined,
      path: this.fg.get('path').value ? this.fg.get('path').value : undefined,
      externalUrl: this.fg.get('externalUrl').value ? this.fg.get('externalUrl').value : undefined,
      version: this.data && this.data.version
    }
  }
  private validateForm() {
    const fg = this.fg;
    const var0 = Validator.exist(fg.get('name').value)
    const var1 = Validator.exist(fg.get('path').value)
    const var2 = Validator.exist(fg.get('externalUrl').value)
    this.nameErrorMsg = var0.errorMsg
    this.pathErrorMsg = var1.errorMsg
    this.externalUrlErrorMsg = var2.errorMsg
    return !var0.errorMsg && !var1.errorMsg && !var2.errorMsg
  }
}
