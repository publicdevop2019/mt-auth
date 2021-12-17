import { Injectable } from '@angular/core';
import { HttpProxyService } from './http-proxy.service';
import { MatDialog } from '@angular/material/dialog';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
export interface IPostSummary {
  results: IPostCard[]
  total: number
}
export interface IPostCard {
  id: number,
  title: string,
  topic: string,
  publishedAt: string,
  publisherId: string,
  views: number,
  comments: number
}
@Injectable({
  providedIn: 'root'
})
export class PostService {
  getAllPosts(pageNum: number, pageSize: number) {
    return this.httpProxy.getAllPosts(pageNum, pageSize)
  }
  deletePost(id: string) {
    return this.httpProxy.deletePost(id).subscribe()
  }

  constructor(private httpProxy: HttpProxyService, public dialog: MatDialog, private _httpInterceptor: CustomHttpInterceptor) { }
}
