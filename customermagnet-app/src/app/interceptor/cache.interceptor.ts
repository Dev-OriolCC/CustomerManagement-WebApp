import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpResponse,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, of, tap} from 'rxjs';
import { HttpCacheService } from '../service/http.cache.service';

@Injectable()
export class CacheInterceptor implements HttpInterceptor {

  constructor(private httpCacheService: HttpCacheService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> | Observable<HttpResponse<unknown>> {
    
    if(request.url.includes("verify") || request.url.includes("login") || request.url.includes("register") || request.url.includes("refresh") || 
      request.url.includes("resetpassword") || request.url.includes("verify") 
      || request.url.includes("new/password")|| request.url.includes("reset/password")) {
        return next.handle(request);
    } 
    if(request.method !== "GET" || request.url.includes("download")) {
      this.httpCacheService.evictAll();
      return next.handle(request);
    }

    const cachedResponse: HttpResponse<any> = this.httpCacheService.get(request.url);
    if(cachedResponse) {
      console.log("Response found in cache")
      this.httpCacheService.logCache()
      return of(cachedResponse)
    }
    return this.handleRequestCache(request, next);

  }
  
  private handleRequestCache(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request)
      .pipe(
        tap(response => {
          if(response instanceof HttpResponse && request.method !== "DELETE") {
            console.log("Catching response...")
            this.httpCacheService.put(request.url, response)
          }
        })
      )
    //throw new Error('Method not implemented.');
  }

}
