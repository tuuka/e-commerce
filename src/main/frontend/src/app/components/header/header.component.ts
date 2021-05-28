import {Component, OnInit} from '@angular/core';

@Component({
    selector: 'app-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.component.sass']
})
export class HeaderComponent implements OnInit {
    menu = Menu;

    constructor() {
    }

    ngOnInit(): void {
    }

}

const Menu = [
    {
        title: 'news',
        subItems: [
            {link:'#',title: 'first'},
            {link:'#',title: 'second'},
            {link:'#',title: 'third'}
        ]
    },
    {
        title: 'designers',
        subItems: [
            {link:'#',title: 'first'},
            {link:'#',title: 'second'}
        ]
    },
    {
        title: 'women',
        subItems: [
            {link:'#',title: 'first'},
            {link:'#',title: 'second'},
            {link:'#',title: 'third'},
            {link:'#',title: 'third'}
        ]
    },
    {
        title: 'men',
        subItems: [
            {link:'#',title: 'first'},
            {link:'#',title: 'second'},
            {link:'#',title: 'third'},
        ]
    },
    {
        title: 'clearance',
        subItems: [
            {link:'#',title: 'first'},
        ]
    }
]
