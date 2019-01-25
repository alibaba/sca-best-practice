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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * @author xiaolongzuo
 */
public interface CodelessDao {

    <T> T save(Class<T> clazz, T entity);

    <T, ID> void deleteById(Class<T> clazz, ID id);

    <T> void delete(T entity);

    <T, ID> Optional<T> findById(Class<T> clazz, ID id);

    <T> List<T> findAll(Class<T> clazz);

    <T, ID> List<T> findAllById(Class<T> clazz, Iterable<ID> ids);

    <T> Page<T> findAll(Class<T> clazz, Pageable pageable);
}
