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

package com.alibaba.codeless.framework.autoconfigure;

import java.lang.reflect.Method;

/**
 * @author xiaolongzuo
 */
public class CodelessControllerMethodFactory {

    private static Method ADD_METHOD;
    private static Method MODIFY_METHOD;
    private static Method DELETE_METHOD;
    private static Method GET_METHOD;
    private static Method LIST_METHOD;
    private static Method LIST_BY_ID_METHOD;
    private static Method LIST_BY_PAGE_METHOD;

    static {
        try {
            Method[] methods = CodelessControllerProxy.class.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals("add")) {
                    ADD_METHOD = method;
                }
                if (method.getName().equals("modify")) {
                    MODIFY_METHOD = method;
                }
                if (method.getName().equals("delete")) {
                    DELETE_METHOD = method;
                }
                if (method.getName().equals("get")) {
                    GET_METHOD = method;
                }
                if (method.getName().equals("list")) {
                    LIST_METHOD = method;
                }
                if (method.getName().equals("listById")) {
                    LIST_BY_ID_METHOD = method;
                }
                if (method.getName().equals("listByPage")) {
                    LIST_BY_PAGE_METHOD = method;
                }
            }
            if (ADD_METHOD == null || MODIFY_METHOD == null || DELETE_METHOD == null || GET_METHOD == null
                || LIST_METHOD == null || LIST_BY_ID_METHOD == null || LIST_BY_PAGE_METHOD == null) {
                throw new NoSuchMethodException();
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private CodelessControllerMethodFactory() {}

    public static Method getAddMethod() {
        return ADD_METHOD;
    }

    public static Method getModifyMethod() {
        return MODIFY_METHOD;
    }

    public static Method getDeleteMethod() {
        return DELETE_METHOD;
    }

    public static Method getGetMethod() {
        return GET_METHOD;
    }

    public static Method getListMethod() {
        return LIST_METHOD;
    }

    public static Method getListByIdMethod() {
        return LIST_BY_ID_METHOD;
    }

    public static Method getListByPageMethod() {
        return LIST_BY_PAGE_METHOD;
    }
}
