package com.safaritrip.management.repository;

import com.safaritrip.management.model.Message;
import com.safaritrip.management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByRecipientOrderByTimestampDesc(User recipient);
}