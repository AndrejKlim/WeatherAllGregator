package com.weatherallgregator.jpa.repo;

import com.weatherallgregator.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<UserEntity, Long> {
}
