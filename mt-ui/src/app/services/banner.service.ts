import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
    providedIn: 'root'
})
export class BannerService {
    constructor(private translate: TranslateService, private _snackBar: MatSnackBar) { }
    openSnackbar(message: string) {
        this.translate.get(message).subscribe(next => {
            this._snackBar.open(next, 'OK', {
                duration: 5000,
            });
        })
    }
    notify(result: boolean) {
        result ? this.openSnackbar('OPERATION_SUCCESS') : this.openSnackbar('OPERATION_FAILED');
    }
}
