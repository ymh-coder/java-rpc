package com.ymh.example.consumer;

import com.ymh.example.common.model.User;
import com.ymh.example.common.service.UserService;
import com.ymh.ymhrpc.bootstrap.ConsumerBootstrap;
import com.ymh.ymhrpc.proxy.ServiceProxyFactory;

public class ConsumerExample {
    public static void main(String[] args) {
//        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
//        System.out.println(rpc);

        // 服务提供者初始化
        ConsumerBootstrap.init();

        //获取代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);

        User user = new User();
        user.setName("yangmenghui");

        User newUser = userService.getUser(user);

        if(newUser != null){
            System.out.println(newUser.getName());
        }else{
            System.out.println("user==null");
        }

//        for(int i=0;i<3;i++){
//            User newUser = userService.getUser(user);
//
//            if(newUser != null){
//                System.out.println(newUser.getName());
//            }else{
//                System.out.println("user==null");
//            }
//            long number=userService.getNumber();
//            System.out.println(number);
//
//        }




    }
}
