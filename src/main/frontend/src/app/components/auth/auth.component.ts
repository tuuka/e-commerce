import {Component, OnInit} from '@angular/core';
import {AbstractControl, FormBuilder, ValidationErrors, ValidatorFn, Validators} from "@angular/forms";
import {AuthLoginInfo, AuthService, SignUpInfo} from "../../services/auth.service";
import {Router} from "@angular/router";

@Component({
    selector: 'app-auth',
    templateUrl: './auth.component.html',
    styleUrls: ['./auth.component.css']
})
export class AuthComponent implements OnInit {

    hide: boolean = true;
    justRegistered: boolean = false;
    loginErrorMessage: string = '';
    signUpErrorMessage: string = '';

    constructor(private fb: FormBuilder,
                private authService: AuthService,
                private router: Router
    ) {
    }

    passwordsMatchValidator: ValidatorFn = (form: AbstractControl): ValidationErrors | null => {
        const pass1 = form.get('password')?.value;
        const pass2 = form.get('confirmPassword')?.value;
        return pass1 && pass2 && pass1 === pass2 ? null : {mismatch: true}
    }


    loginFormModel = this.fb.group({
            email: ['', [Validators.required, Validators.email]],
            password: ['', [Validators.required, Validators.minLength(5)]]
        }
    );

    signUpFormModel = this.fb.group({
            firstName: ['', [Validators.required]],
            lastName: ['', [Validators.required]],
            email: ['', [Validators.required, Validators.email]],
            passwords: this.fb.group({
                password: ['', [Validators.required, Validators.minLength(5)]],
                confirmPassword: ['', [Validators.required, Validators.minLength(5)]]

            }, {validators: this.passwordsMatchValidator})
        }
    );

    onLoginSubmit() {
        this.authService.login(new AuthLoginInfo(
            this.loginFormModel.value.email, this.loginFormModel.value.password))
            .subscribe(
                (res: any) => {
                    if (!res.error) {
                        this.loginErrorMessage = '';
                        this.loginFormModel.reset();
                        console.log('Login successful.');
                        this.authService.setSession(res);
                        this.justRegistered = false;
                        this.router.navigateByUrl('/products');
                        // window.location.href = '/';
                    } else {
                        this.loginErrorMessage = res.message;
                        // handle error
                    }
                },
                err => {
                    console.log(err.error);
                    this.loginErrorMessage = err.error.message;
                }
            );
    }

    onSignupSubmit() {
        let value = this.signUpFormModel.value;
        this.authService.signUp(
            new SignUpInfo(value.firstName, value.lastName, value.email, value.passwords.password)
        ).subscribe(
            (res: any) => {
                if (!res.error) {
                    this.signUpFormModel.reset();
                    // window.location.href = '/';
                    this.justRegistered = true;
                    console.log('New user created!', 'Confirmation email sent.');
                } else {
                    this.signUpErrorMessage = res.message;
                    console.log(res);
                }
            },
            err => {
                this.signUpErrorMessage = err.error.message;
                console.log(err);
            }
        );
    }

    ngOnInit(): void {
        this.loginFormModel.reset();
        this.signUpFormModel.reset();
    }
}

