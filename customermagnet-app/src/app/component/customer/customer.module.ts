import { NgModule } from '@angular/core';

import { CustomersComponent } from './customers/customers.component';
import { NewCustomerComponent } from './new-customer/new-customer.component';
import { ViewCustomerComponent } from './view-customer/view-customer.component';
import { SharedModule } from 'src/app/shared/shared.module';
import { CustomerRoutingModule } from './customer-routing.module';
import { NavbarModule } from '../navbar/navbar.module';
@NgModule({
    declarations: [
        CustomersComponent,
        NewCustomerComponent,
        ViewCustomerComponent
    ],
    imports: [
        SharedModule, CustomerRoutingModule, NavbarModule
    ],
})
export class CustomerModule { }
