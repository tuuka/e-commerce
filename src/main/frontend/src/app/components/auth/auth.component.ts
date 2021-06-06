import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AuthLoginInfo, AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";

@Component({
    selector: 'app-auth',
    templateUrl: './auth.component.html',
    styleUrls: ['./auth.component.css']
})
export class AuthComponent implements OnInit {

    // loginForm: FormGroup;
    // signUpForm: FormGroup;
    // isLoggedIn?: boolean;
    //
    // constructor(private fb: FormBuilder,
    //             private authService: AuthService,
    //             private router: Router) {
    //
    //     this.form = this.fb.group({
    //         email: ['', Validators.required],
    //         password: ['', Validators.required]
    //     });
    // }
    //
    //
    // login() {
    //     const val = this.form.value;
    //
    //     if (val.email && val.password) {
    //         this.authService.login(new AuthLoginInfo(val.email, val.password))
    //             .subscribe(
    //                 (res) => {
    //                     this.authService.setSession(res);
    //                     console.log("User is logged in");
    //                     this.router.navigateByUrl('/');
    //                 }
    //             );
    //     }
    // }
    //
    // signup() {
    //     const val = this.form.value;
    // }

    ngOnInit(): void {
        // this.isLoggedIn = this.authService.isLoggedIn()
    }
}
