package com.ymh.example.consumer;

import com.ymh.example.common.model.User;
import com.ymh.example.common.service.UserService;
import com.ymh.ymhrpc.proxy.ServiceProxyFactory;

public class EasyConsumerExample {
    public static void main(String[] args) {
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("ymh");

        User newUser = userService.getUser(user);
        if(newUser != null) {
            System.out.println(newUser.getName());
        }else{
            System.out.println("user==null");
        }

    }
}
