package net.tuuka.ecommerce.model.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "authority")
@Setter
@Getter
@NoArgsConstructor
public class AppUserAuthority {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "role", unique = true)
    private AppUserRole role;

    public AppUserAuthority(AppUserRole role) {
        this.role = role;
    }

}
