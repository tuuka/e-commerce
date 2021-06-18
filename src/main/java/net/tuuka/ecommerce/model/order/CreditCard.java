package net.tuuka.ecommerce.model.order;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class CreditCard {

    private String type;
    private String number;
    private String code;
    private String expYear;
    private String expMonth;

}
