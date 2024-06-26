import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { BehaviorSubject, Observable, catchError, map, of, startWith } from 'rxjs';
import { DataState } from 'src/app/enum/datastate.enum';
import { EventType } from 'src/app/enum/event-type.enum';
import { CustomHttpResponse, Profile } from 'src/app/interface/appstates';
import { Customer } from 'src/app/interface/customer';
import { State } from 'src/app/interface/state';
import { User } from 'src/app/interface/user';
import { CustomerService } from 'src/app/service/customer.service';
import { InvoiceService } from 'src/app/service/invoice.service';
import { NotificationService } from 'src/app/service/notification.service';
import { UserService } from 'src/app/service/user.service';


@Component({
  selector: 'app-new-invoice',
  templateUrl: './new-invoice.component.html',
  styleUrls: ['./new-invoice.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush

})
export class NewInvoiceComponent implements OnInit {
  
  newInvoiceState$: Observable<State<CustomHttpResponse<Customer[] & User>>>;
  private dataSubject = new BehaviorSubject<CustomHttpResponse<Customer[] & User>>(null);
  private isLoadingSubject = new BehaviorSubject<boolean>(false);
  isLoading$ = this.isLoadingSubject.asObservable();
  readonly DataState = DataState;
  readonly EventType = EventType;

  constructor(private userService: UserService, private customerService: CustomerService, private invoiceService: InvoiceService, private notificationService: NotificationService) { }

  ngOnInit(): void {
    this.newInvoiceState$ = this.invoiceService.newInvoice$()
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

  createInvoice(newInvoiceForm: NgForm): void {
    this.isLoadingSubject.next(true)
    this.dataSubject.next({...this.dataSubject.value, message: null});
    console.log()
    this.newInvoiceState$ = this.invoiceService.createInvoice$(newInvoiceForm.value.customerId, newInvoiceForm.value)  
      .pipe(map(response => {
          this.notificationService.onSuccess(response.message)
          console.log(response)
          this.isLoadingSubject.next(false)
          newInvoiceForm.resetForm({status: 'PENDING'});
          this.dataSubject.next(response);
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
