import { Component, OnInit, Input } from '@angular/core';
import { CustomHttpInterceptor } from 'src/app/services/interceptors/http.interceptor';

@Component({
  selector: 'app-copy-field',
  templateUrl: './copy-field.component.html',
  styleUrls: ['./copy-field.component.css']
})
export class CopyFieldComponent implements OnInit {

  @Input() inputValue: string = '';
  displayEdit = 'hidden';
  editView: false;
  constructor(private _httpInterceptor: CustomHttpInterceptor) {
  }

  ngOnInit() {
  }
  showIcon() {
    this.displayEdit = 'visible'
  }
  hideIcon() {
    this.displayEdit = 'hidden'
  }
  doCopy() {
    this.updateClipboard(this.inputValue);
  }
  updateClipboard(newClip: string) {
    this.copyToClipboard(newClip)
    .then(() => {
      this._httpInterceptor.openSnackbar('COPY_SUCCESS');
    }, () => {
      this._httpInterceptor.openSnackbar('COPY_FAILED');
    });
  }
  copyToClipboard(textToCopy:string) {
    // navigator clipboard api needs a secure context (https)
    if (navigator.clipboard && window.isSecureContext) {
        // navigator clipboard api method'
        return navigator.clipboard.writeText(textToCopy);
    } else {
        let textArea = document.createElement("textarea");
        textArea.value = textToCopy;
        textArea.style.position = "fixed";
        textArea.style.left = "-999999px";
        textArea.style.top = "-999999px";
        document.body.appendChild(textArea);
        textArea.focus();
        textArea.select();
        return new Promise((res, rej) => {
            document.execCommand('copy') ? res('') : rej();
            textArea.remove();
        });
    }
}
}