package com.ymh.example_springboot_provider;

import com.ymh.example.common.model.User;
import com.ymh.example.common.service.UserService;
import com.ymh.ymh_rpc_spring_boot_starter.annotation.RpcService;
import org.springframework.stereotype.Service;

@Service
@RpcService
public class UserServiceeImpl implements UserService {

    @Override
    public User getUser(User user) {
        System.out.println("用户名："+user.getName());
        return user;
    }
}
