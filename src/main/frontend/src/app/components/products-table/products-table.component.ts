import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {CartItem} from "../../model/CartItem";
import {MatTable} from "@angular/material/table";

@Component({
    selector: 'app-products-table',
    templateUrl: './products-table.component.html',
    styleUrls: ['./products-table.component.css']
})
export class ProductsTableComponent implements OnInit {

    @Input() items: CartItem[] = [];
    @Input() totalPrice: number = 0;
    @Input() totalQuantity: number = 0;
    @Input() displayedColumns: string[] = ['image', 'name', 'price', 'quantity', 'total'];
    @Output() decrement = new EventEmitter<CartItem>();
    @Output() increment = new EventEmitter<CartItem>();

    @ViewChild(MatTable) table?: MatTable<CartItem>;

    constructor() {
    }

    ngOnInit(): void {
    }

    decrementQuantity(cartItem: CartItem) {
        this.decrement.emit(cartItem);
    }

    incrementQuantity(cartItem: CartItem) {
        this.increment.emit(cartItem);
    }

    public renderRows() {
        this.table?.renderRows();
    }
}
