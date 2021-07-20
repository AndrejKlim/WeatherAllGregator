package com.weatherallgregator.service;

import com.weatherallgregator.jpa.entity.UserEntity;
import com.weatherallgregator.jpa.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserService {

    private final UserRepo repo;

    public UserEntity findUserById(final Long id){
        return repo.findById(id).orElse(null);
    }
}
