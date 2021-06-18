import {Component, OnInit} from '@angular/core';
import {AbstractControl, FormBuilder, ValidationErrors, ValidatorFn, Validators} from "@angular/forms";
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";

@Component({
    selector: 'app-auth',
    templateUrl: './auth.component.html',
    styleUrls: ['./auth.component.css']
})
export class AuthComponent implements OnInit {

    hide: boolean = true;
    loginMessage: string = '';
    signUpMessage: string = '';
    firstName: string = '';
    lastName: string = '';
    email: string = '';
    isLoggedIn: boolean = false;

    constructor(private fb: FormBuilder,
                private authService: AuthService,
                private router: Router
    ) {
    }

    ngOnInit(): void {
        this.loginFormModel.reset();
        this.signUpFormModel.reset();
        this.authService.userInfo.subscribe(info => {
            this.firstName = info.firstName;
            this.lastName = info.lastName;
            this.email = info.email;
            this.isLoggedIn = info.isLoggedIn;
        });
        this.authService.signUpStatus.subscribe(st => {
            this.signUpMessage = st;
        })
        this.authService.loginStatus.subscribe(st => {
            if (st === 'ok') this.router.navigateByUrl('/products');
            this.loginMessage = st;
        })
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
        this.authService.login(this.loginFormModel.value.email, this.loginFormModel.value.password);
    }

    onSignupSubmit() {
        let value = this.signUpFormModel.value;
        this.authService.signUp(value.firstName, value.lastName, value.email, value.passwords.password);
    }

}

