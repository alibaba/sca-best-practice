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

import java.io.UnsupportedEncodingException;

/**
 * @author xiaolongzuo
 */
public interface StringUtils {

    static boolean isEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }

    static boolean isEmpty(String[] s) {
        return s == null || s.length == 0;
    }

    static byte[] toBytes(String s) {
        try {
            return s.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    static String fromBytes(byte[] bytes) {
        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    static boolean isNumeric(final CharSequence cs) {
        if (isEmpty(cs)) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isDigit(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    static Object toPrimitives(Class<?> clazz, String s) {
        if (clazz == String.class) {
            return s;
        } else if (clazz == Integer.class) {
            return Integer.valueOf(s);
        } else if (clazz == Long.class) {
            return Long.valueOf(s);
        } else if (clazz == Float.class) {
            return Float.valueOf(s);
        } else if (clazz == Short.class) {
            return Short.valueOf(s);
        } else if (clazz == Double.class) {
            return Double.valueOf(s);
        } else {
            throw new IllegalArgumentException("Unsupport primitive type " + clazz.getName());
        }

    }

}
