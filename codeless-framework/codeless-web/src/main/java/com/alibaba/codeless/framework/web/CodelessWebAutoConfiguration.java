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

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.Arrays;
import java.util.Locale;

/**
 * @author xiaolongzuo
 */
@Configuration
@ConditionalOnProperty(name = "spring.yasha.web.enabled", matchIfMissing = true)
public class CodelessWebAutoConfiguration implements InitializingBean {

    private final RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    private final MessageSource messageSource;

    public CodelessWebAutoConfiguration(RequestMappingHandlerAdapter requestMappingHandlerAdapter,
                                        MessageSource messageSource) {
        this.requestMappingHandlerAdapter = requestMappingHandlerAdapter;
        Locale.setDefault(Locale.ROOT);
        this.messageSource = messageSource;
    }

    @Bean
    public JsonHandlerExceptionResolver jsonHandlerExceptionResolver() {
        return new JsonHandlerExceptionResolver(messageSource);
    }

    @Bean
    public JsonHandlerMethodReturnValueHandler jsonHandlerMethodReturnValueHandler() {
        return new JsonHandlerMethodReturnValueHandler(messageSource);
    }

    @Override
    public void afterPropertiesSet() {
        requestMappingHandlerAdapter.setReturnValueHandlers(Arrays.asList(jsonHandlerMethodReturnValueHandler()));
    }
}
