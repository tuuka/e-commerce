export class UserDetails {

    constructor(
        public id: number,
        public firstName: string,
        public lastName: string,
        public email: string,
        public enabled: boolean,
        public locked: boolean,
        public roles: string[],
    ) {
    }

}
