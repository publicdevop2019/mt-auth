import { Component, EventEmitter, OnDestroy, Output } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { interval, Subscription } from 'rxjs';
import { debounce, filter } from 'rxjs/operators';
import { ISearchEvent } from '../search/search.component';
interface IUserSearchConfig {
  searchValue: string,
  searchLabel: string,
}
@Component({
  selector: 'app-user-search',
  templateUrl: './user-search.component.html',
  styleUrls: ['./user-search.component.css']
})
export class SearchNewComponent implements OnDestroy {
  public searchConfigs: IUserSearchConfig[] = [
    {
      searchLabel: 'EMAIL',
      searchValue: 'email',
    },
    {
      searchLabel: 'MOBILE_NUMBER',
      searchValue: 'mobile',
    },
    {
      searchLabel: 'USERNAME',
      searchValue: 'username',
    },
  ]
  @Output() search: EventEmitter<ISearchEvent> = new EventEmitter()

  public fg: FormGroup;
  private searchKey = new FormControl({ value: 'email', disabled: false });
  private searchValue = new FormControl({ value: null, disabled: false });
  private subs: Subscription = new Subscription();
  constructor(
    public translateSvc: TranslateService,
  ) {
    this.fg = new FormGroup({
      searchKey: this.searchKey,
      searchValue: this.searchValue,
    });
    const sub0 = this.searchValue.valueChanges.pipe(filter(e => e !== null && e !== undefined && e !== '' && JSON.stringify(e) !== JSON.stringify([]))).pipe(debounce(() => interval(1000)))
      .subscribe(next => {
        this.search.emit({ value: this.searchKey.value + ":" + next, resetPage: true });
      });
    const sub1 = this.searchKey.valueChanges.pipe(debounce(() => interval(1000)))
      .subscribe(next => {
        if (this.searchValue.value) {
          this.search.emit({ value: next + ":" + this.searchValue.value, resetPage: true });
        }
      });
    this.subs.add(sub0)
    this.subs.add(sub1)
  }
  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }
  reset() {
    this.fg.reset({
      searchValue: null,
      searchKey: 'email',
    }, { emitEvent: false });
    this.search.emit({ value: '', resetPage: true });
  }
}
