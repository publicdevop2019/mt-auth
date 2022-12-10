import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
export interface DialogData {
  reason: string;
}
@Component({
  selector: 'app-enter-reason-dialog',
  templateUrl: './enter-reason-dialog.component.html',
  styleUrls: ['./enter-reason-dialog.component.css']
})
export class EnterReasonDialogComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<EnterReasonDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData,) { }

  ngOnInit(): void {
  }
  onNoClick(): void {
    this.dialogRef.close(false);
  }
}
