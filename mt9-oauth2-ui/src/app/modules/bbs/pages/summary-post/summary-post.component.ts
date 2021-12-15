import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subscription } from 'rxjs';
import { IPostCard, PostService, IPostSummary } from 'src/app/services/post.service';
import { DeviceService } from 'src/app/services/device.service';

@Component({
  selector: 'app-summary-post',
  templateUrl: './summary-post.component.html',
  styleUrls: ['./summary-post.component.css']
})
export class SummaryPostComponent implements OnInit {
  displayedColumns: string[] = ['id', 'title', 'topic', 'publishedAt', 'publisherId', 'edit'];
  dataSource: MatTableDataSource<IPostCard>;
  pageNumber = 0;
  pageSizeOffset=0;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;
  private sub: Subscription;
  totoal = 0;
  constructor(public postSvc: PostService, private breakpointObserver: BreakpointObserver,public deviceSvc:DeviceService) {
    this.postSvc.getAllPosts(this.pageNumber || 0, this.deviceSvc.pageSize).subscribe(products => {
      this.totalPostHandler(products)
    });
    this.sub = this.breakpointObserver.observe([
      Breakpoints.XSmall,
      Breakpoints.Small,
      Breakpoints.Medium,
      Breakpoints.Large,
      Breakpoints.XLarge,
    ]).subscribe(next => {
      if (next.breakpoints[Breakpoints.XSmall]) {
        this.displayedColumns = ['id', 'topic', 'publishedAt', 'publisherId', 'edit'];
      }
      else if (next.breakpoints[Breakpoints.Small]) {
        this.displayedColumns = ['id', 'topic', 'publishedAt', 'publisherId', 'edit'];
      }
      else if (next.breakpoints[Breakpoints.Medium]) {
        this.displayedColumns = ['id', 'title', 'topic', 'publishedAt', 'publisherId', 'edit'];
      }
      else if (next.breakpoints[Breakpoints.Large]) {
        this.displayedColumns = ['id', 'title', 'topic', 'publishedAt', 'publisherId', 'edit'];
      }
      else if (next.breakpoints[Breakpoints.XLarge]) {
        this.displayedColumns = ['id', 'title', 'topic', 'publishedAt', 'publisherId', 'edit'];
      }
      else {
        console.warn('unknown device width match!')
      }
    });
  }
  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }
  ngOnInit() {
  }
  applyFilter(filterValue: string) {
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
  pageHandler(e: PageEvent) {
    this.pageNumber = e.pageIndex;
    this.postSvc.getAllPosts(this.pageNumber || 0, this.deviceSvc.pageSize).subscribe(products => {
      this.totalPostHandler(products)
    });
  }
  private totalPostHandler(posts: IPostSummary) {
    this.dataSource = new MatTableDataSource(posts.results);
    this.dataSource.sort = this.sort;
    this.totoal=posts.total
  }

}
