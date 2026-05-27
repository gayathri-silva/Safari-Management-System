package com.safaritrip.management.service.impl;

import com.safaritrip.management.dto.ProfileDto;
import com.safaritrip.management.dto.UserDto;
import com.safaritrip.management.model.Role;
import com.safaritrip.management.model.User;
import com.safaritrip.management.model.Vehicle;
import com.safaritrip.management.repository.UserRepository;
import com.safaritrip.management.repository.VehicleRepository;
import com.safaritrip.management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired private UserRepository userRepository;
    @Autowired private VehicleRepository vehicleRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void saveUser(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRoles(Set.of(Role.TOURIST));
        userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public void updateUserProfile(String username, ProfileDto profileDto) {
        User user = findByUsername(username);
        if (user != null) {
            user.setEmail(profileDto.getEmail());
            user.setPhoneNumber(profileDto.getPhoneNumber());
            userRepository.save(user);
        }
    }

    @Override
    public List<User> findAllTourists() {
        return userRepository.findAll().stream().filter(user -> user.getRoles().contains(Role.TOURIST)).collect(Collectors.toList());
    }

    @Override
    public List<User> findAllDrivers() {
        return userRepository.findAll().stream().filter(user -> user.getRoles().contains(Role.DRIVER)).collect(Collectors.toList());
    }

    @Override
    public List<User> findUnassignedDrivers() {
        List<Vehicle> allVehicles = vehicleRepository.findAll();
        Set<Long> assignedDriverIds = allVehicles.stream().filter(v -> v.getDriverAccount() != null).map(v -> v.getDriverAccount().getId()).collect(Collectors.toSet());
        return findAllDrivers().stream().filter(driver -> !assignedDriverIds.contains(driver.getId())).collect(Collectors.toList());
    }
}