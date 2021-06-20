package net.tuuka.ecommerce.bootstrap;

import lombok.RequiredArgsConstructor;
import net.tuuka.ecommerce.dao.AppUserAuthorityRepository;
import net.tuuka.ecommerce.dao.AppUserRepository;
import net.tuuka.ecommerce.dao.ProductCategoryRepository;
import net.tuuka.ecommerce.dao.ProductRepository;
import net.tuuka.ecommerce.model.Product;
import net.tuuka.ecommerce.model.user.AppUser;
import net.tuuka.ecommerce.model.user.AppUserAuthority;
import net.tuuka.ecommerce.model.user.AppUserRole;
import net.tuuka.ecommerce.util.FakeProductGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InitDB implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;
    private final AppUserRepository userRepository;

    private final AppUserAuthorityRepository authorityRepository;

    @Value("${app.fill_products}")
    private Boolean FILL_PRODUCTS;

    @Value("${app.security.root.email}")
    private String email;

    @Value("${app.security.root.password}")
    private String password;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        if (FILL_PRODUCTS) {
            productRepository.deleteAll();
            productRepository.flush();
            categoryRepository.deleteAll();
            categoryRepository.flush();
            List<Product> products = FakeProductGenerator.getNewFakeProductList(5, 3);
            categoryRepository.saveAll(products.stream().map(Product::getCategory).collect(Collectors.toSet()));
            productRepository.saveAll(products);
        }

        AppUser rootUser = userRepository.findByEmail(email).orElse(new AppUser());
        if (rootUser.getEmail() == null) {
//            create new admin account
            rootUser.setFirstName("admin");
            rootUser.setLastName("admin");
            rootUser.setEmail(email);
            rootUser.setEnabled(true);
            Set<AppUserAuthority> allowedAuthorities = Arrays.stream(AppUserRole.values())
                    .map(AppUserAuthority::new).collect(Collectors.toSet());
            allowedAuthorities = new HashSet<>(authorityRepository.saveAll(allowedAuthorities));
            rootUser.setAuthorities(allowedAuthorities);
        }
        rootUser.setPassword(new BCryptPasswordEncoder().encode(password));
        userRepository.save(rootUser);

    }
}
