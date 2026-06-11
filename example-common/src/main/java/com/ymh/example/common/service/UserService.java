package com.ymh.example.common.service;

import com.ymh.example.common.model.User;

/**
 * 用户服务
 */
public interface UserService {

    /**
     * 获取用户
     *
     * @param user
     * @return
     */
    User getUser(User user);

    /**
     * 用于测试mock接口返回值
     * @return
     */
    default short getNumber(){
        return 1;
    }
}
