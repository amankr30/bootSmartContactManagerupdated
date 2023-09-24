package com.smart.controller;

import java.security.Principal;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.services.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotPassController {
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	Random random = new Random(1000);
	
	//email id open handler on clicking forgot
	@GetMapping("/forgot")
	public String openEmailForm() {
		return "forgot_email_form";
		
		}
	
	@PostMapping("/send-otp")
	public String sendOtpHandler(@RequestParam("email") String email,HttpSession session) {
		System.out.println("Inside Send Otp handler");
		
		//Generating OTP
		int otp = random.nextInt(9999);
		System.out.println("Email: "+email);
		System.out.println("OTP: " +otp);
		
		//write code to send OTP to Email
		String message="OTP : "+otp;
		String subject="OTP FROM SmartContactManager";
		String to=email;
		String from="ashishgrd914@gmail.com";
		
		
		boolean flag = this.emailService.sendEmail(message, subject, to,from);
		
		if(flag) {
			session.setAttribute("sessionOtp", otp);
			session.setAttribute("email", email);
			return "verify_otp";
		}
		else {
			session.setAttribute("message", new Message("Wrong Email Entered !!", "alert-danger"));
			return "forgot_email_form";
			
		}
		
		}
	
	//verify OTP handler
	@PostMapping("/verify-otp")
	public String verifyOtpHandler(@RequestParam("otp") int otp,HttpSession session) {
		
		int storedOtp=(int)session.getAttribute("sessionOtp");
		String stordEmail=(String)session.getAttribute("email");
		if(otp==storedOtp) {
			
			//if user of valid email in DB
			User user =this.userRepository.getUserByUserName(stordEmail);
			
			
			if(user==null) {
				//send error messege
				session.setAttribute("message", new Message("User Not Exist With This Email !!", "alert-warning"));
				return "forgot_email_form";
			}
			else {
				//send change password form
			}
			
			//return password change form
			return "password_change_form";
		}
		
		
		else {
			session.setAttribute("message", new Message("Wrong OTP Entered !!", "alert-danger"));
			return "verify_otp";
		}
	
	}
	
	//change password handler
	@PostMapping("/changee-password")
	public String changePasswordHandler(@RequestParam("newwPassword") String newwPassword,HttpSession session) {
		
		String email=(String)session.getAttribute("email");
		User user =this.userRepository.getUserByUserName(email);
		
		//change password
		user.setPassword(this.passwordEncoder.encode(newwPassword));
		this.userRepository.save(user);
		session.setAttribute("message", new Message(" Password Changed !!", "alert-success"));
		
		return "redirect:/signin";
	}

}
