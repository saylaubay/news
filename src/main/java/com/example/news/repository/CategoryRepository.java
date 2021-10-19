package com.example.news.repository;

import com.example.news.entity.Category;
import com.example.news.entity.Xabar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Category findByName(String name);

    boolean existsByName(String name);

}
