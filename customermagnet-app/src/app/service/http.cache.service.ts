import { HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class HttpCacheService {

  private httpResponseCache: { [key: string]: HttpResponse<any> } = {};
  
  put = (key: string, httpResponse: HttpResponse<any>): void => {
    console.log(`put ${key}`)
    this.httpResponseCache[key] = httpResponse
  }

  get = (key: string): HttpResponse<any> | undefined  | null => {
    return this.httpResponseCache[key];
  }

  evict = (key: string): boolean => {
    return delete this.httpResponseCache[key];
  }

  evictAll = (): void => {
    this.httpResponseCache = {};
  }

  logCache = (): void => {
    console.log(this.httpResponseCache);
  }

}
