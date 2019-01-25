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

import com.alibaba.codeless.framework.core.utils.StringUtils;
import org.springframework.util.Assert;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author xiaolongzuo
 */
public class CodelessEntityMetadata {

    private Class<?> entityClass;

    private Method getIdMethod;

    private Field idField;

    public CodelessEntityMetadata(Class<?> entityClass) {
        Assert.notNull(entityClass, "The entity class must not be null!");
        this.entityClass = entityClass;
        this.idField = findIdField(this.entityClass);
        this.getIdMethod = findGetIdMethod(this.entityClass, this.idField);
    }

    private Field findIdField(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        if (fields == null) {
            return null;
        }
        for (Field field : fields) {
            if (field.getDeclaredAnnotation(Id.class) != null) {
                return field;
            }
        }
        return null;
    }

    private Method findGetIdMethod(Class<?> clazz, Field idField) {
        String fieldName = idField.getName();
        String getMethodName = fieldName.length() <= 1 ? "get" + fieldName.toUpperCase()
            : "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        try {
            return clazz.getDeclaredMethod(getMethodName, new Class[0]);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public Method getGetIdMethod() {
        return getIdMethod;
    }

    public Field getIdField() {
        return idField;
    }

    public Object getIdValue(String stringValue) {
        return StringUtils.toPrimitives(idField.getType(), stringValue);
    }

}
