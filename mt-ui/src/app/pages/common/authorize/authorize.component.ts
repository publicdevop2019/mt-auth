import { Component, OnInit } from '@angular/core';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { Router, ActivatedRoute } from '@angular/router';
import { switchMap } from 'rxjs/operators';
import { IAuthorizeParty } from 'src/app/clazz/validation/common.interface';

@Component({
  selector: 'app-authorize',
  templateUrl: './authorize.component.html',
  styleUrls: ['./authorize.component.css']
})
export class AuthorizeComponent implements OnInit {
  public authorizeParty: IAuthorizeParty;
  constructor(public httpProxy: HttpProxyService, private router: Router, private activeRoute: ActivatedRoute) {
    this.activeRoute.queryParamMap.pipe(switchMap((queryMaps) => {
      this.authorizeParty = {
        response_type: queryMaps.get('response_type'),
        client_id: queryMaps.get('client_id'),
        state: queryMaps.getAll('state')[1],
        redirect_uri: queryMaps.get('redirect_uri'),
        projectId: queryMaps.get('project_id'),
      }
      return this.httpProxy.autoApprove(this.authorizeParty.projectId,this.authorizeParty.client_id)
    })).subscribe(next => {
      if (next)
        this.authorize();
    })
  }

  ngOnInit() {
  }
  authorize() {
    this.httpProxy.authorize(this.authorizeParty).subscribe(next => {
      location.replace(this.authorizeParty.redirect_uri + '?code=' + next.authorize_code);
    })
  }
  decline() {
    /** clear authorize party info */
    this.router.navigate(['/home']);
  }
}
