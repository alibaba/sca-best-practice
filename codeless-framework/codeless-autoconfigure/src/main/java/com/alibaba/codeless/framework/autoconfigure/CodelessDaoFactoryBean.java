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

import org.springframework.aop.SpringProxy;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.projection.DefaultMethodInvokingMethodInterceptor;
import org.springframework.data.repository.core.support.SurroundingTransactionDetectorMethodInterceptor;

/**
 * @author xiaolongzuo
 */
public class CodelessDaoFactoryBean implements FactoryBean<CodelessDao>, ResourceLoaderAware {

    private ClassLoader classLoader;

    @Autowired
    private CodelessDaoProxy codelessDaoProxy;

    @Override
    public CodelessDao getObject() {
        ProxyFactory result = new ProxyFactory();
        result.setTarget(codelessDaoProxy);
        result.setInterfaces(CodelessDao.class, SpringProxy.class);
        result.addAdvice(SurroundingTransactionDetectorMethodInterceptor.INSTANCE);
        result.addAdvisor(ExposeInvocationInterceptor.ADVISOR);
        result.addAdvice(new DefaultMethodInvokingMethodInterceptor());
        return (CodelessDao)result.getProxy(classLoader);
    }

    @Override
    public Class<?> getObjectType() {
        return CodelessDao.class;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.classLoader = resourceLoader.getClassLoader();
    }

}
