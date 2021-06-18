package net.tuuka.ecommerce.model.order;

import lombok.*;

import javax.persistence.Embeddable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Embeddable
public class Address {

    private String apartment;
    private String street;
    private String city;
    private String country;
    private String state;
    private String zip;

}
