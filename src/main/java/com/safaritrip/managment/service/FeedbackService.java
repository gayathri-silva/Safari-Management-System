package com.safaritrip.management.service;

import com.safaritrip.management.dto.FeedbackDto;
import com.safaritrip.management.model.Feedback;
import java.util.List;

public interface FeedbackService {
    void saveFeedback(FeedbackDto feedbackDto, String username);
    List<Feedback> findAllFeedback();
    void saveAdminReply(Long feedbackId, String reply);
}