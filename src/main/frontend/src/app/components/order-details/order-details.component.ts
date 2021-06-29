import {Component, OnInit, ViewChild} from '@angular/core';
import {OrdersService} from "../../services/orders.service";
import {ActivatedRoute, Router} from "@angular/router";
import {FormBuilder, FormControl, Validators} from "@angular/forms";
import {Order} from "../../model/Order";
import {Address} from "../../model/Purchase";
import {UserDetails} from "../../model/user-details";
import {CartItem} from "../../model/CartItem";
import {ProductsTableComponent} from "../products-table/products-table.component";
import {OrderItem} from "../../model/OrderItem";
import {OrderRequest} from "../../model/OrderRequest";

@Component({
    selector: 'app-order-details',
    templateUrl: './order-details.component.html',
    styleUrls: ['./order-details.component.css']
})
export class OrderDetailsComponent implements OnInit {

    orderInfo?: Order;
    orderAddress?: Address;
    userInfo?: UserDetails;
    orderItems: CartItem[] = [];
    displayedColumns = ['image', 'name', 'price', 'quantity', 'total'];
    changeQuantityDisabled: boolean = false;
    orderStatusList: string[] = [];

    @ViewChild(ProductsTableComponent) table?: ProductsTableComponent;

    constructor(private orderService: OrdersService,
                private route: ActivatedRoute,
                private fb: FormBuilder,
                private router: Router) {
    }

    ngOnInit(): void {
        // @ts-ignore
        let id: number = +this.route.snapshot.paramMap.get('id');
        this.orderService.getOrderStatusList().subscribe(data => {
            this.orderStatusList = data
            this.getOrderById(id);
        })
    }

    private getOrderById(id: number) {
        this.orderService.getOrderDetailsById(id).subscribe(
            data => {
                this.orderItems = data.orderProducts.map(op => {
                    let item: CartItem = new CartItem(op.product);
                    item.quantity = op.quantity;
                    return item;
                });
                this.userInfo = data.user;
                this.orderAddress = data.shippingAddress;
                this.orderInfo = data.order;
                let status: string = data.order.status;
                this.changeQuantityDisabled = !!status ? ['PAID', 'SHIPPED'].includes(status) : false;
                this.setFormModel();
            }
        )
    }

    orderFormModel = this.fb.group({
            email: new FormControl({value: '', disabled: true}),
            // orderId: new FormControl({value: '', disabled: true}),
            orderStatus: new FormControl({value: ''}),
            shippingAddress: this.fb.group({
                apartment: ['', [Validators.required]],
                street: ['', [Validators.required]],
                city: ['', [Validators.required]],
                country: ['', [Validators.required]],
                state: [''],
                zip: ['', [Validators.required]]
            }),
        }
    );

    private setFormModel() {
        this.orderFormModel.patchValue({
            email: this.userInfo?.email,
            orderStatus: this.orderInfo?.status,
            // orderId: this.orderInfo?.id,
            shippingAddress: {
                apartment: this.orderAddress?.apartment,
                street: this.orderAddress?.street,
                city: this.orderAddress?.city,
                country: this.orderAddress?.country,
                state: this.orderAddress?.state,
                zip: this.orderAddress?.zip
            },
        });
    }

    compareStatuses(status1: any, status2: any) {
        return status1 && status2 && status1 === status2;
    }

    decrementQuantity(item: CartItem) {
        let existingItem = this.findExistingItem(item);
        // @ts-ignore
        if (existingItem && this.orderInfo.totalQuantity > 1) {
            existingItem.quantity -= 1;
            if (existingItem.quantity < 1) this.removeItem(item);
            this.computeOrderTotalsAndRefresh()
        }

    }

    incrementQuantity(item: CartItem) {
        let existingItem = this.findExistingItem(item);
        if (existingItem) {
            existingItem.quantity -= 1;
            this.computeOrderTotalsAndRefresh()
        }
    }

    private findExistingItem(item: CartItem) {
        return this.orderItems.find(orderItem => item.id === orderItem.id);
    }

    private removeItem(item: CartItem) {
        let index = this.orderItems.findIndex(orderItem => item.id === orderItem.id);
        if (index > -1) this.orderItems.splice(index, 1);
    }

    private computeOrderTotalsAndRefresh() {
        let totalQuantity: number = 0;
        let totalPrice: number = 0;
        this.orderItems.forEach((item: CartItem) => {
            totalQuantity += item.quantity;
            totalPrice += item.quantity * item.unitPrice;
        })
        // @ts-ignore
        this.orderInfo.totalQuantity = totalQuantity;
        // @ts-ignore
        this.orderInfo.totalPrice = totalPrice;
        this.table?.renderRows()
    }

    onSubmit() {

        let orderToSave = new OrderRequest(
            this.orderInfo?.id,
            this.orderFormModel.get('orderStatus')?.value,
            this.orderFormModel.get('shippingAddress')?.value,
            this.orderItems.map(item => new OrderItem(item.id, item.quantity))
        )
        this.orderService.putOrder(orderToSave).subscribe(
            (result: any) => {
                console.log(result);
                this.router.navigateByUrl('/');
            },
            err => {
                console.log(err);
            }
        );
    }
}



