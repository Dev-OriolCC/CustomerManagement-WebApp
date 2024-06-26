import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';
import { ProfileComponent } from './component/profile/profile.component';
import { HomeComponent } from './component/home/home/home.component';
import { AuthenticationGuard } from './guard/authentication.guard';
import { InvoicesComponent } from './component/invoice/invoices/invoices.component';
import { NewInvoiceComponent } from './component/invoice/new-invoice/new-invoice.component';
import { ViewInvoiceComponent } from './component/invoice/view-invoice/view-invoice.component';

const routes: Routes = [
  { path: 'profile', loadChildren: () => import('./component/profile/profile.module').then(module => module.ProfileModule) },
  { path: '', redirectTo: '/', pathMatch: 'full' },
  { path: '**', component: HomeComponent, canActivate: [AuthenticationGuard] }
  
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {preloadingStrategy: PreloadAllModules} )],
  exports: [RouterModule]
})
export class AppRoutingModule { }
