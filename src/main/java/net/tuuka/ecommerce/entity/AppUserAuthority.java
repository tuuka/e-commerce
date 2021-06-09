package net.tuuka.ecommerce.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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

    @ManyToMany(mappedBy="userAuthorities", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private Set<AppUser> users = new HashSet<>();

    public AppUserAuthority(AppUserRole role) {
        this.role = role;
    }

}
