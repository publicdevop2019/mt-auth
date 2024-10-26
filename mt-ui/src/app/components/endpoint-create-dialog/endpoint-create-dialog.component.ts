import { Component } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { Utility } from 'src/app/misc/utility';
import { Validator } from 'src/app/misc/validator';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { ClientCreateDialogComponent } from '../client-create-dialog/client-create-dialog.component';

@Component({
  selector: 'app-api-create-dialog',
  templateUrl: './endpoint-create-dialog.component.html',
  styleUrls: ['./endpoint-create-dialog.component.css']
})
export class EndpointCreateDialogComponent {
  allowError: boolean = false;
  nameErrorMsg: string = undefined;
  typeErrorMsg: string = undefined;
  fg = new FormGroup({
    name: new FormControl('MyAPI_' + Utility.getRandomString().substring(0,3), []),
    type: new FormControl('PROTECTED_NONE_SHARED_API', []),
  });
  constructor(
    public dialogRef: MatDialogRef<ClientCreateDialogComponent>,
    public httpProxySvc: HttpProxyService,
  ) {
    this.fg.valueChanges.subscribe(() => {
      if (this.allowError) {
        this.validateCreateDialogForm()
      }
    })
  }
  dismiss(event: MouseEvent) {
    this.dialogRef.close();
    event.preventDefault();
  }
  private validateCreateDialogForm() {
    const fg = this.fg;

    const var0 = Validator.exist(fg.get('name').value)
    this.nameErrorMsg = var0.errorMsg;

    const var1 = Validator.exist(fg.get('type').value)
    this.typeErrorMsg = var1.errorMsg;

    return !var0.errorMsg && !var1.errorMsg
  }
  create() {
    this.allowError = true;
    const result = this.validateCreateDialogForm()
    if (result) {
      this.dialogRef.close({
        name: this.fg.get('name').value,
        type: this.fg.get('type').value,
      })
    }
  }
}
