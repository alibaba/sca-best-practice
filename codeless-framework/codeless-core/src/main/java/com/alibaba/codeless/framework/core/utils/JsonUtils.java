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

package com.alibaba.codeless.framework.core.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

/**
 * @author xiaolongzuo
 */
public interface JsonUtils {

    Gson GSON = new GsonBuilder()
        .setDateFormat("yyyy-MM-dd HH:mm:ss")
        .create();

    static String toJson(Object object) {
        return GSON.toJson(object);
    }

    static <T> T fromJson(String jsonString, Class<T> clazz) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }
        return GSON.fromJson(jsonString, clazz);
    }

    static <T> T fromJson(String jsonString, Type type) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }
        return (T)GSON.fromJson(jsonString, type);
    }

}
