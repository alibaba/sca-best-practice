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

import com.alibaba.codeless.framework.core.utils.JsonUtils;
import com.alibaba.codeless.framework.core.utils.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.MethodParameter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * @author xiaolongzuo
 */
public class JsonHandlerMethodReturnValueHandler implements HandlerMethodReturnValueHandler {

    private static final String CONTENT_TYPE = "application/json;charset=utf-8";

    private static final String DEFAULT_SUCCESS_CODE = "YA-200";

    private MessageSource messageSource;

    public JsonHandlerMethodReturnValueHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return true;
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest) throws Exception {
        mavContainer.setRequestHandled(true);
        HttpServletResponse httpServletResponse = webRequest.getNativeResponse(HttpServletResponse.class);
        httpServletResponse.setContentType(CONTENT_TYPE);
        ServletServerHttpResponse outputMessage = new ServletServerHttpResponse(httpServletResponse);
        Locale locale = (Locale)webRequest.getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME,
            RequestAttributes.SCOPE_SESSION);
        String message;
        try {
            message = messageSource.getMessage(DEFAULT_SUCCESS_CODE, new Object[0], locale);
        } catch (NoSuchMessageException e) {
            message = DefaultMessagesProperties.getMessage(DEFAULT_SUCCESS_CODE);
        }
        JsonResponse jsonResponse = new JsonResponse(DEFAULT_SUCCESS_CODE, message, returnValue);
        outputMessage.getBody().write(StringUtils.toBytes(JsonUtils.toJson(jsonResponse)));
        outputMessage.getBody().flush();
    }

}
