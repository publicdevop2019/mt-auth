import { Component, OnInit, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';

@Component({
  selector: 'app-progress-spinner',
  templateUrl: './progress-spinner.component.html',
  styleUrls: ['./progress-spinner.component.css']
})
export class ProgressSpinnerComponent implements AfterViewChecked {
  ngAfterViewChecked(): void {
    this.spinnerRef.nativeElement.focus();
  }
  public spinnerRef: ElementRef;
  @ViewChild('spinner', { static: true }) set spinner(view: ElementRef) {
    if (view) {
      this.spinnerRef = view;
    }

  }
  constructor() { }
  trap() {
    this.spinnerRef.nativeElement.focus()
  }
}
