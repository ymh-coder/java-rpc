package com.ymh.example.provider;

import com.ymh.example.common.model.User;
import com.ymh.example.common.service.UserService;

public class UserServiceImpl implements UserService {

    public User getUser(User user) {
        System.out.println("用户名："+user.getName());
        return user;
    }
}
