import { Component } from '@angular/core';
import { Utility } from 'src/app/misc/utility';
import { Validator } from 'src/app/misc/validator';
import { ICorsProfile } from 'src/app/misc/interface';
import { MyCorsProfileService } from 'src/app/services/my-cors-profile.service';
import { FormGroup, FormControl } from '@angular/forms';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
@Component({
  selector: 'app-cors',
  templateUrl: './cors.component.html',
  styleUrls: ['./cors.component.css']
})
export class CorsComponent {
  context: 'NEW' | 'EDIT' = 'NEW';
  fg = new FormGroup({
    id: new FormControl({ value: '', disabled: true }),
    name: new FormControl(''),
    description: new FormControl(''),
    maxAge: new FormControl(''),
    allowCredentials: new FormControl(''),
    allowOrigin: new FormControl(''),
    allowedHeaders: new FormControl(''),
    exposedHeaders: new FormControl(''),
  });
  nameErrorMsg: string = undefined;
  allowError: boolean = false;
  changeId: string = Utility.getChangeId();
  data: ICorsProfile = undefined;
  constructor(
    public entityService: MyCorsProfileService,
    public router: RouterWrapperService,
  ) {
    entityService.setProjectId(this.router.getProjectIdFromUrl())
    const configId = this.router.getCorsConfigIdFromUrl();
    if (configId === 'template') {
    } else {
      this.context = 'EDIT'
      if (this.router.getData() === undefined) {
        this.router.navProjectHome()
      }
      this.data = this.router.getData() as ICorsProfile;
      this.fg.get('id').setValue(this.data.id)
      this.fg.get('name').setValue(this.data.name)
      this.fg.get('description').setValue(this.data.description || '')
      this.fg.get('allowCredentials').setValue(this.data.allowCredentials)
      this.fg.get('allowedHeaders').setValue(this.data.allowedHeaders.join(','))
      this.fg.get('allowOrigin').setValue(this.data.allowOrigin.join(','))
      this.fg.get('exposedHeaders').setValue(this.data.exposedHeaders.join(','))
      this.fg.get('maxAge').setValue(this.data.maxAge)
    }
    this.fg.valueChanges.subscribe(() => {
      if (this.allowError) {
        this.validateForm()
      }
    })
  }
  update() {
    if (this.validateForm()) {
      this.entityService.update(this.data.id, this.convertToPayload(), this.changeId)
    }
  }
  create() {
    if (this.validateForm()) {
      this.entityService.create(this.convertToPayload(), this.changeId)
    }
  }
  private convertToPayload(): ICorsProfile {
    return {
      id: this.fg.get('id').value,
      name: this.fg.get('name').value,
      description: Utility.hasValue(this.fg.get('description').value) ? this.fg.get('description').value : undefined,
      allowCredentials: !!this.fg.get('allowCredentials').value,
      allowOrigin: (this.fg.get('allowOrigin').value as string).split(',').filter(e => e),
      allowedHeaders: (this.fg.get('allowedHeaders').value as string).split(',').filter(e => e),
      exposedHeaders: (this.fg.get('exposedHeaders').value as string).split(',').filter(e => e),
      maxAge: +this.fg.get('maxAge').value,
      version: this.data && this.data.version
    }
  }
  private validateForm() {
    const fg = this.fg;
    const var0 = Validator.exist(fg.get('name').value)
    this.nameErrorMsg = var0.errorMsg
    return !var0.errorMsg
  }
}
