import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthenticationGuard } from 'src/app/guard/authentication.guard';
import { InvoicesComponent } from './invoices/invoices.component';
import { NewInvoiceComponent } from './new-invoice/new-invoice.component';
import { ViewInvoiceComponent } from './view-invoice/view-invoice.component';



const invoiceRoutes: Routes = [
    { path: 'invoices', component: InvoicesComponent, canActivate: [AuthenticationGuard] },
    { path: 'invoices/new', component: NewInvoiceComponent, canActivate: [AuthenticationGuard] },
    { path: 'invoice/:id/:invoiceId', component: ViewInvoiceComponent, canActivate: [AuthenticationGuard] },
];

@NgModule({
    imports: [RouterModule.forChild(invoiceRoutes)],
    exports: [RouterModule]
})
export class InvoiceRoutingModule { }
