package com.ymh.example_springboot_consumer;

import com.ymh.example.common.model.User;
import com.ymh.example.common.service.UserService;
import com.ymh.ymh_rpc_spring_boot_starter.annotation.RpcReference;
import org.springframework.stereotype.Service;

@Service
public class ExampleServiceImpl {

    @RpcReference
    private UserService userService;

    public void test(){
        User user = new User();
        user.setName("ymh");
        User resultUser = userService.getUser(user);
        System.out.println(resultUser.getName());
    }
}
