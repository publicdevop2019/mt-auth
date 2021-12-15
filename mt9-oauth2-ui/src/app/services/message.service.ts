import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { EntityCommonService } from '../clazz/entity.common-service';
import { IIdBasedEntity } from '../clazz/summary.component';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
import * as UUID from 'uuid/v1';
import { DeviceService } from './device.service';
export interface IDetail extends IIdBasedEntity {
    date: number
    message: string
}
@Injectable({
    providedIn: 'root'
})
export class MessageService extends EntityCommonService<IDetail, IDetail>{
    private SVC_NAME = '/messenger-svc';
    private ENTITY_NAME = '/systemNotifications';
    entityRepo: string = environment.serverUri + this.SVC_NAME + this.ENTITY_NAME;
    role: string = 'root';
    constructor(httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor,deviceSvc:DeviceService) {
        super(httpProxy, interceptor,deviceSvc);
    }
    public latestMessage: string[] = [];
    saveMessage(message: string) {
        if (this.latestMessage.length === 5) {
            this.latestMessage.splice(0, 1)
        }
        this.latestMessage.push(message);
    }
    private socket: WebSocket;
    connectSystemMonitor() {
        if (environment.mode !== 'offline') {
            const jwtBody = this.httpProxySvc.currentUserAuthInfo.access_token.split('.')[1];
            const raw = atob(jwtBody);
            if ((JSON.parse(raw).authorities as string[]).filter(e => e === "0R8G09B8K64H").length > 0) {
                //0C8AZTODP4H5 messenger client id
                this.httpProxySvc.createEntity(environment.serverUri + '/auth-svc/tickets', '0C8AZTODP4H5', null, UUID()).subscribe(next => {
                    this.socket = new WebSocket(`${this.getProtocal()}://${this.getPath()}/messenger-svc/system-monitor?jwt=${btoa(next)}`);
                    this.socket.addEventListener('message', (event) => {
                        this.saveMessage(event.data as string);
                    });
                });
            }
        }
    }
    connectMallMonitor() {
        if (environment.mode !== 'offline') {
            const jwtBody = this.httpProxySvc.currentUserAuthInfo.access_token.split('.')[1];
            const raw = atob(jwtBody);
            if ((JSON.parse(raw).authorities as string[]).filter(e => e === "0R8G09BPEZGG").length > 0) {
                this.httpProxySvc.createEntity(environment.serverUri + '/auth-svc/tickets', '0C8AZTODP4H5', null, UUID()).subscribe(next => {
                    this.socket = new WebSocket(`${this.getProtocal()}://${this.getPath()}/messenger-svc/mall-monitor?jwt=${btoa(next)}`);
                    this.socket.addEventListener('message', (event) => {
                        this.saveMessage(event.data as string);
                    });
                });
            }
        }
    }
    clear() {
        this.latestMessage = [];
    }
    private getProtocal(){
        let protocal="ws"
        if(environment.serverUri.includes("https")){
            protocal="wss"
        }
        return protocal;
    }
    private getPath(){
        if(environment.serverUri.includes('localhost')){
            return 'localhost:8111'
        }
        return environment.serverUri.replace('http://','').replace("https://","")
    }
}
