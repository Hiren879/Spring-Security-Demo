package com.demo.services;

import com.demo.adapters.SecurityUser;
import com.demo.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class H2UserDetailsService implements UserDetailsService {

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = usersRepository.findUserByName(username);
        return user.map(SecurityUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("User name not found for user " + username));
    }
}
