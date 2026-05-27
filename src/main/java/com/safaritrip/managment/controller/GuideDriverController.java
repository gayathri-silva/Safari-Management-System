package com.safaritrip.management.controller;

import com.safaritrip.management.config.security.CustomUserDetails;
import com.safaritrip.management.model.Booking;
import com.safaritrip.management.model.BookingStatus;
import com.safaritrip.management.model.Guide;
import com.safaritrip.management.model.User;
import com.safaritrip.management.repository.BookingRepository;
import com.safaritrip.management.repository.GuideRepository;
import com.safaritrip.management.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/guide")
public class GuideDriverController {

    @Autowired private GuideRepository guideRepository;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private BookingService bookingService;

    @GetMapping("/schedule")
    public String showSchedule(Model model, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User currentUser = userDetails.getUser();
        Guide guide = guideRepository.findByUserAccount(currentUser).orElse(null);
        List<Booking> assignedBookings = Collections.emptyList();
        if (guide != null) {
            assignedBookings = bookingRepository.findAllByGuideId(guide.getId());
        }
        model.addAttribute("bookings", assignedBookings);
        model.addAttribute("guideName", guide != null ? guide.getName() : currentUser.getUsername());
        model.addAttribute("statuses", BookingStatus.values());
        return "guide/schedule";
    }

    @PostMapping("/schedule/update-status")
    public String updateTripStatus(@RequestParam("bookingId") Long bookingId, @RequestParam("status") BookingStatus status) {
        bookingService.updateBookingStatus(bookingId, status);
        return "redirect:/guide/schedule";
    }

    @GetMapping("/profile")
    public String showProfilePage(Model model, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Guide guide = guideRepository.findByUserAccount(userDetails.getUser()).orElseThrow(() -> new RuntimeException("Guide profile not found"));
        model.addAttribute("guide", guide);
        return "guide/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam("guideId") Long guideId, @RequestParam("contactNumber") String contactNumber, RedirectAttributes redirectAttributes) {
        Guide guide = guideRepository.findById(guideId).orElseThrow(() -> new RuntimeException("Guide profile not found"));
        guide.setContactNumber(contactNumber);
        guideRepository.save(guide);
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        return "redirect:/guide/profile";
    }
}