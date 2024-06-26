import { NgModule } from '@angular/core';

import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ExtractArrayValuePipe } from '../pipes/extract-array-value.pipe';
@NgModule({
    declarations: [ ExtractArrayValuePipe ],
    imports: [ RouterModule, CommonModule, FormsModule ],
    exports: [ RouterModule, CommonModule, FormsModule, ExtractArrayValuePipe ],
})
export class SharedModule { }
