import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { BehaviorSubject, Observable, catchError, map, of, startWith } from 'rxjs';
import { DataState } from 'src/app/enum/datastate.enum';
import { EventType } from 'src/app/enum/event-type.enum';
import { CustomHttpResponse, Profile } from 'src/app/interface/appstates';
import { State } from 'src/app/interface/state';
import { CustomerService } from 'src/app/service/customer.service';
import { NotificationService } from 'src/app/service/notification.service';
import { UserService } from 'src/app/service/user.service';

@Component({
  selector: 'app-new-customer',
  templateUrl: './new-customer.component.html',
  styleUrls: ['./new-customer.component.css', '../../../app.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush

})
export class NewCustomerComponent implements OnInit {
  
  newCustomerState$: Observable<State<CustomHttpResponse<Profile>>>;
  private dataSubject = new BehaviorSubject<CustomHttpResponse<Profile>>(null);
  private isLoadingSubject = new BehaviorSubject<boolean>(false);
  isLoading$ = this.isLoadingSubject.asObservable();
  private showLogsSubject = new BehaviorSubject<boolean>(false);
  showLogs$ = this.showLogsSubject.asObservable();
  readonly DataState = DataState;
  readonly EventType = EventType;

  constructor(private userService: UserService, private customerService: CustomerService, private notificationService: NotificationService) { }

  ngOnInit(): void {
    this.newCustomerState$ = this.userService.profile$()
      .pipe(map(response => {
          console.log(response)
          this.dataSubject.next(response);
          return {
            dataState: DataState.LOADED, appData: response
        } 
      }),
        startWith({ dataState: DataState.LOADING }),
        catchError((error: string) => {
          this.notificationService.onError(error)
          return of({ dataState: DataState.ERROR, appData: this.dataSubject.value, error })
        })
      )

  }

  createCustomer(newCustomerForm: NgForm): void {    
    this.isLoadingSubject.next(true)
    this.newCustomerState$ = this.customerService.createCustomer$(newCustomerForm.value)  
      .pipe(map(response => {
          console.log(response)
          this.notificationService.onSuccess(response.message)
          this.isLoadingSubject.next(false)
          newCustomerForm.resetForm({type: 'INDIVIDUAL', status: 'ACTIVE'});
          return { dataState: DataState.LOADED, appData: this.dataSubject.value } 
      }),
        startWith({ dataState: DataState.LOADED, appData: this.dataSubject.value }),
        catchError((error: string) => {
          this.isLoadingSubject.next(false)
          this.notificationService.onError(error)
          return of({ dataState: DataState.LOADED, error })
        })
      )

  }

}
