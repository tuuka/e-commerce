package net.tuuka.ecommerce.dao;

import net.tuuka.ecommerce.entity.AppUserAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserAuthorityRepository extends JpaRepository<AppUserAuthority, AppUserAuthority> {
}
