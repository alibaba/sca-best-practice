/*
 * Copyright (C) 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.sca.bp.usercenter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;

/**
 * @author xiaolongzuo
 */
@Controller
@RequestMapping("/example")
@RefreshScope
public class ExampleController {

    @Value("${user.id}")
    private String userId;

    @Autowired
    private MessageChannel output;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @RequestMapping("/testConfig")
    public String testConfig() {
        return "Hello, " + userId;
    }

    @RequestMapping("/testMq")
    public String testMq() {
        boolean send = output.send(MessageBuilder.createMessage("I am a message", new MessageHeaders(new HashMap<>())));
        return String.valueOf(send);
    }

    @RequestMapping("/testRedis")
    public String testRedis() {
        redisTemplate.opsForValue().set("testKey", "Hello, chenzhu!");
        return redisTemplate.opsForValue().get("testKey");
    }

}
