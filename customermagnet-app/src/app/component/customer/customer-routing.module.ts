import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthenticationGuard } from 'src/app/guard/authentication.guard';
import { CustomersComponent } from './customers/customers.component';
import { NewCustomerComponent } from './new-customer/new-customer.component';
import { ViewCustomerComponent } from './view-customer/view-customer.component';

const customerRoutes: Routes = [

    { path: 'customers', component: CustomersComponent, canActivate: [AuthenticationGuard] },
    { path: 'customers/new', component: NewCustomerComponent, canActivate: [AuthenticationGuard] },
    { path: 'customer/:id', component: ViewCustomerComponent, canActivate: [AuthenticationGuard] }

];

@NgModule({
    imports: [RouterModule.forChild(customerRoutes)],
    exports: [RouterModule]
})
export class CustomerRoutingModule { }
