import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { EntityCommonService } from '../clazz/entity.common-service';
import { IIdBasedEntity } from '../clazz/summary.component';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
import { AuthService } from './auth.service';
import * as UUID from 'uuid/v1';
import { DeviceService } from './device.service';
import { copyOf } from './utility';
import { IBellNotification } from './message.service';
@Injectable({
    providedIn: 'root'
})
export class UserMessageService extends EntityCommonService<IBellNotification, IBellNotification>{
    private SVC_NAME = '/auth-svc';
    private ENTITY_NAME = '/user/notifications/bell';
    entityRepo: string = environment.serverUri + this.SVC_NAME + this.ENTITY_NAME;
    constructor(public authSvc: AuthService, httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor, deviceSvc: DeviceService) {
        super(httpProxy, interceptor, deviceSvc);
    }
    public latestMessage: IBellNotification[] = [];
    dismiss(value: IBellNotification) {
        this.httpProxySvc.dismissUserNotification(value.id).subscribe(() => {
            this.latestMessage = this.latestMessage.filter(e => e.id !== value.id)
        })
    }
    pullUnAckMessage() {
        if (environment.mode !== 'offline') {
            this.readEntityByQuery(0, 200, 'unAck:1').subscribe(next => {
                this.latestMessage = next.data
            });
        }
    }
    saveMessage(message: string) {
        this.latestMessage = [JSON.parse(message), ...this.latestMessage]
    }
    private socket: WebSocket;
    connectToMonitor() {
        if (environment.mode !== 'offline') {
            this.httpProxySvc.createEntity(environment.serverUri + `/auth-svc/tickets/0C8AZTODP4HT`, null, UUID()).subscribe(next => {
                this.socket = new WebSocket(`${this.getProtocal()}://${this.getPath()}/auth-svc/user/monitor?jwt=${btoa(next)}`);
                this.socket.addEventListener('message', (event) => {
                    if (event.data !== '_renew')
                        this.saveMessage(event.data as string);
                });
            });
        }
    }
    clear() {
        this.latestMessage = [];
    }
    private getProtocal() {
        let protocal = "ws"
        if (environment.serverUri.includes("https")) {
            protocal = "wss"
        }
        return protocal;
    }
    private getPath() {
        if (environment.serverUri.includes('localhost')) {
            return 'localhost:8111'
        }
        return environment.serverUri.replace('http://', '').replace("https://", "")
    }
    //clone so view will render as new with new timestamp
    clone() {
        this.latestMessage = copyOf(this.latestMessage)
    }
}
