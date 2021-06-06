import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {AbstractControl, FormBuilder, ValidationErrors, ValidatorFn, Validators} from "@angular/forms";

@Component({
    selector: 'app-sign-up',
    templateUrl: './sign-up.component.html',
    styleUrls: ['./sign-up.component.css']
})
export class SignUpComponent implements OnInit {

    constructor(
        private authService: AuthService,
        private fb: FormBuilder) {
    }

    passwordsMatchValidator: ValidatorFn = (form: AbstractControl): ValidationErrors | null => {
        const pass1 = form.get('password')?.value;
        const pass2 = form.get('confirmPassword')?.value;
        return pass1 && pass2 && pass1 === pass2 ? null : {mismatch: true}
    }

    signUpFormModel = this.fb.group({
            firstName: [''],
            lastName: [''],
            email: ['', [Validators.required, Validators.email]],
            passwords: this.fb.group({
                password: ['', [Validators.required, Validators.minLength(5)]],
                confirmPassword: ['', [Validators.required, Validators.minLength(5)]]

            }, {validators: this.passwordsMatchValidator})
        }
    );

    ngOnInit() {
        this.signUpFormModel.reset();
    }

    onSubmit() {
        this.authService.signUp(this.signUpFormModel.value).subscribe(
            (res: any) => {
                if (res.succeeded) {
                    this.signUpFormModel.reset();
                    window.location.href = '/';
                    console.log('New user created!', 'Registration successful.');
                } else {
                    // handle error
                    console.log(res);
                }
            },
            err => {
                console.log(err);
            }
        );
    }


}
