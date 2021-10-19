package com.example.news.controller;

import com.example.news.entity.Category;
import com.example.news.entity.User;
import com.example.news.entity.Xabar;
import com.example.news.payload.CategoryDto;
import com.example.news.payload.XabarDto;
import com.example.news.repository.CategoryRepository;
import com.example.news.repository.XabarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    XabarRepository xabarRepository;
    @Autowired
    CategoryRepository categoryRepository;

    private static final String uploadDirectory="src/main/resources/static/fayllar";

    @GetMapping
    public String home(Model model){
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);

        return "categoryList";
    }

    @GetMapping("/addNewCategory")
    public String forAddCategory(Model model){
        model.addAttribute("categoryDto", new CategoryDto());
        return "addCategory";
    }

    @PostMapping("/addNewCategory")
    public String addCategory(CategoryDto categoryDto){
        boolean b = categoryRepository.existsByName(categoryDto.getName());
        if (b){
            return "redirect:/category";
        }
        Category category = new Category();
        category.setName(categoryDto.getName());
        categoryRepository.save(category);
        return "redirect:/category";
    }





}
