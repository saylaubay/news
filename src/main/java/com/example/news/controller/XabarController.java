package com.example.news.controller;

import com.example.news.entity.Category;
import com.example.news.entity.User;
import com.example.news.entity.Xabar;
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
@RequestMapping("/")
public class XabarController {

    @Autowired
    XabarRepository xabarRepository;
    @Autowired
    CategoryRepository categoryRepository;

    private static final String uploadDirectory="src/main/resources/static/fayllar";

    @GetMapping("/category/{catName}")
    public String byCategory(@PathVariable String catName, Model model){
        Category category = categoryRepository.findByName(catName);
        List<Xabar> list = xabarRepository.findByCategory(category);
        List<Category> categories = categoryRepository.findAll();

        model.addAttribute("xabars", list);
        model.addAttribute("categories", categories);
        return "category";
    }

    @GetMapping
    public String home(Model model){
        List<Xabar> all = xabarRepository.findAll();
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("xabars", all);
        model.addAttribute("categories", categories);
        model.addAttribute("query", "");

        return "index";
    }

    @GetMapping("/newsList")
    public String newsList(Model model){
        List<Xabar> all = xabarRepository.findAll();
        model.addAttribute("xabars", all);
        return "newsList";
    }

    @GetMapping("/addNew")
    public String forAddNew(Model model){
        model.addAttribute("newXabar", new XabarDto());
        List<Category> all = categoryRepository.findAll();
        model.addAttribute("categories", all);
        return "addNew";
    }

    @PostMapping("/addXabar")
    public String addNew(@RequestParam("fayl") MultipartFile multipartFile,
                         @ModelAttribute("xabarDto") XabarDto xabarDto) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();

        Xabar xabar = new Xabar();
        xabar.setTitle(xabarDto.getTitle());
        xabar.setBody(xabarDto.getBody());
        xabar.setOriginalName(originalFilename);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user= (User) authentication.getPrincipal();
        xabar.setUser(user);

        Optional<Category> byId = categoryRepository.findById(xabarDto.getCategory().getId());
            if (byId.isPresent()){
                Category category = byId.get();
                xabar.setCategory(category);
            }

//        String pathName = UUID.randomUUID().toString();

        String[] split = originalFilename.split("\\.");
        String pathName = UUID.randomUUID().toString() + "." + split[split.length-1];

        xabar.setImgName(pathName);

        xabarRepository.save(xabar);

        Path path = Paths.get(uploadDirectory + "/" + pathName);

        Files.copy(multipartFile.getInputStream(), path);
//Files.delete();
        return "redirect:/";
    }

//    @PostMapping("/addNew")
//    public String addNew(
////            @PathVariable Integer id,
//                         @RequestParam("fayl") MultipartFile multipartFile,
//                         @ModelAttribute("xabarDto") XabarDto xabarDto) throws IOException {
//        String originalFilename = multipartFile.getOriginalFilename();
//
////        Optional<Xabar> xb = xabarRepository.findById(id);
//        if (xb.isPresent()){
//            Xabar dbXabar = xb.get();
//
//            dbXabar.setTitle(xabarDto.getTitle());
//            dbXabar.setBody(xabarDto.getBody());
//            dbXabar.setOriginalName(originalFilename);
//
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            User user= (User) authentication.getPrincipal();
//            dbXabar.setUser(user);
//
//            Optional<Category> byId = categoryRepository.findById(xabarDto.getCategory().getId());
//            if (byId.isPresent()){
//                Category category = byId.get();
//                dbXabar.setCategory(category);
//            }
//
//            String[] split = originalFilename.split("\\.");
//            String pathName = UUID.randomUUID().toString() + "." + split[split.length-1];
//
//            dbXabar.setImgName(pathName);
//
//            xabarRepository.save(dbXabar);
//
//            Path path = Paths.get(uploadDirectory + "/" + pathName);
//
//            Files.copy(multipartFile.getInputStream(), path);
////Files.delete();
//            return "redirect:/";
//        }
//        return "error";
//    }

    @GetMapping("/editNew/{id}")
    public String forEdit(@PathVariable Integer id, Model model){
        System.out.println(id);
        List<Category> all = categoryRepository.findAll();
        Optional<Xabar> byId = xabarRepository.findById(id);

        if (byId.isPresent()){
            Xabar xabar = byId.get();
            model.addAttribute("editXabar", xabar);
            model.addAttribute("categories", all);
            return "editXabar";
        }

        return "error";
    }

    @PostMapping("/editNew/{id}")
    public String edit(@RequestParam("fayl") MultipartFile multipartFile,
                       Xabar xb, @PathVariable Integer id) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        String[] split = originalFilename.split("\\.");
        String pathName = UUID.randomUUID().toString() + "." + split[split.length-1];

        System.out.println(id);

        Optional<Xabar> byId = xabarRepository.findById(id);
        if (byId.isPresent()){
            Xabar xabar = byId.get();
            xabar.setTitle(xb.getTitle());
            xabar.setBody(xb.getBody());
            xabar.setOriginalName(originalFilename);

            xabar.setImgName(pathName);
            xabar.setUser(xb.getUser());
            xabar.setCategory(xb.getCategory());

            xabarRepository.save(xabar);

            Path path = Paths.get(uploadDirectory + "/" + pathName);

            Files.copy(multipartFile.getInputStream(), path);
            return "redirect:/newsList";
        }

        return "index";
    }

    @GetMapping("/deleteNew/{id}")
    public String deleteNew(@PathVariable Integer id){
        try {
            xabarRepository.deleteById(id);
            return "redirect:/newsList";
        }catch (Exception e){
            return "error";
        }
    }

    @PostMapping("/search")
    public String search(@RequestParam String query, Model model){
        System.out.println(query);
        List<Xabar> searcingNews = xabarRepository.findByTitleContaining(query);
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("xabarlar", searcingNews);
        model.addAttribute("categories", categories);
        return "search";
    }

    @GetMapping("/news/{title}")
    public String oneNew(@PathVariable String title, Model model){
        Xabar xabar = xabarRepository.findByTitle(title);
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("xabar", xabar);
        model.addAttribute("categories", categories);
        return "oneXabar";
    }

    @GetMapping("/myNews")
    public String myNews(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User)authentication.getPrincipal();
        List<Xabar> xabarList = xabarRepository.findByUser(user);
        model.addAttribute("myXabars", xabarList);
        return "myXabarList";
    }

    @GetMapping("/allNewsList")
    public String allNews(Model model){
        List<Xabar> xabars = xabarRepository.findAll();
        model.addAttribute("xabars", xabars);
        return "allNews";
    }



}
