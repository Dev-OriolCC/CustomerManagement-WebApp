import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute, EventType, ParamMap, Router } from '@angular/router';
import { Observable, BehaviorSubject, map, startWith, catchError, of, switchMap } from 'rxjs';
import { DataState } from 'src/app/enum/datastate.enum';
import { AccountType, CustomHttpResponse, Profile, VerifyState } from 'src/app/interface/appstates';
import { State } from 'src/app/interface/state';
import { User } from 'src/app/interface/user';
import { CustomerService } from 'src/app/service/customer.service';
import { NotificationService } from 'src/app/service/notification.service';
import { UserService } from 'src/app/service/user.service';

@Component({
  selector: 'app-verify',
  templateUrl: './verify.component.html',
  styleUrls: ['./verify.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class VerifyComponent implements OnInit {



  verifyState$: Observable<VerifyState>;
  private userSubject = new BehaviorSubject<User>(null);
  $user = this.userSubject.asObservable();
  private isLoadingSubject = new BehaviorSubject<boolean>(false);
  isLoading$ = this.isLoadingSubject.asObservable();
  readonly DataState = DataState;

  constructor(private activatedRoute: ActivatedRoute, private userService: UserService, private notificationService: NotificationService) { }

  ngOnInit(): void {
    this.verifyState$ = this.activatedRoute.paramMap.pipe(
      switchMap((params: ParamMap) => {
        const type: AccountType = this.getAccountType(window.location.href);
        return this.userService.verify$(params.get('key'), type)
          .pipe(map(response => {
            console.log(response)
            type === "password" ? this.userSubject.next(response.data.user) : null;
            this.notificationService.onDefault(response.message)
            return { type, title: "Verified!", dataState: DataState.LOADED, message: response.message, verifySuccess: true }
          }),
            startWith({ title: "Verifying...", dataState: DataState.LOADING, message: "Wait while we verify...", verifySuccess: false }),
            catchError((error: string) => {
              this.notificationService.onError(error)
              return of({ title: "An error occured", dataState: DataState.ERROR, message: error, error, verifySuccess: false })
            })
          )
      })
    )
  }

  renewPassword(resetPasswordForm: NgForm): void {
    this.isLoadingSubject.next(true);
    this.verifyState$ = this.userService.renewPassword$({
      userId: this.userSubject.value.id,
      password: resetPasswordForm.value.password,
      confirmPassword: resetPasswordForm.value.confirmPassword
    })
      .pipe(map(response => {
        this.isLoadingSubject.next(false);
        this.notificationService.onDefault(response.message)
        return { type: "account" as AccountType, title: "Success!", dataState: DataState.LOADED, message: response.message, verifySuccess: true }
      }),
        startWith({ type: "password" as AccountType, title: "Verified!", dataState: DataState.LOADED, verifySuccess: false }),
        catchError((error: string) => {
          this.isLoadingSubject.next(false);
          this.notificationService.onError(error)
          return of({ type: "password" as AccountType, title: "Verified!", error, dataState: DataState.LOADED, verifySuccess: true })
        })
      )

  }

  getAccountType(href: string): AccountType {
    return href.includes("account") ? "account" : "password";
  }

}
