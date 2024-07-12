import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { BehaviorSubject, Observable, catchError, map, of, startWith } from 'rxjs';
import { DataState } from 'src/app/enum/datastate.enum';
import { CustomHttpResponse, Profile } from 'src/app/interface/appstates';
import { Customer } from 'src/app/interface/customer';
import { State } from 'src/app/interface/state';
import { User } from 'src/app/interface/user';
import { ExtractArrayValuePipe } from 'src/app/pipes/extract-array-value.pipe';
import { CustomerService } from 'src/app/service/customer.service';
import { NotificationService } from 'src/app/service/notification.service';
import { UserService } from 'src/app/service/user.service';
@Component({
  selector: 'app-customers',
  templateUrl: './customers.component.html',
  styleUrls: ['./customers.component.css', '../../../app.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CustomersComponent implements OnInit {

  customersState$: Observable<State<CustomHttpResponse<Customer & User>>>;
  private dataSubject = new BehaviorSubject<CustomHttpResponse<Customer & User>>(null);
  private isLoadingSubject = new BehaviorSubject<boolean>(false);
  isLoading$ = this.isLoadingSubject.asObservable();
  private showLogsSubject = new BehaviorSubject<boolean>(false);
  showLogs$ = this.showLogsSubject.asObservable();
  private currentPageSubject = new BehaviorSubject<number>(0);
  currentPage$ = this.currentPageSubject.asObservable();
  readonly DataState = DataState;

  
  constructor(private userService: UserService, private customerService: CustomerService, private notificationService: NotificationService) { }

  ngOnInit(): void {
    this.customersState$ = this.customerService.customersList$()
      .pipe(map(response => {
          console.log(response)
          this.dataSubject.next(response);
          return {
            dataState: DataState.LOADED, appData: response
        } 
      }),
        startWith({ dataState: DataState.LOADING }),
        catchError((error: string) => {
          this.notificationService.onError(error);
          return of({ dataState: DataState.ERROR, error })
        })
      )
  }

  searchCustomers(searchCustomers: NgForm): void {
    this.customersState$ = this.customerService.searchCustomers$(searchCustomers.value.name)
      .pipe(map(response => {
          console.log(response)
          this.currentPageSubject.next(0)
          this.dataSubject.next(response);
          return { dataState: DataState.LOADED, appData: response } 
      }),
        startWith({ dataState: DataState.LOADED }),
        catchError((error: string) => {
          this.notificationService.onError(error)
          return of({ dataState: DataState.ERROR, error })
        })
      )
  }

  public goToPage(pageNumber?: number, name?: string): void {
    this.customersState$ = this.customerService.searchCustomers$(name, pageNumber)
      .pipe(map(response => {
        console.log(response)
        this.dataSubject.next(response);
        this.currentPageSubject.next(pageNumber);
        return {
          dataState: DataState.LOADED, appData: response
      } 
    }),
      startWith({ dataState: DataState.LOADED, appData: this.dataSubject.value }),
      catchError((error: string) => {
        this.notificationService.onError(error)
        return of({ dataState: DataState.LOADED, error, appData: this.dataSubject.value })
      })
    )
  }

  public goToNextOrPrevPage(direction?: string, name?: string ): void {
    this.goToPage(direction === 'forward' ? this.currentPageSubject.value + 1 : this.currentPageSubject.value - 1, name);
  }


  public viewCustomer(customer: Customer): void {

  }

}

