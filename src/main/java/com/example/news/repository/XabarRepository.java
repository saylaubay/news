package com.example.news.repository;

import com.example.news.entity.Category;
import com.example.news.entity.User;
import com.example.news.entity.Xabar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface XabarRepository extends JpaRepository<Xabar, Integer> {

    List<Xabar> findByCategory(Category category);

    List<Xabar> findByTitleContaining(String title);

    Xabar findByTitle(String title);

    List<Xabar> findByUser(User user);

}
