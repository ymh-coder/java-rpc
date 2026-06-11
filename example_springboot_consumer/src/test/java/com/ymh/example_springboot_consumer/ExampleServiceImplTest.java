package com.ymh.example_springboot_consumer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class ExampleServiceImplTest {

    @Resource
    private ExampleServiceImpl exampleServiceImpl;

    @Test
    void test1(){exampleServiceImpl.test();}
}
