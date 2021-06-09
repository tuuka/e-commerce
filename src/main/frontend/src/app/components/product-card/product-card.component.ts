import {Component, Input, Renderer2} from '@angular/core';
import {Product} from "../../model/Product";

@Component({
    selector: 'app-product-card',
    templateUrl: './product-card.component.html',
    styleUrls: ['./product-card.component.css']
})
export class ProductCardComponent {


    constructor(private renderer: Renderer2) {
    }

    @Input() product?: Product;

    hover (event: { target: any; }) {
        this.renderer.addClass(event.target, 'mat-elevation-z5')
    }

    unHover (event: { target: any; }) {
        this.renderer.removeClass(event.target, 'mat-elevation-z5')
    }

}
