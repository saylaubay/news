package com.example.news.controller;

import com.example.news.entity.Category;
import com.example.news.entity.User;
import com.example.news.payload.LoginDto;
import com.example.news.payload.RegisterDto;
import com.example.news.repository.CategoryRepository;
import com.example.news.repository.UserRepository;
import com.example.news.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService authService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    UserRepository userRepository;

    @GetMapping("/register")
    public String auth(Model model){
        model.addAttribute("registerDto", new RegisterDto());

        return "register";
    }

    @PostMapping("/register")
    public String register(RegisterDto registerDto){
        boolean b = authService.register(registerDto);
        if (b)
            return "redirect:/login";
        return "error";
    }

    @GetMapping("/login")
    public String forLogin(Model model){
        model.addAttribute("loginDto", new LoginDto());

        return "login";
    }

    @GetMapping("/logout")
    public String logout(){
        SecurityContextHolder.clearContext();
        return "redirect:/";
    }

    @PostMapping("/login")
    public String login(LoginDto loginDto){

        boolean b = authService.login(loginDto);
        if (b){
            return "redirect:/";
        }
        return "error";
    }

    @GetMapping("/profile")
    public String forProfile(Model model){
//        model.addAttribute("")
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        model.addAttribute("user", user);

        List<Category> all = categoryRepository.findAll();
        model.addAttribute("categories", all);

        return "profile";
    }

    @GetMapping("/usersList")
    public String userList(Model model){
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "userList";
    }

    @GetMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable Integer id){
        userRepository.deleteById(id);
        return "redirect:/auth/usersList";
    }




}
