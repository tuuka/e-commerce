import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {FormBuilder, Validators} from "@angular/forms";

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

    hide = true;

    constructor(private authService: AuthService,
                private fb: FormBuilder) {
    }

    loginFormModel = this.fb.group({
            email: ['', [Validators.required, Validators.email]],
            password: ['', [Validators.required, Validators.minLength(5)]]
        }
    );

    ngOnInit() {
        this.loginFormModel.reset();
    }

    onSubmit() {
        this.authService.login(this.loginFormModel.value).subscribe(
            (res: any) => {
                if (res.succeeded) {
                    this.loginFormModel.reset();
                    window.location.href = '/';
                    console.log('Login successful.');
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
