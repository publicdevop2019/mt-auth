import { Component, OnInit } from '@angular/core';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { ActivatedRoute } from '@angular/router';
import { switchMap } from 'rxjs/operators';
import { IAuthorizeClientDetail, IAuthorizeParty } from 'src/app/misc/interface';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
import { DeviceService } from 'src/app/services/device.service';
import { Utility } from 'src/app/misc/utility';
import { Logger } from 'src/app/misc/logger';
import { LanguageService } from 'src/app/services/language.service';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-authorize',
  templateUrl: './authorize.component.html',
  styleUrls: ['./authorize.component.css']
})
export class AuthorizeComponent implements OnInit {
  public authorizeParty: IAuthorizeParty;
  avatar: string | ArrayBuffer;
  approvalHistoryKey: string
  name: string;
  clientInfo: IAuthorizeClientDetail;
  constructor(
    public httpProxy: HttpProxyService,
    private router: RouterWrapperService,
    public deviceSvc: DeviceService,
    private activeRoute: ActivatedRoute,
    public langSvc: LanguageService,
  ) {
    this.deviceSvc.updateDocTitle('AUTH_CODE_DOC_TITLE');
    this.activeRoute.queryParamMap.pipe(switchMap((queryMaps) => {
      this.authorizeParty = {
        response_type: queryMaps.get('response_type'),
        client_id: queryMaps.get('client_id'),
        state: queryMaps.getAll('state')[1],
        redirect_uri: queryMaps.get('redirect_uri'),
        projectId: queryMaps.get('project_id'),
      }
      //use jti so user does not need to approve multiple times
      this.approvalHistoryKey = "sso_" + this.authorizeParty.projectId + "_" + this.authorizeParty.client_id + "_" + this.httpProxy.currentUserAuthInfo.jti;
      return this.httpProxy.ssoClient(this.authorizeParty.projectId, this.authorizeParty.client_id)
    })).subscribe(next => {
      this.clientInfo = next;
      if (this.approvedBefore()) {
        this.authorize()
      }
      this.httpProxy.getMyProfile().subscribe(next => {
        this.name = next.username || next.email || next.mobileNumber
      })
    })
  }
  approvedBefore() {
    Logger.debug("last approval is {}", localStorage.getItem(this.approvalHistoryKey))
    return Utility.hasValue(localStorage.getItem(this.approvalHistoryKey))
  }

  ngOnInit() {
  }
  authorize() {
    localStorage.setItem(this.approvalHistoryKey, 'true')
    this.httpProxy.authorize(this.authorizeParty).subscribe(next => {
      location.replace(this.authorizeParty.redirect_uri + '?code=' + next.authorize_code);
    })
  }
  decline() {
    /** clear authorize party info */
    this.router.navProjectHome();
    localStorage.removeItem(this.approvalHistoryKey)
  }
  getAvatar() {
    this.httpProxy.getAvatar().subscribe(blob => {
      Utility.createImageFromBlob(blob, (reader) => {
        this.avatar = reader.result
      })
    })
  }
  firstLetter(name: string) {
    if (!name) {
      return '';
    }
    return name.substring(0, 1).toUpperCase()
  }
  isEnglish() {
    return this.langSvc.currentLanguage() === 'enUS'
  }
  isChinese() {
    return this.langSvc.currentLanguage() === 'zhHans'
  }
}
