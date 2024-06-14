import { Pipe, PipeTransform } from '@angular/core';
import { Invoice } from '../interface/invoice';

@Pipe({
  name: 'extractArrayValue'
})
export class ExtractArrayValuePipe implements PipeTransform {

  transform(value: any, args: string): any {
    let total: number = 0;
    if(args === 'number') {
        let numberArray: number[] = [];
        for(let i = 0; i < value; i++) {
          numberArray.push(i)
        }
        return numberArray;
    } else if(args === 'total') {
      let invoiceArray: Invoice[] = value;
      for(let i = 0; i < invoiceArray.length; i++) {
        total += invoiceArray[i].total;
      }
      console.log(total)
      return total.toFixed(2);
      
    }

    return 0;
  }

}
