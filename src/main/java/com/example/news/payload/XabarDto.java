package com.example.news.payload;

import com.example.news.entity.Category;
import com.example.news.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class XabarDto {
    private Integer id;
    private String title;
    private String body;
    private String imgName;
    private String originalName;

    private Category category;

    private User user;

}
