import {Component, EventEmitter, Input, Output, ViewEncapsulation} from '@angular/core';

@Component({
    selector: 'app-select',
    templateUrl: './select.component.html',
    styleUrls: ['./select.component.css'],
    encapsulation: ViewEncapsulation.None,
})
export class SelectComponent {

    @Input() selectOptions: SelectOption[] = [];
    @Input() selected?: string;
    @Input() label?:string;
    @Input() width:string = '';
    @Output() valueChangedEvent = new EventEmitter<string>();

    valueChanged(value: string) {
        this.selected = value;
        this.valueChangedEvent.emit(this.selected);
    }
}

export interface SelectOption {
    value: string;
    viewValue: string
}