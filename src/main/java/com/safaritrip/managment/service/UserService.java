package com.safaritrip.management.service;

import com.safaritrip.management.dto.ProfileDto;
import com.safaritrip.management.dto.UserDto;
import com.safaritrip.management.model.User;
import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);
    User findByUsername(String username);
    void updateUserProfile(String username, ProfileDto profileDto);
    List<User> findAllTourists();
    List<User> findAllDrivers();
    List<User> findUnassignedDrivers();
}