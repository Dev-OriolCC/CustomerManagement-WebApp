import { NgModule } from '@angular/core';
import { SharedModule } from 'src/app/shared/shared.module';
import { ProfileComponent } from './profile.component';
import { ProfileRoutingModule } from './profile-routing.module';
import { NavbarModule } from '../navbar/navbar.module';

@NgModule({
    declarations: [ ProfileComponent ],
    imports: [ SharedModule, ProfileRoutingModule, NavbarModule ],
})
export class ProfileModule { }
