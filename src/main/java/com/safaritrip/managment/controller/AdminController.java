package com.safaritrip.management.controller;

import com.safaritrip.management.dto.GuideCreationDto;
import com.safaritrip.management.model.*;
import com.safaritrip.management.repository.BookingRepository;
import com.safaritrip.management.repository.GuideRepository;
import com.safaritrip.management.repository.UserRepository;
import com.safaritrip.management.repository.VehicleRepository;
import com.safaritrip.management.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private BookingService bookingService;
    @Autowired private FeedbackService feedbackService;
    @Autowired private GuideRepository guideRepository;
    @Autowired private VehicleRepository vehicleRepository;
    @Autowired private UserService userService;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private NotificationService notificationService;
    @Autowired private MessageService messageService;
    @Autowired private PasswordEncoder passwordEncoder;

    @GetMapping("/dashboard")
    public String showAdminDashboard(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        return "admin/dashboard";
    }

    @GetMapping("/bookings")
    public String listAllBookings(Model model) {
        model.addAttribute("bookings", bookingService.findAllBookings());
        return "admin/bookings";
    }

    @PostMapping("/bookings/notify-payment")
    public String notifyForPayment(@RequestParam("bookingId") Long bookingId, RedirectAttributes redirectAttributes) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new RuntimeException("Booking not found"));
        User tourist = booking.getTourist();
        if (tourist != null) {
            notificationService.createNotification(tourist, "Admin Reminder: Please complete the payment for your booking (#" + booking.getId() + ") to confirm your trip.");
            redirectAttributes.addFlashAttribute("successMessage", "Payment reminder sent to " + tourist.getUsername());
        }
        return "redirect:/admin/bookings";
    }

    @GetMapping("/feedback")
    public String listAllFeedback(Model model) {
        model.addAttribute("feedbackList", feedbackService.findAllFeedback());
        return "admin/feedback";
    }

    @PostMapping("/feedback/reply")
    public String saveAdminReply(@RequestParam("feedbackId") Long feedbackId, @RequestParam("reply") String reply) {
        feedbackService.saveAdminReply(feedbackId, reply);
        return "redirect:/admin/feedback";
    }

    @GetMapping("/tourists")
    public String listTourists(Model model) {
        model.addAttribute("tourists", userService.findAllTourists());
        return "admin/tourists";
    }

    @GetMapping("/tourists/edit/{id}")
    public String showEditTouristForm(@PathVariable("id") Long id, Model model) {
        User tourist = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid tourist Id:" + id));
        model.addAttribute("tourist", tourist);
        return "admin/tourist-form";
    }

    @PostMapping("/tourists/update")
    public String updateTourist(@ModelAttribute("tourist") User touristForm) {
        User existingUser = userRepository.findById(touristForm.getId()).orElseThrow(() -> new IllegalArgumentException("Invalid tourist Id:" + touristForm.getId()));
        existingUser.setUsername(touristForm.getUsername());
        existingUser.setEmail(touristForm.getEmail());
        existingUser.setPhoneNumber(touristForm.getPhoneNumber());
        userRepository.save(existingUser);
        return "redirect:/admin/tourists";
    }

    @GetMapping("/tourists/delete/{id}")
    public String deleteTourist(@PathVariable("id") Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/tourists";
    }

    @GetMapping("/guides")
    public String listGuides(Model model) {
        model.addAttribute("guides", guideRepository.findAll());
        return "admin/guides";
    }

    @GetMapping("/guides/new")
    public String showAddGuideForm(Model model) {
        model.addAttribute("guideDto", new GuideCreationDto());
        return "admin/guide-creation-form";
    }

    @PostMapping("/guides/create")
    public String createGuide(@ModelAttribute("guideDto") @Valid GuideCreationDto guideDto, BindingResult result, Model model) {
        if (userRepository.findByUsername(guideDto.getUsername()).isPresent()) {
            result.rejectValue("username", null, "Username already exists.");
        }
        if (userRepository.findAll().stream().anyMatch(user -> user.getEmail().equals(guideDto.getEmail()))) {
            result.rejectValue("email", null, "Email address is already in use.");
        }
        if (result.hasErrors()) {
            model.addAttribute("guideDto", guideDto);
            return "admin/guide-creation-form";
        }
        User guideUser = new User();
        guideUser.setUsername(guideDto.getUsername());
        guideUser.setEmail(guideDto.getEmail());
        guideUser.setPassword(passwordEncoder.encode(guideDto.getPassword()));
        guideUser.setPhoneNumber(guideDto.getContactNumber());
        guideUser.setRoles(Set.of(Role.GUIDE));
        Guide guide = new Guide();
        guide.setName(guideDto.getName());
        guide.setContactNumber(guideDto.getContactNumber());
        guide.setLicenseNumber(guideDto.getLicenseNumber());
        guide.setUserAccount(guideUser);
        guideRepository.save(guide);
        return "redirect:/admin/guides";
    }

    @GetMapping("/guides/edit/{id}")
    public String showEditGuideForm(@PathVariable("id") Long id, Model model) {
        Guide guide = guideRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid guide Id:" + id));
        model.addAttribute("guide", guide);
        return "admin/guide-edit-form";
    }

    @PostMapping("/guides/update")
    public String updateGuide(@ModelAttribute("guide") Guide guide) {
        Guide existingGuide = guideRepository.findById(guide.getId()).orElseThrow(() -> new RuntimeException("Guide not found"));
        existingGuide.setName(guide.getName());
        existingGuide.setContactNumber(guide.getContactNumber());
        existingGuide.setLicenseNumber(guide.getLicenseNumber());
        guideRepository.save(existingGuide);
        return "redirect:/admin/guides";
    }

    @GetMapping("/guides/delete/{id}")
    public String deleteGuide(@PathVariable("id") Long id) {
        guideRepository.deleteById(id);
        return "redirect:/admin/guides";
    }

    @GetMapping("/vehicles")
    public String listVehicles(Model model) {
        model.addAttribute("vehicles", vehicleRepository.findAll());
        return "admin/vehicles";
    }

    @GetMapping("/vehicles/new")
    public String showAddVehicleForm(Model model) {
        model.addAttribute("vehicle", new Vehicle());
        model.addAttribute("drivers", userService.findUnassignedDrivers());
        return "admin/vehicle-form";
    }

    @PostMapping("/vehicles/save")
    public String saveVehicle(@ModelAttribute("vehicle") Vehicle vehicle, @RequestParam(value = "driverId", required = false) Long driverId) {
        if (driverId != null) {
            User driverUser = userRepository.findById(driverId).orElse(null);
            vehicle.setDriverAccount(driverUser);
        } else {
            vehicle.setDriverAccount(null);
        }
        vehicleRepository.save(vehicle);
        return "redirect:/admin/vehicles";
    }

    @GetMapping("/vehicles/edit/{id}")
    public String showEditVehicleForm(@PathVariable("id") Long id, Model model) {
        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid vehicle Id:" + id));
        List<User> availableDrivers = userService.findUnassignedDrivers();
        if (vehicle.getDriverAccount() != null) {
            availableDrivers.add(vehicle.getDriverAccount());
        }
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("drivers", availableDrivers);
        return "admin/vehicle-form";
    }

    @GetMapping("/vehicles/delete/{id}")
    public String deleteVehicle(@PathVariable("id") Long id) {
        vehicleRepository.deleteById(id);
        return "redirect:/admin/vehicles";
    }

    @GetMapping("/bookings/assign/{id}")
    public String showAssignmentForm(@PathVariable("id") Long bookingId, Model model) {
        Booking booking = bookingService.findAllBookings().stream().filter(b -> b.getId().equals(bookingId)).findFirst().orElseThrow(() -> new RuntimeException("Booking not found"));
        model.addAttribute("booking", booking);
        model.addAttribute("guides", guideRepository.findAll());
        model.addAttribute("vehicles", vehicleRepository.findAll());
        return "admin/assign-form";
    }

    @PostMapping("/bookings/assign")
    public String saveAssignment(@RequestParam("bookingId") Long bookingId, @RequestParam(value="guideId", required=false) Long guideId, @RequestParam(value="vehicleId", required=false) Long vehicleId) {
        bookingService.assignGuideAndVehicle(bookingId, guideId, vehicleId);
        return "redirect:/admin/bookings";
    }

    @GetMapping("/reports")
    public String showReportsPage(Model model) {
        model.addAttribute("totalTourists", userService.findAllTourists().size());
        model.addAttribute("totalBookings", bookingRepository.count());
        model.addAttribute("pendingBookings", bookingRepository.countByStatus(BookingStatus.PENDING));
        model.addAttribute("confirmedBookings", bookingRepository.countByStatus(BookingStatus.CONFIRMED));
        model.addAttribute("completedBookings", bookingRepository.countByStatus(BookingStatus.COMPLETED));
        model.addAttribute("totalGuides", guideRepository.count());
        model.addAttribute("totalVehicles", vehicleRepository.count());
        return "admin/reports";
    }

    @GetMapping("/message/new/{touristId}")
    public String showMessageForm(@PathVariable("touristId") Long touristId, Model model) {
        User tourist = userRepository.findById(touristId).orElseThrow(() -> new IllegalArgumentException("Invalid tourist Id:" + touristId));
        model.addAttribute("tourist", tourist);
        return "admin/message-form";
    }

    @PostMapping("/message/send")
    public String sendMessage(@RequestParam("recipientId") Long recipientId, @RequestParam("content") String content, Authentication authentication, RedirectAttributes redirectAttributes) {
        User recipient = userRepository.findById(recipientId).orElseThrow(() -> new IllegalArgumentException("Invalid recipient Id:" + recipientId));
        User sender = userService.findByUsername(authentication.getName());
        messageService.sendMessage(sender, recipient, content);
        redirectAttributes.addFlashAttribute("successMessage", "Message sent successfully to " + recipient.getUsername());
        return "redirect:/admin/tourists";
    }
}