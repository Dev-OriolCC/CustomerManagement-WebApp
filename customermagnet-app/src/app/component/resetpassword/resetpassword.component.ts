import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Observable, of, map, startWith, catchError } from 'rxjs';
import { DataState } from 'src/app/enum/datastate.enum';
import { RegisterState } from 'src/app/interface/appstates';
import { UserService } from 'src/app/service/user.service';

@Component({
  selector: 'app-resetpassword',
  templateUrl: './resetpassword.component.html',
  styleUrls: ['./resetpassword.component.css']
})
export class ResetpasswordComponent {
  resetPasswordState$: Observable<RegisterState> = of({ dataState: DataState.LOADED })
  readonly DataState = DataState;

  constructor(private userService: UserService) { };

  resetPassword(resetPasswordForm: NgForm) {
    console.log(resetPasswordForm.value)
    this.resetPasswordState$ = this.userService.resetPassword$(resetPasswordForm.value.email)
      .pipe(map(response => {
        console.log("Response: " + response)
        resetPasswordForm.reset()
        return { dataState: DataState.LOADED, registerSuccess: true, message: response.message }
      }),
        startWith({ dataState: DataState.LOADING, registerSuccess: false }),
        catchError((error: string) => {
          console.log("ERROR: " + error)
          return of({ dataState: DataState.ERROR, registerSuccess: false, error })
        })
      );
  }

}
