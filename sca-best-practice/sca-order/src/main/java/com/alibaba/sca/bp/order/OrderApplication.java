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

package com.alibaba.sca.bp.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.SubscribableChannel;

import java.io.UnsupportedEncodingException;

/**
 * @author xiaolongzuo
 */
@SpringBootApplication
@EnableBinding({Sink.class})
@EnableDiscoveryClient
@EnableFeignClients("com.alibaba.sca.bp.order.rest")
public class OrderApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(OrderApplication.class, args);
        SubscribableChannel input = (SubscribableChannel)applicationContext.getBean("input");
        input.subscribe(message -> {
            try {
                System.out.println("Get mq message ,content is : " + new String((byte[])message.getPayload(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
