import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './component/login/login.component';
import { RegisterComponent } from './component/register/register.component';
import { VerifyComponent } from './component/verify/verify.component';
import { ResetpasswordComponent } from './component/resetpassword/resetpassword.component';
import { CustomersComponent } from './component/customers/customers.component';
import { ProfileComponent } from './component/profile/profile.component';
import { HomeComponent } from './component/home/home.component';
import { AuthenticationGuard } from './guard/authentication.guard';
import { NewCustomerComponent } from './component/new-customer/new-customer.component';
import { CustomerComponent } from './component/customer/customer.component';
import { InvoicesComponent } from './component/invoices/invoices.component';
import { NewInvoiceComponent } from './component/new-invoice/new-invoice.component';
import { ViewInvoiceComponent } from './component/view-invoice/view-invoice.component';
import { ViewCustomerComponent } from './component/view-customer/view-customer.component';

const routes: Routes = [
  
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'resetpassword', component: ResetpasswordComponent },
  { path: 'user/verify/password/:key', component: VerifyComponent }, 
  { path: 'user/verify/account/:key', component: VerifyComponent }, 
  { path: 'customers', component: CustomersComponent, canActivate: [AuthenticationGuard] },
  { path: 'customers/new', component: NewCustomerComponent, canActivate: [AuthenticationGuard] },
  { path: 'customer/:id', component: ViewCustomerComponent, canActivate: [AuthenticationGuard] },
  { path: 'invoices', component: InvoicesComponent, canActivate: [AuthenticationGuard] },
  { path: 'invoices/new', component: NewInvoiceComponent, canActivate: [AuthenticationGuard] },
  { path: 'invoice/:id/:invoiceId', component: ViewInvoiceComponent, canActivate: [AuthenticationGuard] },
  { path: 'profile', component: ProfileComponent, canActivate: [AuthenticationGuard] },
  { path: '', component: HomeComponent, canActivate: [AuthenticationGuard] },
  { path: '', redirectTo: '/', pathMatch: 'full' },
  { path: '**', component: HomeComponent }
  
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
