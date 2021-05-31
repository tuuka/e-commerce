export class PagedListLinks {

    constructor(
        public first: { href: string, templated: boolean },
        public prev: { href: string, templated: boolean },
        public self: { href: string, templated: boolean },
        public next: { href: string, templated: boolean },
        public last: { href: string, templated: boolean },
        public search: { href: string, templated: boolean }
    ) {
    }

}
