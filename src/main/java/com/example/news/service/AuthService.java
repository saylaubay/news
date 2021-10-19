package com.example.news.service;

import com.example.news.entity.Role;
import com.example.news.entity.User;
import com.example.news.payload.LoginDto;
import com.example.news.payload.RegisterDto;
import com.example.news.repository.RoleRepository;
import com.example.news.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    AuthenticationManager authenticationManager;

    public boolean register(RegisterDto registerDto){
        boolean b = userRepository.existsByUsername(registerDto.getUsername());
        if (b){
            return false;
        }

        Role admin = roleRepository.findByName("ADMIN");
        User user = new User(
            registerDto.getFirstName(),
                registerDto.getLastName(),
                registerDto.getUsername(),
                passwordEncoder.encode(registerDto.getPassword()),
//                roleRepository.findByName("ADMIN")
                admin
        );

        userRepository.save(user);

        return true;
    }

    public boolean login(LoginDto loginDto){
        try {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDto.getUsername(),
                    loginDto.getPassword()
            ));
            SecurityContextHolder.getContext().setAuthentication(authenticate);
            return true;
        }catch (Exception e){
            return false;
        }
    }


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(s);
        return user;
    }
}
