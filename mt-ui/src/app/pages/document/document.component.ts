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
      link: 'deploy',
      display: 'DEPLOYMENT',
      icon: 'directions_run',
      params: {
      },
    },
    {
      link: 'build',
      display: 'BUILD',
      icon: 'build',
      params: {
      },
    },
    {
      link: 'design',
      display: 'DESIGN_DOC',
      icon: 'description',
      params: {
      },
    },
    {
      link: 'error',
      display: 'ERROR_DOC',
      icon: 'bug_report',
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
