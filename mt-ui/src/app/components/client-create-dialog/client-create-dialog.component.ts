import { Component, Inject, OnDestroy } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormInfoService } from 'mt-form-builder';
import { Validator } from 'src/app/misc/validator';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { MyClientService } from 'src/app/services/my-client.service';
import { DialogData } from '../batch-update-cors/batch-update-cors.component';
import { ProjectService } from 'src/app/services/project.service';
import { FORM_CONFIG } from 'src/app/form-configs/client-create-dialog.config';

@Component({
  selector: 'app-client-create-dialog',
  templateUrl: './client-create-dialog.component.html',
  styleUrls: ['./client-create-dialog.component.css']
})
export class ClientCreateDialogComponent implements OnDestroy {
  formId: string;
  allowError: boolean = false;
  constructor(
    public dialogRef: MatDialogRef<ClientCreateDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData,
    public clientSvc: MyClientService,
    public httpProxySvc: HttpProxyService,
    public fis: FormInfoService,
    private psv: ProjectService,
  ) {
    this.fis.init(FORM_CONFIG, this.formId)
    this.fis.formGroups[this.formId].get('projectId').setValue(this.psv.viewProject.id)
    this.fis.formGroups[this.formId].valueChanges.subscribe(() => {
      if (this.allowError) {
        this.validateCreateDialogForm()
      }
    })
  }
  ngOnDestroy(): void {
    this.fis.reset(this.formId);
  }
  dismiss(event: MouseEvent) {
    this.dialogRef.close();
    event.preventDefault();
  }
  private validateCreateDialogForm() {
    const fg = this.fis.formGroups[this.formId];

    const var0 = Validator.exist(fg.get('name').value)
    this.fis.updateError(this.formId, 'name', var0.errorMsg)

    const var1 = Validator.exist(fg.get('frontOrBackApp').value)
    this.fis.updateError(this.formId, 'frontOrBackApp', var1.errorMsg)


    return !var0.errorMsg && !var1.errorMsg
  }
  create() {
    this.allowError = true;
    const result = this.validateCreateDialogForm()
    if (result) {
      this.dialogRef.close({
        projectId: this.fis.formGroups[this.formId].get('projectId').value,
        name: this.fis.formGroups[this.formId].get('name').value,
        type: this.fis.formGroups[this.formId].get('frontOrBackApp').value,
      })
    }
  }
}
