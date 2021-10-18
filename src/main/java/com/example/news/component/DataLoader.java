package com.example.news.component;

import com.example.news.entity.Permission;
import com.example.news.entity.Role;
import com.example.news.entity.User;
import com.example.news.repository.RoleRepository;
import com.example.news.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Value("${spring.datasource.initialization-mode}")
    private String initialMode;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (initialMode.equals("always")){
            Set<Permission> adminPer = new HashSet<>();
            adminPer.add(Permission.DELETE);
            adminPer.add(Permission.EDIT);
            adminPer.add(Permission.ADD);
            Role admin = roleRepository.save(new Role(
                    "ADMIN",
//                    Collections.singleton(Permission.DELETE)
                    adminPer
            ));
            Set<Permission> userPer = new HashSet<>();
            userPer.add(Permission.EDIT);
            userPer.add(Permission.ADD);
            Role user = roleRepository.save(new Role(
                    "USER",
//                    Collections.singleton(Permission.EDIT)
                    userPer
            ));

            userRepository.save(new User(
                    "Saylaubay",
                    "Bekmurzaev",
                    "sake",
                    passwordEncoder.encode("123"),
                    admin
            ));
            userRepository.save(new User(
                    "Ramazan",
                    "Darmiyanov",
                    "roma",
                    passwordEncoder.encode("456"),
                    user
            ));

        }
    }



}
