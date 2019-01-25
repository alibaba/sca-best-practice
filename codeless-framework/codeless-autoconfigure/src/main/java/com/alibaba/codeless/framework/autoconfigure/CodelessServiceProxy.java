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

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/**
 * @author xiaolongzuo
 */
public class CodelessServiceProxy implements ApplicationContextAware {

    private CodelessDao codelessDao;

    public <T> T save(Class<T> clazz, T entity) {
        return codelessDao.save(clazz, entity);
    }

    public <T, ID> void delete(Class<T> clazz, ID id) {
        codelessDao.deleteById(clazz, id);
    }

    public <T, ID> T getOne(Class<T> clazz, ID id) {
        return codelessDao.findById(clazz, id).get();
    }

    public <T> List<T> getAll(Class<T> clazz) {
        return codelessDao.findAll(clazz);
    }

    public <T, ID> List<T> getAllById(Class<T> clazz, List<ID> ids) {
        return codelessDao.findAllById(clazz, ids);
    }

    public <T> CodelessPageResult<T> getAllByPage(Class<T> clazz, PageRequest pageRequest) {
        return CodelessPageResult.of(codelessDao.findAll(clazz, pageRequest));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.codelessDao = (CodelessDao)applicationContext.getBean("codelessDao");
    }

}
