import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { HttpProxyService } from './http-proxy.service';
import { IBellNotification } from './message.service';
import { Utility } from '../misc/utility';
import { RESOURCE_NAME } from '../misc/constant';
@Injectable({
    providedIn: 'root'
})
export class UserMessageService {
    private url = Utility.getUserResource(RESOURCE_NAME.USER_BELL_NOTIFICATION)
    constructor(
        public httpProxy: HttpProxyService,
    ) { }
    public latestMessage: IBellNotification[] = [];
    dismiss(value: IBellNotification) {
        this.httpProxy.dismissUserNotification(value.id).subscribe(() => {
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
                this.socket = new WebSocket(`${this.getProtocal()}://${this.getPath()}/auth-svc/monitor/user?jwt=${btoa(next)}`);
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
        this.latestMessage = Utility.copyOf(this.latestMessage)
    }
}
