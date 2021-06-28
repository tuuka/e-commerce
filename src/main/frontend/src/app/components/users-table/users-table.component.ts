import {Component, OnInit} from '@angular/core';
import {UserDetails} from "../../model/user-details";
import {AuthService} from "../../services/auth.service";

@Component({
    selector: 'app-users-table',
    templateUrl: './users-table.component.html',
    styleUrls: ['./users-table.component.css']
})
export class UsersTableComponent implements OnInit {

    users: UserDetails[] = [];
    displayedColumns: string[] = ['id', 'email', 'name', 'enabled', 'locked', 'roles', 'action'];

    constructor(private authService: AuthService) {
    }

    ngOnInit(): void {

        this.authService.getAccounts().subscribe(
            data => {
                this.users = UsersTableComponent.convertRoles(data);
            },
            err => console.log(err)
        )
    }

    private static convertRoles(users: UserDetails[]) {
        let result: UserDetails[] = [];
        for (let user of users) {
            user.roles = user.roles ? user.roles.reduce((acc: string[], auth) => {
                acc.push(auth.toLowerCase().replace("role_", ""));
                return acc;
            }, []) : [];
            result.push(user);
        }
        return result;
    }

    remove(user: UserDetails) {
        console.log('remove')
    }

    edit(user: UserDetails) {
        console.log('edit')

    }
}
