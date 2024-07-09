import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Customer } from '../interface/customer';
import { Observable, catchError, tap, throwError } from 'rxjs';
import { CustomHttpResponse } from '../interface/appstates';
import { User } from '../interface/user';
import { Invoice } from '../interface/invoice';
import { environment } from 'src/environments/environment';

@Injectable()
export class InvoiceService {

  private readonly server: string = environment.API_BASE_URL;
  constructor(private http: HttpClient) { }

  newInvoice$ = () => <Observable<CustomHttpResponse<Customer[] & User>>>
  this.http.get<CustomHttpResponse<Customer[] & User>>
  (`${this.server}/api/v1/invoice/new`, {})
  .pipe(
    tap(console.log),
    catchError(this.handleError)
  )

  createInvoice$ = (customerId: number, invoice: Invoice) => <Observable<CustomHttpResponse<Customer[] & User>>>
  this.http.post<CustomHttpResponse<Customer[] & User>>
  (`${this.server}/api/v1/invoice/addInvoice/to/${customerId}`, invoice)
  .pipe(
    tap(console.log),
    catchError(this.handleError)
  )

  invoicesList$ = (page: number = 0) => <Observable<CustomHttpResponse<Customer & User>>>
  this.http.get<CustomHttpResponse<Customer & User >>
  (`${this.server}/api/v1/invoice/list?page=${page}`, {})
  .pipe(
    tap(console.log),
    catchError(this.handleError)
  )

  getInvoice$ = (invoiceId: number) => <Observable<CustomHttpResponse<Invoice & Customer & User>>>
  this.http.get<CustomHttpResponse<Invoice & Customer & User>>
  (`${this.server}/api/v1/invoice/get/${invoiceId}`, {})
  .pipe(
    tap(console.log),
    catchError(this.handleError)
  )


  private handleError(error: HttpErrorResponse): Observable<any> {
    let errorMessage: string;
    if (error.error instanceof ErrorEvent) {
      // Frontend Error
      errorMessage = `A client error occurred: ${error.error.message}`;
    } else {
      if(error.error.reason) {
        // Backend Error
        errorMessage = error.error.reason;
      } else {
        // Generic Error
        errorMessage = `An error occurred: ${error.status}`;
      }
    }
      return throwError(() => errorMessage);
  }

}
