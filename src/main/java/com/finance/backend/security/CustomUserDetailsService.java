package com.finance.backend.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.finance.backend.entity.*;
import com.finance.backend.entity.User;
import com.finance.backend.repository.*;

import java.util.*;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;

    public CustomUserDetailsService(UserRepository userRepo, RoleRepository roleRepo) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {

        User user = userRepo.findByUsername(username).orElseThrow();

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        for (String role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role));
            System.out.println("Authorities: " + authorities);
            Role r = roleRepo.findByName(role).orElse(null);
            if (r != null && r.getPermissions() != null) {
                r.getPermissions()
                        .forEach(p -> authorities.add(new SimpleGrantedAuthority(p)));
            }
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}