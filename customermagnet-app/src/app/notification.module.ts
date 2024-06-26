import { NgModule } from '@angular/core';
import { NotifierModule, NotifierOptions } from 'angular-notifier';

const notificiationConfig: NotifierOptions = {}


@NgModule({
    imports: [NotifierModule],
    exports: [NotifierModule]
})
export class NotificationModule { }
