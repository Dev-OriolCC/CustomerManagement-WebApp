import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { BehaviorSubject, Observable, catchError, map, of, startWith, switchMap } from 'rxjs';
import { DataState } from 'src/app/enum/datastate.enum';
import { CustomHttpResponse, CustomerState } from 'src/app/interface/appstates';
import { State } from 'src/app/interface/state';
import { CustomerService } from 'src/app/service/customer.service';
import { NotificationService } from 'src/app/service/notification.service';
import { UserService } from 'src/app/service/user.service';

@Component({
  selector: 'app-view-customer',
  templateUrl: './view-customer.component.html',
  styleUrls: ['./view-customer.component.css', '../../../app.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush

})
export class ViewCustomerComponent implements OnInit {

  customerState$: Observable<State<CustomHttpResponse<CustomerState>>>;
  private dataSubject = new BehaviorSubject<CustomHttpResponse<CustomerState>>(null);
  private isLoadingSubject = new BehaviorSubject<boolean>(false);
  isLoading$ = this.isLoadingSubject.asObservable();

  readonly DataState = DataState;
  private readonly CUSTOMER_ID = "id";

  constructor(private activatedRoute: ActivatedRoute, private customerService: CustomerService, private notificationService: NotificationService) { }

  ngOnInit(): void {
    this.customerState$ = this.activatedRoute.paramMap.pipe(
      switchMap((params: ParamMap) => {

        return this.customerService.customerView$(+params.get(this.CUSTOMER_ID))
        .pipe(map(response => {
            this.dataSubject.next(response);
            return { dataState: DataState.LOADED, appData: response } 
        }),
          startWith({ dataState: DataState.LOADING }),
          catchError((error: string) => {
            this.notificationService.onError(error)
            return of({ dataState: DataState.ERROR, error })
          })
        )

      })
    )
  }

  updateCustomer(customerForm: NgForm): void {
    this.isLoadingSubject.next(true)
    this.customerState$ = this.customerService.updateCustomer$(customerForm.value)
      .pipe(map(response => {

          console.log(response)
          this.dataSubject.next({...response, data: { ...response.data, customers: { ...response.data.customers
            , invoices: this.dataSubject.value.data.customers.invoices }}});
          this.notificationService.onSuccess(response.message)
          this.isLoadingSubject.next(false)
          return {
            dataState: DataState.LOADED, appData: this.dataSubject.value
        } 
      }),
        startWith({ dataState: DataState.LOADED, appData: this.dataSubject.value }),
        catchError((error: string) => {
          console.log(error)
          this.isLoadingSubject.next(false)
          this.notificationService.onError(error)
          return of({ dataState: DataState.ERROR, appData: this.dataSubject.value, error })
        })
      )
  }

}
