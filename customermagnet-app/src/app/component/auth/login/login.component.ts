import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NgForm } from '@angular/forms';
import { BehaviorSubject, Observable, catchError, map, of, startWith } from 'rxjs';
import { LoginState } from 'src/app/interface/appstates';
import { DataState } from 'src/app/enum/datastate.enum';
import { Key } from 'src/app/enum/key.enum';
import { UserService } from 'src/app/service/user.service';
import { NotificationService } from 'src/app/service/notification.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class LoginComponent implements OnInit {

  loginState$: Observable<LoginState> = of({ dataState: DataState.LOADED });
  private phoneSubject = new BehaviorSubject<string | null>(null);
  private emailSubject = new BehaviorSubject<string | null>(null);
  readonly DataState =  DataState;

  constructor(private router: Router, private userService: UserService, private notificationService: NotificationService) { }

  ngOnInit(): void {
    this.userService.isAuthenticated() ? this.router.navigate(['/']) : this.router.navigate(['/login']);
  }

  login(loginForm: NgForm): void {
    console.log("water");

    this.loginState$ = this.userService.login$(loginForm.value.email, loginForm.value.password)
      .pipe(map(response => {
        if (response.data.user.using_nfa) {
          this.notificationService.onDefault(response.message)
          this.phoneSubject.next(response.data.user.phone);
          this.emailSubject.next(response.data.user.email);
          return {
            dataState: DataState.LOADED, loginSuccess: false, isUsingMfa: true,
            phone: response.data.user.phone.substring(response.data.user.phone?.length - 4)
          };
        } else {
          this.notificationService.onDefault(response.message)
          localStorage.setItem(Key.TOKEN, response.data.access_token);
          localStorage.setItem(Key.REFRESH_TOKEN, response.data.refresh_token);
          this.router.navigate(['/']);
          return { dataState: DataState.LOADED, loginSuccess: true }
        }
      }),
        startWith({ dataState: DataState.LOADING, isUsingMfa: false }),
        catchError((error: string) => {
          this.notificationService.onError(error)
          return of({ dataState: DataState.ERROR, isUsingMfa: false, loginSuccess: false, error })
        })
      )
  }

  verifyCode(verifyCodeForm: NgForm): void {
    this.loginState$ = this.userService.verifyCode$(verifyCodeForm.value.code, this.emailSubject.value)
      .pipe(map(response => {
        localStorage.setItem(Key.TOKEN, response.data.access_token);
        localStorage.setItem(Key.REFRESH_TOKEN, response.data.refresh_token);
        this.router.navigate(['/']);
        return { dataState: DataState.LOADED, loginSuccess: true }
      }),
        startWith({ dataState: DataState.LOADING, isUsingMfa: true, loginSuccess: false, 
          phone: this.phoneSubject.value.substring(this.phoneSubject.value.length - 4)
        }),
        catchError((error: string) => {
          return of({ dataState: DataState.ERROR, isUsingMfa: true, loginSuccess: false, error, 
            phone: this.phoneSubject.value.substring(this.phoneSubject.value.length - 4)
          })
        })
      )
  }

  loginPage(): void {
    this.loginState$ = of({ dataState: DataState.LOADED });
  }

}
