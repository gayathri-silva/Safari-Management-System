package com.safaritrip.management.repository;

import com.safaritrip.management.model.Guide;
import com.safaritrip.management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface GuideRepository extends JpaRepository<Guide, Long> {
    Optional<Guide> findByUserAccount(User user);
}