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

package com.alibaba.codeless.framework.web;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @author xiaolongzuo
 */
public class DefaultMessagesProperties {

    private DefaultMessagesProperties() {}

    private static final Properties properties;

    static {
        ClassPathResource classPathResource = new ClassPathResource("messages_default.properties");
        properties = new Properties();
        try {
            properties.load(new InputStreamReader(classPathResource.getInputStream(), "UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean contains(String key) {
        return properties.containsKey(key);
    }

    public static String getMessage(String key) {
        return properties.getProperty(key);
    }

    public static String getMessage(String key, Object[] args) {
        String message = properties.getProperty(key);
        if (args == null || message == null) {
            return message;
        }
        for (int i = 0; i < args.length; i++) {
            message = message.replace("{" + i + "}", args[i].toString());
        }
        return message;
    }

}
