import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './component/login/login.component';
import { RegisterComponent } from './component/register/register.component';
import { VerifyComponent } from './component/verify/verify.component';
import { ResetpasswordComponent } from './component/resetpassword/resetpassword.component';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { ProfileComponent } from './component/profile/profile.component';
import { HomeComponent } from './component/home/home.component';
import { CustomersComponent } from './component/customers/customers.component';
import { NavbarComponent } from './component/navbar/navbar.component';
import { StatsComponent } from './component/stats/stats.component';
import { TokenInterceptor } from './interceptor/token.interceptor';
import { NewCustomerComponent } from './component/new-customer/new-customer.component';
import { NewInvoiceComponent } from './component/new-invoice/new-invoice.component';
import { InvoicesComponent } from './component/invoices/invoices.component';
import { ViewCustomerComponent } from './component/view-customer/view-customer.component';
import { ViewInvoiceComponent } from './component/view-invoice/view-invoice.component';
import { ExtractArrayValuePipe } from './pipes/extract-array-value.pipe';
import { CacheInterceptor } from './interceptor/cache.interceptor';
@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    VerifyComponent,
    ResetpasswordComponent,
    ProfileComponent,
    HomeComponent,
    CustomersComponent,
    NavbarComponent,
    StatsComponent,
    NewCustomerComponent,
    NewInvoiceComponent,
    InvoicesComponent,
    ViewCustomerComponent,
    ViewInvoiceComponent,
    ExtractArrayValuePipe
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [ 
    { provide: HTTP_INTERCEPTORS, useClass: TokenInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: CacheInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
