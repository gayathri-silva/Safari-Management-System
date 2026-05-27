package com.safaritrip.management.controller;

import com.safaritrip.management.dto.BookingDto;
import com.safaritrip.management.dto.FeedbackDto;
import com.safaritrip.management.dto.ProfileDto;
import com.safaritrip.management.model.*;
import com.safaritrip.management.repository.*;
import com.safaritrip.management.service.BookingService;
import com.safaritrip.management.service.FeedbackService;
import com.safaritrip.management.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/tourist")
public class TouristController {

    @Autowired private UserService userService;
    @Autowired private BookingService bookingService;
    @Autowired private FeedbackService feedbackService;
    @Autowired private NationalParkRepository nationalParkRepository;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private CardDetailRepository cardDetailRepository;
    @Autowired private MessageRepository messageRepository;

    @GetMapping("/profile")
    public String showProfilePage(Model model, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName());
        ProfileDto profileDto = new ProfileDto();
        profileDto.setEmail(currentUser.getEmail());
        profileDto.setPhoneNumber(currentUser.getPhoneNumber());
        model.addAttribute("profile", profileDto);
        return "tourist/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute("profile") @Valid ProfileDto profileDto, BindingResult result, Authentication authentication, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) { return "tourist/profile"; }
        userService.updateUserProfile(authentication.getName(), profileDto);
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        return "redirect:/tourist/profile";
    }

    @GetMapping("/booking")
    public String showBookingPage(Model model) {
        model.addAttribute("parks", nationalParkRepository.findAll());
        model.addAttribute("booking", new BookingDto());
        return "tourist/booking";
    }

    @PostMapping("/booking")
    public String processBooking(@ModelAttribute("booking") @Valid BookingDto bookingDto, BindingResult result, Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            // THIS IS THE FIX: Add the parks list back to the model
            model.addAttribute("parks", nationalParkRepository.findAll());
            return "tourist/booking";
        }
        Booking newBooking = bookingService.createBooking(bookingDto, authentication.getName());
        redirectAttributes.addFlashAttribute("booking", newBooking);
        return "redirect:/tourist/booking-confirmation";
    }

    @GetMapping("/booking-confirmation")
    public String showBookingConfirmation(Model model) {
        if (!model.containsAttribute("booking")) { return "redirect:/tourist/booking"; }
        return "tourist/booking-confirmation";
    }

    @GetMapping("/bookings")
    public String showBookingHistory(Model model, Authentication authentication) {
        model.addAttribute("bookings", bookingService.findBookingsByUsername(authentication.getName()));
        return "tourist/bookings";
    }

    @GetMapping("/bookings/delete/{id}")
    public String deleteBooking(@PathVariable("id") Long bookingId, RedirectAttributes redirectAttributes) {
        bookingRepository.deleteById(bookingId);
        redirectAttributes.addFlashAttribute("successMessage", "Your booking has been successfully canceled.");
        return "redirect:/tourist/bookings";
    }

    @GetMapping("/feedback/new/{bookingId}")
    public String showFeedbackForm(@PathVariable("bookingId") Long bookingId, Model model) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new RuntimeException("Booking not found"));
        model.addAttribute("booking", booking);
        model.addAttribute("feedback", new FeedbackDto());
        return "tourist/add-feedback";
    }

    @PostMapping("/feedback/new")
    public String saveFeedback(@ModelAttribute("feedback") @Valid FeedbackDto feedbackDto,
                               BindingResult result,
                               Authentication authentication,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            Booking booking = bookingRepository.findById(feedbackDto.getBookingId()).orElseThrow(() -> new RuntimeException("Booking not found"));
            model.addAttribute("booking", booking);
            return "tourist/add-feedback";
        }
        feedbackService.saveFeedback(feedbackDto, authentication.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Thank you for your feedback!");
        return "redirect:/tourist/bookings";
    }

    @GetMapping("/booking/pay/{bookingId}")
    public String showPaymentPage(@PathVariable("bookingId") Long bookingId, Model model) {
        model.addAttribute("booking", bookingRepository.findById(bookingId).orElse(null));
        return "tourist/payment";
    }

    @PostMapping("/booking/pay")
    public String processPayment(@RequestParam("bookingId") Long bookingId, RedirectAttributes redirectAttributes) {
        bookingService.updateBookingStatus(bookingId, BookingStatus.CONFIRMED);
        redirectAttributes.addFlashAttribute("successMessage", "Payment successful! Your booking is confirmed.");
        return "redirect:/tourist/bookings";
    }

    @GetMapping("/profile/card")
    public String showCardDetailsPage(Model model, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName());
        model.addAttribute("cardDetail", cardDetailRepository.findByUserId(currentUser.getId()).orElse(new CardDetail()));
        return "tourist/card-details";
    }

    @PostMapping("/profile/card")
    public String saveCardDetails(@ModelAttribute("cardDetail") @Valid CardDetail cardDetail, BindingResult result, Authentication authentication, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) { return "tourist/card-details"; }
        User currentUser = userService.findByUsername(authentication.getName());
        cardDetail.setUser(currentUser);
        cardDetailRepository.save(cardDetail);
        redirectAttributes.addFlashAttribute("successMessage", "Payment details saved successfully!");
        return "redirect:/tourist/profile/card";
    }

    @GetMapping("/profile/card/delete")
    public String deleteCardDetails(Authentication authentication, RedirectAttributes redirectAttributes) {
        User currentUser = userService.findByUsername(authentication.getName());
        cardDetailRepository.deleteByUserId(currentUser.getId());
        redirectAttributes.addFlashAttribute("successMessage", "Payment details have been deleted.");
        return "redirect:/tourist/profile/card";
    }

    @GetMapping("/messages")
    public String showMessages(Model model, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName());
        model.addAttribute("messages", messageRepository.findByRecipientOrderByTimestampDesc(currentUser));
        return "tourist/messages";
    }
}