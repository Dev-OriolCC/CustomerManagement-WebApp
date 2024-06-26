import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { SharedModule } from 'src/app/shared/shared.module';
import { InvoicesComponent } from './invoices/invoices.component';
import { NewInvoiceComponent } from './new-invoice/new-invoice.component';
import { ViewInvoiceComponent } from './view-invoice/view-invoice.component';
import { InvoiceRoutingModule } from './invoice-routing.module';
import { NavbarModule } from '../navbar/navbar.module';
@NgModule({
    declarations: [
        InvoicesComponent, NewInvoiceComponent, ViewInvoiceComponent
    ],
    imports: [
        SharedModule, InvoiceRoutingModule, NavbarModule
    ],
})
export class InvoiceModule { }
