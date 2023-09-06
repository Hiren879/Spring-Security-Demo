package com.demo;

import com.demo.entities.Authority;
import com.demo.entities.Users;
import com.demo.repositories.AuthorityRepository;
import com.demo.repositories.UsersRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class SecurityDemo {

    public static void main(String[] args) {
//        SpringApplication.run(SecurityDemo.class, args);
        ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(SecurityDemo.class, args);
        UsersRepository usersRepository = configurableApplicationContext.getBean(UsersRepository.class);
        AuthorityRepository authorityRepository = configurableApplicationContext.getBean(AuthorityRepository.class);
        BCryptPasswordEncoder passwordEncoder = (BCryptPasswordEncoder) configurableApplicationContext.getBean(
                PasswordEncoder.class);

        // Authority 1 - read
        Authority adminAuthority = new Authority();
        adminAuthority.setAuthorityName("ROLE_ADMIN");

        // Authority 2 : write
        Authority userAuthority = new Authority();
        userAuthority.setAuthorityName("ROLE_USER");

        List<Authority> allAuthorityList = Arrays.asList(adminAuthority, userAuthority);
        //  save all authorities to DB
        authorityRepository.saveAll(allAuthorityList);

        // User 1
        Users user1 = new Users();
        user1.setName("hiren");
        user1.setPassword(passwordEncoder.encode("hiren"));
        user1.setAuthorities(allAuthorityList);

        // User 2
        Users user2 = new Users();
        user2.setName("dhyey");
        user2.setPassword(passwordEncoder.encode("dhyey"));
        user2.setAuthorities(Arrays.asList(userAuthority));

        List<Users> allUsers = Arrays.asList(user1, user2);
        // save all users to DB
        usersRepository.saveAll(allUsers);
    }
}