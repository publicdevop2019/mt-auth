import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-resolve-confirm-dialog',
  templateUrl: './resolve-confirm-dialog.component.html',
  styleUrls: ['./resolve-confirm-dialog.component.css']
})
export class ResolveConfirmDialogComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<ResolveConfirmDialogComponent>,
    ) { }

  ngOnInit() {
  }
  cancelClick(){
    this.dialogRef.close();
  }
}
