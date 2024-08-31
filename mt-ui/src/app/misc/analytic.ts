import { Logger } from "./logger";
import { Utility } from "./utility";

declare let gtag: Function;
export class Analytic {
    static buttonClicked(name: string) {
        if (!Utility.isLocalhost()) {
            Logger.debug('firing')
            gtag('event', 'button_click', {
                'event_category': 'Button',
                'event_label': name
            });
        }
    }
}