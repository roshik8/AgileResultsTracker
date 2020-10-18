package com.roshik.services;

import com.roshik.domains.User;
import com.roshik.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public void add(User user){
        userRepository.save(user);
    }
}
