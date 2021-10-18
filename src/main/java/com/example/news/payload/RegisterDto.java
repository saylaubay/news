package com.example.news.payload;

import com.example.news.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {

    private String firstName;
    private String lastName;
    private String username;
    private String password;

    private Role role;

}
