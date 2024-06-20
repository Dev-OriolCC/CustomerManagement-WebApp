import { Component, OnInit } from '@angular/core';
import { BehaviorSubject, Observable, catchError, map, of, startWith } from 'rxjs';
import { DataState } from 'src/app/enum/datastate.enum';
import { CustomHttpResponse, Profile } from 'src/app/interface/appstates';
import { Customer } from 'src/app/interface/customer';
import { State } from 'src/app/interface/state';
import { User } from 'src/app/interface/user';
import { CustomerService } from 'src/app/service/customer.service';
import { UserService } from 'src/app/service/user.service';
import { HttpEvent, HttpEventType } from '@angular/common/http';
import { saveAs } from 'file-saver';
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  homeState$: Observable<State<CustomHttpResponse<Customer & User>>>;
  private dataSubject = new BehaviorSubject<CustomHttpResponse<Customer & User>>(null);
  private isLoadingSubject = new BehaviorSubject<boolean>(false);
  isLoading$ = this.isLoadingSubject.asObservable();
  private showLogsSubject = new BehaviorSubject<boolean>(false);
  showLogs$ = this.showLogsSubject.asObservable();
  private fileStatusSubject = new BehaviorSubject<{status: string, type: string, percent: number}>(undefined);
  fileStatus$ = this.fileStatusSubject.asObservable();
  private currentPageSubject = new BehaviorSubject<number>(0);
  currentPage$ = this.currentPageSubject.asObservable();
  readonly DataState = DataState;

  
  constructor(private userService: UserService, private customerService: CustomerService) { }

  ngOnInit(): void {
    this.homeState$ = this.customerService.customersList$()
      .pipe(map(response => {
          console.log(response)
          this.dataSubject.next(response);
          return {
            dataState: DataState.LOADED, appData: response
        } 
      }),
        startWith({ dataState: DataState.LOADING }),
        catchError((error: string) => {
          return of({ dataState: DataState.ERROR, error })
        })
      )

  }

  public goToPage(pageNumber?: number): void {
    this.homeState$ = this.customerService.customersList$(pageNumber)
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
        return of({ dataState: DataState.LOADED, error, appData: this.dataSubject.value })
      })
    )
  }

  public goToNextOrPrevPage(direction?: string): void {
    this.goToPage(direction === 'forward' ? this.currentPageSubject.value + 1 : this.currentPageSubject.value - 1);
  }


  public viewCustomer(customer: Customer): void {

  }

  public report(): void {
    console.log("Report...")
    this.homeState$ = this.customerService.downloadReport$()
      .pipe(map(response => {
        console.log(response)
        this.reportProgress(response);
        
        return {
          dataState: DataState.LOADED, appData: this.dataSubject.value
      } 
    }),
      startWith({ dataState: DataState.LOADED, appData: this.dataSubject.value }),
      catchError((error: string) => {
        return of({ dataState: DataState.LOADED, error, appData: this.dataSubject.value })
      })
    )
  }

  private reportProgress(httpEvent: HttpEvent<String[] | Blob>): void {
    console.log(httpEvent)
    switch (httpEvent.type)
    {
      case HttpEventType.DownloadProgress || HttpEventType.UploadProgress:
          this.fileStatusSubject.next({ status: "progress",  type: "downloading", percent: (100 * httpEvent.loaded / httpEvent.total)})
        break;
      
      case HttpEventType.ResponseHeader:
        
        break;

      case HttpEventType.Response:
        saveAs(new File([<Blob>httpEvent.body], httpEvent.headers.get('File-Name'), {type: `${httpEvent.headers.get('Content-Type')};charset=utf-8`} ));

        this.fileStatusSubject.next(undefined);
        break;
      
      default:
        console.log(httpEvent)
        break;
    }
  }


}
