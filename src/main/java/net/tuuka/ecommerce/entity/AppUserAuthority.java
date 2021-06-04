package net.tuuka.ecommerce.entity;

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
    @SequenceGenerator(name = "authoritySequence",
            sequenceName = "authority_sequence",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "authoritySequence")
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private AppUserRole role;

    public AppUserAuthority(AppUserRole role) {
        this.role = role;
    }

}
