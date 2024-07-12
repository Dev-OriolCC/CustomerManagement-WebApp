import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, catchError, tap, throwError } from 'rxjs';
import { CustomHttpResponse, Profile } from '../interface/appstates';
import { User } from '../interface/user';
import { Key } from '../enum/key.enum';
import { JwtHelperService } from '@auth0/angular-jwt';
import { environment } from 'src/environments/environment';

@Injectable()
export class UserService {

  private readonly server: string = environment.API_BASE_URL;
  private jwtHelper = new JwtHelperService();

  constructor(private http: HttpClient) { }


  login$ = (email: string, password: string) => <Observable<CustomHttpResponse<Profile>>>
    this.http.post<CustomHttpResponse<Profile>>
      (`${this.server}/api/v1/user/login`, { email, password })
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      )

  save$ = (user: User) => <Observable<CustomHttpResponse<Profile>>>
    this.http.post<CustomHttpResponse<Profile>>
      (`${this.server}/api/v1/user/register`, user)
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      )

  resetPassword$ = (email: string) => <Observable<CustomHttpResponse<Profile>>>
    this.http.get<CustomHttpResponse<Profile>>
      (`${this.server}/api/v1/user/resetpassword/${email}`, {})
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      )

  verify$ = (key: string, type: string) => <Observable<CustomHttpResponse<Profile>>>
    this.http.get<CustomHttpResponse<Profile>>
      (`${this.server}/api/v1/user/verify/${type}/${key}`)
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      )
  renewPassword$ = (form: {userId: number, password: string, confirmPassword: string}) => <Observable<CustomHttpResponse<Profile>>>
  this.http.post<CustomHttpResponse<Profile>>
    (`${this.server}/api/v1/user/reset/password/`, form)
    .pipe(
      tap(console.log),
      catchError(this.handleError)
    )

  verifyCode$ = (code: string, email: string) => <Observable<CustomHttpResponse<Profile>>>
    this.http.get<CustomHttpResponse<Profile>>
      (`${this.server}/api/v1/user/verify/code/${email}/${code}`)
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      )

  profile$ = () => <Observable<CustomHttpResponse<Profile>>>
    this.http.get<CustomHttpResponse<Profile>>
      (`${this.server}/api/v1/user/profile`)
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      )

  update$ = (user: User) => <Observable<CustomHttpResponse<Profile>>>
    this.http.patch<CustomHttpResponse<Profile>>
      (`${this.server}/api/v1/user/update`, user)
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      )

  updatePassword$ = (form: { currentPassword: string, newPassword: string, confirmNewPassword: string }) => <Observable<CustomHttpResponse<Profile>>>
    this.http.patch<CustomHttpResponse<Profile>>
      (`${this.server}/api/v1/user/update/password`, form)
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      )

  updateRole$ = (roleName: string) => <Observable<CustomHttpResponse<Profile>>>
    this.http.patch<CustomHttpResponse<Profile>>
      (`${this.server}/api/v1/user/update/role/${roleName}`, {})
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      )

  updateAccountSettings$ = (form: { enabled: boolean, non_locked: boolean }) => <Observable<CustomHttpResponse<Profile>>>
    this.http.patch<CustomHttpResponse<Profile>>
      (`${this.server}/api/v1/user/update/account/settings`, form)
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      )

  toggleMfa$ = () => <Observable<CustomHttpResponse<Profile>>>
    this.http.patch<CustomHttpResponse<Profile>>
      (`${this.server}/api/v1/user/toggleMfa`, {})
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      )

  updateProfileImage$ = (formData: FormData) => <Observable<CustomHttpResponse<Profile>>>
    this.http.patch<CustomHttpResponse<Profile>>
      (`${this.server}/api/v1/user/update/profile/image`, formData)
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      )


  refreshToken$ = () => <Observable<CustomHttpResponse<Profile>>>
    this.http.get<CustomHttpResponse<Profile>>
      (`${this.server}/api/v1/user/refresh/token`, { headers: { Authorization: `Bearer ${localStorage.getItem(Key.REFRESH_TOKEN)}` } })
      .pipe(
        tap(response => {
          console.log(response);
          localStorage.removeItem(Key.REFRESH_TOKEN);
          localStorage.removeItem(Key.TOKEN);
          localStorage.setItem(Key.TOKEN, response.data.access_token);
          localStorage.setItem(Key.REFRESH_TOKEN, response.data.refresh_token);
        }),
        catchError(this.handleError)
      )

  logout(): void {
    localStorage.removeItem(Key.TOKEN);
    localStorage.removeItem(Key.REFRESH_TOKEN);
  };

  isAuthenticated = (): boolean => (this.jwtHelper.decodeToken<string>(localStorage.getItem(Key.TOKEN)) && !this.jwtHelper.isTokenExpired(localStorage.getItem(Key.TOKEN)))

  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage: string;
    if (error.error instanceof ErrorEvent) {
      // Frontend Error
      errorMessage = `[ERROR] A client error occurred: ${error.error.message}`;
    } else {
      if (error.error.reason) {
        // Backend Error
        console.log("[ERROR] Backend Error: ", error.error.reason)
        errorMessage = error.error.reason;
      } else {
        // Generic Error
        errorMessage = `[ERROR] An error occurred: ${error.status}`;
      }
    }
    return throwError(() => errorMessage);
  }

}
