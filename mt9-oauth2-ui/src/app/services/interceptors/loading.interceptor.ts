import { HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { finalize } from 'rxjs/operators';
import { HttpProxyService } from '../http-proxy.service';

@Injectable()
export class LoadingInterceptor implements HttpInterceptor {
  private totalRequests = 0;

  constructor(private httpProxy: HttpProxyService) { }

  intercept(request: HttpRequest<any>, next: HttpHandler) {
    if (request.headers.get('loading')) {
      return next.handle(request.clone({ headers: request.headers.delete('loading') }));
    }
    this.totalRequests++;
    this.httpProxy.inProgress = true;
    return next.handle(request).pipe(
      finalize(() => {
        this.decreaseRequests();
      })
    );
  }

  private decreaseRequests() {
    this.totalRequests--;
    if (this.totalRequests === 0) {
      this.httpProxy.inProgress = false;
    }
  }
}