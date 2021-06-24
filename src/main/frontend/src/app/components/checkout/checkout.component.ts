import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormControl, ValidationErrors, Validators} from "@angular/forms";
import {SelectOption} from "../select/select.component";
import {CartService} from "../../services/cart.service";
import {CheckoutService} from "../../services/checkout.service";
import {AuthService, UserInfo} from "../../services/auth.service";
import {Purchase} from "../../model/Purchase";
import {Router} from "@angular/router";

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

    userInfo: UserInfo = new UserInfo();

    constructor(private fb: FormBuilder,
                private cartService: CartService,
                private checkoutService: CheckoutService,
                private authService: AuthService,
                private router: Router) {
    }

    ngOnInit(): void {
        this.checkoutService.getCreditCardMonths().subscribe(data => {
            this.creditCardMonths = data;
        });
        this.checkoutService.getCreditCardYears().subscribe(data => {
            this.creditCardYears = data;
        });
        this.cartService.totalQuantity.subscribe(q => {
            this.totalQuantity = q;
        });
        this.cartService.totalPrice.subscribe(p => {
            this.totalPrice = p;
        })
        this.authService.userInfo.subscribe(info => {
            this.userInfo = info;
        })
        this.checkoutFormModel.reset();
        this.setUpFormModel();
        // this.cartService.computeCartTotals();

    }

    notOnlyWhitespace(control: FormControl): ValidationErrors | null {
        if ((control.value != null) && (control.value.trim().length === 0)) {
            return {'notOnlyWhitespace': true};
        } else {
            return null;
        }
    }

    checkoutFormModel = this.fb.group({
            customer: this.fb.group({
                // firstName: ['', [Validators.required, this.notOnlyWhitespace]],
                // lastName: ['', [Validators.required, this.notOnlyWhitespace]],
                email: ['', [Validators.required,
                    Validators.pattern('^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$')]]
            }),
            shippingAddress: this.fb.group({
                apartment: ['', [Validators.required]],
                street: ['', [Validators.required]],
                city: ['', [Validators.required]],
                country: ['', [Validators.required]],
                state: [''],
                zip: ['', [Validators.required]]
            }),
            // creditCard: this.fb.group({
            //     type: ['', [Validators.required]],
            //     number: ['', [Validators.required,
            //         Validators.pattern('[0-9]{16}')]],
            //     code: ['', [Validators.required,
            //         Validators.pattern('[0-9]{3}')]],
            //     expMonth: ['', [Validators.required]],
            //     expYear: ['', [Validators.required]]
            // })
        }
    );

    private setUpFormModel() {
        this.checkoutFormModel.patchValue({
            customer: {
                firstName: this.userInfo.firstName,
                lastName: this.userInfo.lastName,
                email: this.userInfo.email
            }
        })
    }

    onSubmit() {
        if (this.checkoutFormModel.invalid) {
            this.checkoutFormModel.markAllAsTouched();
        }
        // console.log(this.checkoutFormModel);
        let purchase = new Purchase(
            this.checkoutFormModel.get('customer.email')?.value,
            // this.checkoutFormModel.get('creditCard')?.value,
            this.checkoutFormModel.get('shippingAddress')?.value,
            this.cartService.getCartFromStorage()
        )
        // console.log(order);
        this.checkoutService.sendOrder(purchase).subscribe(
            (result: any) => {
                console.log(result);
                this.cartService.removeFromStorage()
                this.checkoutFormModel.reset();
                this.router.navigateByUrl('/');
            },
            err => {
                console.log(err);
            }
        );
    }

    // cardTypeChange(event: any) {
    //     this.checkoutFormModel.get('creditCard')?.setValue(event.target.value, {onlySelf: true});
    //     console.log(this.checkoutFormModel.get('creditCard'));
    // }
}

