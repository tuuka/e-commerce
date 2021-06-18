import {HttpErrorResponse} from "@angular/common/http";
import {throwError} from "rxjs";

export class HttpErrorHandler {
    public static handleError(error: HttpErrorResponse) {
        if (error.status === 0) {
            // A client-side or network error occurred. Handle it accordingly.
            console.error('An error occurred:', error.error);
        } else {
            // The backend returned an unsuccessful response code.
            // The response body may contain clues as to what went wrong.
            console.error(
                `Backend returned code ${error.status}, ` +
                `message: ${error.error.message}`);
        }
        // Return an observable with a user-facing error message.
        return throwError(
            `${error.error.message}`);
    }
}
