import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Category} from "../../model/Category";

@Component({
    selector: 'app-category-list',
    templateUrl: './category-list.component.html',
    styleUrls: ['./category-list.component.css']
})
export class CategoryListComponent {

    selectedId?: number;

    @Input() categories?: Category[];
    @Output() categoryClicked = new EventEmitter<number>();

    onClick($event:Category) {
        this.selectedId = $event.id;
        this.categoryClicked.emit(this.selectedId);
    }

    resetCategory() {
        this.selectedId = 0;
        this.categoryClicked.emit(this.selectedId);
    }
}
