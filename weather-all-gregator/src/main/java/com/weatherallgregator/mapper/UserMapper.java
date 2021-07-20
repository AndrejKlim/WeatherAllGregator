package com.weatherallgregator.mapper;

import com.weatherallgregator.dto.User;
import com.weatherallgregator.jpa.entity.UserEntity;

import static com.weatherallgregator.mapper.ForecastLocationMapper.mapToForecastLocation;
import static com.weatherallgregator.mapper.ForecastLocationMapper.mapToForecastLocationEntity;

public class UserMapper {

    private UserMapper(){

    }

    public static User mapToUser(final UserEntity entity){
        User user = new User();
        user.setId(entity.getId());
        user.setTimeZone(entity.getTimeZone());
        user.setForecastLocation(mapToForecastLocation(entity.getForecastLocation()));

        return user;
    }

    public static UserEntity mapToUserEntity(final User dto){
        UserEntity userEntity = new UserEntity();
        userEntity.setId(dto.getId());
        userEntity.setTimeZone(dto.getTimeZone());
        userEntity.setForecastLocation(mapToForecastLocationEntity(dto.getForecastLocation()));

        return userEntity;
    }
}
