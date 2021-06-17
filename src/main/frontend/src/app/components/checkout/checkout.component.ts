import {Component, OnInit} from '@angular/core';
import {FormBuilder, Validators} from "@angular/forms";
import {Router} from "@angular/router";
import {SelectOption} from "../select/select.component";
import {CartService} from "../../services/cart.service";
import {CheckoutService} from "../../services/checkout.service";

@Component({
    selector: 'app-checkout',
    templateUrl: './checkout.component.html',
    styleUrls: ['./checkout.component.css']
})
export class CheckoutComponent implements OnInit {

    cardTypes: SelectOption[] = [
        {value: 'visa', viewValue: 'Visa'},
        {value: 'mastercard', viewValue: 'Mastercard'},
        {value: 'mir', viewValue: 'Mir'}
    ];

    creditCardMonths: number[] = [];
    creditCardYears: number[] = [];

    totalPrice: number = 0;
    totalQuantity: number = 0;

    constructor(private fb: FormBuilder,
                private cartService: CartService,
                private checkoutService: CheckoutService) {
    }

    ngOnInit(): void {
        this.checkoutService.getCreditCardMonths().subscribe(data => {
            this.creditCardMonths = data;
        });
        this.checkoutService.getCreditCardYears().subscribe(data => {
            this.creditCardYears = data;
        });
        this.checkoutFormModel.reset();
        this.cartService.totalQuantity.subscribe(q=>{
            this.totalQuantity = q;
        });
        this.cartService.totalPrice.subscribe(p => {
            this.totalPrice = p;
        })
        this.cartService.computeCartTotals();

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
                number: ['', [Validators.required, Validators.minLength(16)]],
                code: ['', [Validators.required]],
                expMonth: ['', [Validators.required]],
                expYear: ['', [Validators.required]]
            })
        }
    );

    onSubmit() {
        console.log(this.checkoutFormModel);
        // this.checkoutFormModel.reset();
    }

    cardTypeChange(event:any) {
        this.checkoutFormModel.get('creditCard')?.setValue(event.target.value, {onlySelf: true});
        console.log(this.checkoutFormModel.get('creditCard'));
    }
}
