import { MediaMatcher } from '@angular/cdk/layout';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { INavElement } from 'src/app/components/nav-bar/nav-bar.component';

@Component({
  selector: 'app-document',
  templateUrl: './document.component.html',
  styleUrls: ['./document.component.css']
})
export class DocumentComponent implements OnInit {
  mobileQuery: MediaQueryList;
  private _mobileQueryListener: () => void;
  documentList: INavElement[] = [
    {
      link: 'lunch',
      display: 'GET_START',
      icon: 'dashboard',
      params: {
      },
    },
    {
      link: 'design',
      display: 'DESIGN_DOC',
      icon: 'blur_on',
      params: {
      },
    },
  ];
  constructor(changeDetectorRef: ChangeDetectorRef, media: MediaMatcher,) {
    this.mobileQuery = media.matchMedia('(max-width: 600px)');
    this._mobileQueryListener = () => changeDetectorRef.detectChanges();
    this.mobileQuery.addListener(this._mobileQueryListener);
  }

  ngOnInit(): void {
  }
}
