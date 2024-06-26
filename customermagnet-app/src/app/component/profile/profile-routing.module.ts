import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthenticationGuard } from 'src/app/guard/authentication.guard';
import { ProfileComponent } from './profile.component';

const profileRoutes: Routes = [
    { path: '', children: [{ path: '', component: ProfileComponent, canActivate: [AuthenticationGuard] }] },
];

@NgModule({
    imports: [RouterModule.forChild(profileRoutes)],
    exports: [RouterModule]
})
export class ProfileRoutingModule { }
