import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { EntityCommonService } from '../clazz/entity.common-service';
import { IIdBasedEntity } from '../clazz/summary.component';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
import * as UUID from 'uuid/v1';
import { DeviceService } from './device.service';
export interface INotification extends IIdBasedEntity{
    title:string,
    descriptions:string[],
    date:number
}
@Injectable({
    providedIn: 'root'
})
export class MessageService extends EntityCommonService<INotification, INotification>{
    private SVC_NAME = '/auth-svc';
    private ENTITY_NAME = '/mngmt/notifications';
    entityRepo: string = environment.serverUri + this.SVC_NAME + this.ENTITY_NAME;
    constructor(httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor,deviceSvc:DeviceService) {
        super(httpProxy, interceptor,deviceSvc);
    }
    public latestMessage: INotification[] = [];
    saveMessage(message: string) {
        if (this.latestMessage.length === 5) {
            this.latestMessage.splice(0, 1)
        }
        this.latestMessage.push(JSON.parse(message));
    }
    private socket: WebSocket;
    connectToMonitor() {
        if (environment.mode !== 'offline') {
            this.httpProxySvc.createEntity(environment.serverUri + `/auth-svc/tickets/0C8AZTODP4HT`, null, UUID()).subscribe(next => {
                this.socket = new WebSocket(`${this.getProtocal()}://${this.getPath()}/auth-svc/monitor?jwt=${btoa(next)}`);
                this.socket.addEventListener('message', (event) => {
                    this.saveMessage(event.data as string);
                });
            });
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
