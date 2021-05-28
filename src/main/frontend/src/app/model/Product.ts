export class Product {

  constructor(
    public id: number,
    public sku: string,
    public name: string,
    public description: string,
    public unitPrice: number,
    public imageUrl: string,
    public active: boolean,
    public unitsInStock: number,
    public created: Date,
    public lastUpdated: Date,
    public _links: {
      self: { href: string },
      category: { href: string },
      products: { href: string }
    }
  ) {}

}
