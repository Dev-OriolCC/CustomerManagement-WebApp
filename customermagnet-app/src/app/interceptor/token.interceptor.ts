import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpResponse,
  HttpErrorResponse
} from '@angular/common/http';
import { BehaviorSubject, Observable, catchError, switchMap, throwError } from 'rxjs';
import { Key } from '../enum/key.enum';
import { UserService } from '../service/user.service';
import { CustomHttpResponse, Profile } from '../interface/appstates';

@Injectable({providedIn: 'root'})
export class TokenInterceptor implements HttpInterceptor {

  private isRefreshTokenLoading: boolean = false;
  private refreshTokenSubject = new BehaviorSubject<CustomHttpResponse<Profile>>(null); 

  constructor(private userService: UserService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> | Observable<HttpResponse<unknown>> {
    
    if(request.url.includes("verify") || request.url.includes("login") || request.url.includes("register") || request.url.includes("refresh") || 
      request.url.includes("resetpassword")) {
        return next.handle(request);
    } 
    return next.handle(this.addAuthorizationTokenHeader(request, localStorage.getItem(Key.TOKEN)))
    .pipe(
        catchError((error: HttpErrorResponse) => {
          console.log(error)
          if(error instanceof HttpErrorResponse && error.status == 401 ){ //&& error.error.reason.includes("need to login")){
            return this.handleRefreshToken(request, next)
          } else {
            console.log("error");
            return throwError(() => error);
          }
          
        })
    );
    
  };
  private handleRefreshToken(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    if(!this.isRefreshTokenLoading) {
      console.log("Refresh token...");
      this.isRefreshTokenLoading = true;
      this.refreshTokenSubject.next(null);
      return this.userService.refreshToken$().pipe(
        switchMap((response) => {
          this.isRefreshTokenLoading = false;
          this.refreshTokenSubject.next(response);
          console.log("New token",response.data.access_token);
          return next.handle(this.addAuthorizationTokenHeader(request, response.data.access_token));
        })
      );
    } else {
      return this.userService.refreshToken$().pipe(
        switchMap((response) => {
          return next.handle(this.addAuthorizationTokenHeader(request, response.data.access_token));
        })
      );
    }
  }

  private addAuthorizationTokenHeader(request: HttpRequest<unknown>, token: string): HttpRequest<any> {
    return request.clone({ setHeaders: { 'Authorization': `Bearer ${token}` } });
  }
}
