import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { HttpProxyService } from './http-proxy.service';
import { Utility } from '../misc/utility';
import { RESOURCE_NAME } from '../misc/constant';
import { IIdBasedEntity } from '../misc/interface';
export interface IBellNotification extends IIdBasedEntity {
    title: string,
    descriptions: string[],
    date: number
}
@Injectable({
    providedIn: 'root'
})
export class MessageService {
    private url = Utility.getMgmtResource(RESOURCE_NAME.MGMT_BELL_NOTIFICATION)
    constructor(
        public httpProxy: HttpProxyService,
    ) { }
    public latestMessage: IBellNotification[] = [];
    dismiss(value: IBellNotification) {
        this.httpProxy.dismissNotification(value.id).subscribe(() => {
            this.latestMessage = this.latestMessage.filter(e => e.id !== value.id)
        })
    }
    pullUnAckMessage() {
        if (environment.mode !== 'offline') {
            this.httpProxy.readEntityByQuery<IBellNotification>(this.url, 0, 200, 'unAck:1').subscribe(next => {
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
            this.httpProxy.createEntity(environment.serverUri + `/auth-svc/tickets/0C8AZTODP4HT`, null, Utility.getChangeId()).subscribe(next => {
                this.socket = new WebSocket(`${this.getProtocal()}://${this.getPath()}/auth-svc/monitor?jwt=${btoa(next)}`);
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
        if (!environment.demo && environment.serverUri.includes('localhost')) {
            return 'localhost:8111'
        }
        return environment.serverUri.replace('http://', '').replace("https://", "")
    }
    //clone so view will render as new with new timestamp
    clone() {
        this.latestMessage = Utility.copyOf(this.latestMessage)
    }
}
