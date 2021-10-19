package com.example.news.controller;

import com.example.news.entity.Category;
import com.example.news.entity.Role;
import com.example.news.entity.User;
import com.example.news.payload.LoginDto;
import com.example.news.payload.RegisterDto;
import com.example.news.payload.UserDto;
import com.example.news.repository.CategoryRepository;
import com.example.news.repository.RoleRepository;
import com.example.news.repository.UserRepository;
import com.example.news.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

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
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

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

    @GetMapping("/addUser")
    public String forAddUser(Model model){
        List<Role> roleList = roleRepository.findAll();
        model.addAttribute("addUser",new UserDto());
        model.addAttribute("roleList", roleList);
        return "addUser";
    }

    @PostMapping("/addUser")
    public String addUser(UserDto userDto){
        System.out.println(userDto);
        boolean b = userRepository.existsByUsername(userDto.getUsername());
        if (b){
            return "error";
        }
        Optional<Role> byId = roleRepository.findById(userDto.getRole().getId());
        Role role = new Role();
        if (byId.isPresent()){
             role = byId.get();
        }
        User user = new User(
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getUsername(),
                passwordEncoder.encode(userDto.getPassword()),
                role
        );
        userRepository.save(user);

        return "redirect:/auth/usersList";
    }

    @GetMapping("/editProfile")
    public String forEdtProfile(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        model.addAttribute("user", user);
        return "editProfile";
    }

    @PostMapping("/editProfile")
    public String editProfile(UserDto userDto, String oldPass, String newPass){
        System.out.println(userDto);
        System.out.println(oldPass);
        System.out.println(newPass );


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        boolean matches = passwordEncoder.matches(oldPass, user.getPassword());
        if (matches){
            user.setUsername(userDto.getUsername());
            user.setFirstName(userDto.getFirstName());
            user.setLastName(userDto.getLastName());
            user.setPassword(passwordEncoder.encode(newPass));

            userRepository.save(user);

            return "redirect:/auth/profile";
        }



        return "error";
    }




}
