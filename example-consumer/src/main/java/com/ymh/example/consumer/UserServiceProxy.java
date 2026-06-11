package com.ymh.example.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.ymh.example.common.model.User;
import com.ymh.example.common.service.UserService;
import com.ymh.ymhrpc.model.RpcRequest;
import com.ymh.ymhrpc.model.RpcResponse;
import com.ymh.ymhrpc.serializer.JdkSerializer;

import java.io.IOException;


public class UserServiceProxy implements UserService {
    @Override
    public User getUser(User user) {
        // 指定序列化器
        JdkSerializer serializer = new JdkSerializer();

        //发请求
        RpcRequest rpcRequest=RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterTypes(new Class[]{User.class})
                .args(new Object[]{user})
                .build();

        try {
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            byte[] result;
            try(HttpResponse httpResponse= HttpRequest.post("http://localhost:8080")
                    .body(bodyBytes)
                    .execute()){
                result =httpResponse.bodyBytes();
            }
            RpcResponse rpcResponse=serializer.deserialize(result,RpcResponse.class);
            return (User) rpcResponse.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
