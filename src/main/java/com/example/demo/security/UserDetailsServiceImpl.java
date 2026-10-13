package com.example.demo.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            System.out.println(">>> loadUserByUsername called with: " + username);  // add this
        return userRepository.findByUsername(username)
               .map(user -> {
                System.out.println(">>> User found in DB: " + user.getUsername());  // add this
                return new UserDetailsImpl(user);
            })
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

    }
}
