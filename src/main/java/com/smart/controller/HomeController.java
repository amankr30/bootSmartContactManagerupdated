package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@GetMapping("/")
	public String home(Model m) {
		System.out.println("Inside Home Handler");
		m.addAttribute("title", "Home- Smart Contact Manager");
		return "home";
	}

	@GetMapping("/about")
	public String aboutHandler(Model m) {
		System.out.println("Inside About Handler");
		m.addAttribute("title", "About- Smart Contact Manager");
		return "about";
	}

	@GetMapping("/signup")
	public String signupHandler(Model m) {
		System.out.println("Inside signUp Handler");
		m.addAttribute("title", "Signup- Smart Contact Manager");
		m.addAttribute("user", new User());
		return "signup";
	}

	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result1,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model m,
			HttpSession session) {

		try {

			if (!agreement) {
				System.out.println("You have not Agreed terms and conditions");
				throw new Exception("You have not Agreed terms and conditions");
			}

			if (result1.hasErrors()) {

				System.out.println(result1.toString());
				m.addAttribute("user", user);
				return "signup";
			}

			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("Default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));

			System.out.println("Agreement" + agreement);
			System.out.println("USER" + user);

			User result = this.userRepository.save(user);
			m.addAttribute("user", new User());

			session.setAttribute("message", new Message("Successfully Registered !!", "alert-success"));

			return "signup";
		} catch (Exception e) {
			e.printStackTrace();
			m.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went Wrong !!" + e.getMessage(), "alert-danger"));

			return "signup";
		}
	}

	@GetMapping("/signin")
	public String customSigninHandler(Model m) {
		System.out.println("Inside Sign-In Handler");
		m.addAttribute("title", "Login - Smart Contact Manager");
		return "login";
	}
}
