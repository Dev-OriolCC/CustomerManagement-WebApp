import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpEvent, HttpHeaders } from '@angular/common/http';
import { Observable, catchError, tap, throwError } from 'rxjs';
import { CustomHttpResponse, Profile, Page, CustomerState } from '../interface/appstates';
import { User } from '../interface/user';
import { Key } from '../enum/key.enum';
import { JwtHelperService } from '@auth0/angular-jwt';
import { Customer } from '../interface/customer';
import { Stats } from '../interface/stats';
import { environment } from 'src/environments/environment';

@Injectable()
export class CustomerService {

  private readonly server: string = environment.API_BASE_URL;
  //private jwtHelper = new JwtHelperService();

  constructor(private http: HttpClient) { }


  customersList$ = (page: number = 0, size: number = 10) => <Observable<CustomHttpResponse<Customer & User & Stats>>>
    this.http.get<CustomHttpResponse<Customer & User & Stats>>
      (`${this.server}/api/v1/customer/list?page=${page}&?size=${size}`, {})
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      )

  customerView$ = (id: number) => <Observable<CustomHttpResponse<CustomerState>>>
    this.http.get<CustomHttpResponse<CustomerState>>
      (`${this.server}/api/v1/customer/get/${id}`, {})
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      )

  downloadReport$ = () => <Observable<HttpEvent<Blob>>>
    this.http.get(`${this.server}/api/v1/customer/download/report`, {
      reportProgress: true, observe: 'events', responseType: 'blob'
    })
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      )


  searchCustomers$ = (name: string = "", page: number = 0) => <Observable<CustomHttpResponse<Customer & User>>>
    this.http.get<CustomHttpResponse<Customer & User>>
      (`${this.server}/api/v1/customer/search?name=${name}&page=${page}`, {})
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      )


  createCustomer$ = (customer: Customer) => <Observable<CustomHttpResponse<Customer & User>>>
    this.http.post<CustomHttpResponse<Customer & User>>
      (`${this.server}/api/v1/customer/create`, customer)
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      )

  updateCustomer$ = (customer: Customer) => <Observable<CustomHttpResponse<CustomerState>>>
    this.http.put<CustomHttpResponse<CustomerState>>
      (`${this.server}/api/v1/customer/update`, customer)
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
      if (error.error.reason) {
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
