import {Component, OnInit} from '@angular/core';
import {FormBuilder, Validators} from "@angular/forms";
import {Router} from "@angular/router";

@Component({
    selector: 'app-checkout',
    templateUrl: './checkout.component.html',
    styleUrls: ['./checkout.component.css']
})
export class CheckoutComponent implements OnInit {

    constructor(private fb: FormBuilder,
                // private authService: AuthService,
                private router: Router) {
    }

    ngOnInit(): void {
        this.checkoutFormModel.reset();
    }

    checkoutFormModel = this.fb.group({
            customer: this.fb.group({
                firstName: ['', [Validators.required]],
                lastName: ['', [Validators.required]],
                email: ['', [Validators.required, Validators.email]]
            }),
            shippingAddress: this.fb.group({
                apartment: ['', [Validators.required]],
                street: ['', [Validators.required]],
                city: ['', [Validators.required]],
                country: ['', [Validators.required]],
                state: [''],
                zip: ['', [Validators.required]]
            }),
            creditCard: this.fb.group({
                type: ['', [Validators.required]],
                number: ['', [Validators.required, Validators.minLength(12)]],
                code: ['', [Validators.required]],
                expMonth: ['', [Validators.required]],
                expYear: ['', [Validators.required]]
            })
        }
    );

    onSubmit() {
        console.log(this.checkoutFormModel);
    }

}
