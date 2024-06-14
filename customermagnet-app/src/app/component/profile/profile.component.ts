import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { BehaviorSubject, Observable, catchError, map, of, startWith } from 'rxjs';
import { DataState } from 'src/app/enum/datastate.enum';
import { CustomHttpResponse, Profile } from 'src/app/interface/appstates';
import { State } from 'src/app/interface/state';
import { UserService } from 'src/app/service/user.service';
import { EventType } from '../../enum/event-type.enum';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  
  profileState$: Observable<State<CustomHttpResponse<Profile>>>;
  private dataSubject = new BehaviorSubject<CustomHttpResponse<Profile>>(null);
  private isLoadingSubject = new BehaviorSubject<boolean>(false);
  isLoading$ = this.isLoadingSubject.asObservable();
  private showLogsSubject = new BehaviorSubject<boolean>(false);
  showLogs$ = this.showLogsSubject.asObservable();
  readonly DataState = DataState;
  readonly EventType = EventType;

  constructor(private userService: UserService) { }

  ngOnInit(): void {

    this.profileState$ = this.userService.profile$()
      .pipe(map(response => {
          console.log(response)
          this.dataSubject.next(response);
          return {
            dataState: DataState.LOADED, appData: response
        } 
      }),
        startWith({ dataState: DataState.LOADING }),
        catchError((error: string) => {
          return of({ dataState: DataState.ERROR, appData: this.dataSubject.value, error })
        })
      )

  }

  update(profileForm: NgForm): void {
    this.isLoadingSubject.next(true)
    //console.log(profileForm.)
    this.profileState$ = this.userService.update$(profileForm.value)
      .pipe(map(response => {
        
          console.log(response)
          this.dataSubject.next({...response, data: response.data});
          this.isLoadingSubject.next(false)
          return {
            dataState: DataState.LOADED, appData: this.dataSubject.value
        } 
      }),
        startWith({ dataState: DataState.LOADING, appData: this.dataSubject.value }),
        catchError((error: string) => {
          console.log(error)
          this.isLoadingSubject.next(false)
          return of({ dataState: DataState.ERROR, appData: this.dataSubject.value, error })
        })
      )
  }

  updatePassword(passwordForm: NgForm): void {
    this.isLoadingSubject.next(true)
    if(passwordForm.value.newPassword === passwordForm.value.confirmNewPassword) {
      this.profileState$ = this.userService.updatePassword$(passwordForm.value)
        .pipe(map(response => {
            console.log(response)
            passwordForm.reset();
            this.isLoadingSubject.next(false)
            return { dataState: DataState.LOADED, appData: this.dataSubject.value } 
        }),
          startWith({ dataState: DataState.LOADING, appData: this.dataSubject.value }),
          catchError((error: string) => {
            console.log(error)
            passwordForm.reset();
            this.isLoadingSubject.next(false)
            return of({ dataState: DataState.ERROR, appData: this.dataSubject.value, error })
          })
        ) 
    } else {
      this.isLoadingSubject.next(false)
      console.log("Passwords dont match")
      passwordForm.reset();
    }
    
  }

  updateRole(roleForm: NgForm): void {
    this.isLoadingSubject.next(true)
    console.log(roleForm.value)
    this.profileState$ = this.userService.updateRole$(roleForm.value.roleName)
      .pipe(map(response => {
          console.log(response)
          this.dataSubject.next({...response, data: response.data});
          this.isLoadingSubject.next(false)
          return {
            dataState: DataState.LOADED, appData: this.dataSubject.value
        } 
      }),
        startWith({ dataState: DataState.LOADING, appData: this.dataSubject.value }),
        catchError((error: string) => {
          console.log(error)
          this.isLoadingSubject.next(false)
          return of({ dataState: DataState.ERROR, appData: this.dataSubject.value, error })
        })
      )
  }

  updateAccountSettings(accountSettingsForm: NgForm): void {
    this.isLoadingSubject.next(true)
    console.log(accountSettingsForm.value)
    this.profileState$ = this.userService.updateAccountSettings$(accountSettingsForm.value)
      .pipe(map(response => {
          console.log(response)
          this.dataSubject.next({...response, data: response.data});
          this.isLoadingSubject.next(false)
          return {
            dataState: DataState.LOADED, appData: this.dataSubject.value
        } 
      }),
        startWith({ dataState: DataState.LOADING, appData: this.dataSubject.value }),
        catchError((error: string) => {
          console.log(error)
          this.isLoadingSubject.next(false)
          return of({ dataState: DataState.ERROR, appData: this.dataSubject.value, error })
        })
      )
  }

  toggleMfa(): void {
    this.isLoadingSubject.next(true)
    this.profileState$ = this.userService.toggleMfa$()
      .pipe(map(response => {
          console.log(response)
          this.dataSubject.next({...response, data: response.data});
          this.isLoadingSubject.next(false)
          return { dataState: DataState.LOADED, appData: this.dataSubject.value } 
      }),
        startWith({ dataState: DataState.LOADING, appData: this.dataSubject.value }),
        catchError((error: string) => {
          console.log(error)
          this.isLoadingSubject.next(false)
          return of({ dataState: DataState.ERROR, appData: this.dataSubject.value, error })
        })
      )
  }

  toggleLogs(): void {
    this.showLogsSubject.next(!this.showLogsSubject.value);
  }

  updateProfileImage(image: File): void {
    if(image) {

      this.isLoadingSubject.next(true)
      this.profileState$ = this.userService.updateProfileImage$(this.getFormData(image))
        .pipe(map(response => {
            console.log(response)
            this.dataSubject.next({...response,
              data: {...response.data,
                user: {...response.data.user, imageUrl: `${response.data.user.imageUrl}?time=${new Date().getTime()}`
              }
            }}); 
            this.isLoadingSubject.next(false)
            return { dataState: DataState.LOADED, appData: this.dataSubject.value } 
        }),
          startWith({ dataState: DataState.LOADING, appData: this.dataSubject.value }),
          catchError((error: string) => {
            console.log(error)
            this.isLoadingSubject.next(false)
            return of({ dataState: DataState.ERROR, appData: this.dataSubject.value, error })
          })
        )
    }
    
  }

  private getFormData(image: File): FormData {
    const formData = new FormData();
    formData.append('image', image)
    return formData;
  }
}
