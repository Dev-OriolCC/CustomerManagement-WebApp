import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { BehaviorSubject, Observable, catchError, map, of, startWith, switchMap } from 'rxjs';
import { DataState } from 'src/app/enum/datastate.enum';
import { CustomHttpResponse } from 'src/app/interface/appstates';
import { Customer } from 'src/app/interface/customer';
import { Invoice } from 'src/app/interface/invoice';
import { State } from 'src/app/interface/state';
import { User } from 'src/app/interface/user';
import { CustomerService } from 'src/app/service/customer.service';
import { InvoiceService } from 'src/app/service/invoice.service';
import jsPDF from 'jspdf';

@Component({
  selector: 'app-view-invoice',
  templateUrl: './view-invoice.component.html',
  styleUrls: ['./view-invoice.component.css']
})
export class ViewInvoiceComponent implements OnInit {

  invoiceState$: Observable<State<CustomHttpResponse<Invoice & Customer & User>>>;
  private dataSubject = new BehaviorSubject<CustomHttpResponse<Invoice & Customer & User>>(null);
  private isLoadingSubject = new BehaviorSubject<boolean>(false);
  isLoading$ = this.isLoadingSubject.asObservable();

  readonly DataState = DataState;
  private readonly INVOICE_ID = "id";

  constructor(private activatedRoute: ActivatedRoute, private invoiceService: InvoiceService) { }

  ngOnInit(): void {
    console.log("Loading...")
    this.invoiceState$ = this.activatedRoute.paramMap.pipe(
      switchMap((params: ParamMap) => {

        return this.invoiceService.getInvoice$(+params.get(this.INVOICE_ID))
        .pipe(map(response => {
            console.log(response)
            this.dataSubject.next(response);
            return { dataState: DataState.LOADED, appData: response } 
        }),
          startWith({ dataState: DataState.LOADING }),
          catchError((error: string) => {
            return of({ dataState: DataState.ERROR, error })
          })
        )
      })
    )
  }

  exportAsPDF(): void {
    const filename = `invoice-${this.dataSubject.value.data['invoice'].invoiceNumber}.pdf`;
    const doc = new jsPDF();
    //var pageHeight= doc.internal.pageSize.getHeight();
    //doc.addPage(document.getElementById('invoice'))
    doc.html(document.getElementById('invoice'), {margin: 5, windowWidth: 1000, width: 200, callback: (invoice) => invoice.save(filename) } )
    doc.save(filename)
    
  }

}
