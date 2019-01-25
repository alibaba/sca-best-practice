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

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author xiaolongzuo
 */
public class CodelessAutoGenerateHandlerMapping extends RequestMappingHandlerMapping {

    private Map<String, CodelessEntityMetadata> codelessEntityMetadataMap;

    private RequestMappingInfo.BuilderConfiguration config;

    private boolean useSuffixPatternMatch = true;

    private boolean useRegisteredSuffixPatternMatch = false;

    private boolean useTrailingSlashMatch = true;

    @Override
    public void setUseSuffixPatternMatch(boolean useSuffixPatternMatch) {
        this.useSuffixPatternMatch = useSuffixPatternMatch;
    }

    @Override
    public void setUseRegisteredSuffixPatternMatch(boolean useRegisteredSuffixPatternMatch) {
        this.useRegisteredSuffixPatternMatch = useRegisteredSuffixPatternMatch;
        this.useSuffixPatternMatch = (useRegisteredSuffixPatternMatch || this.useSuffixPatternMatch);
    }

    @Override
    public void setUseTrailingSlashMatch(boolean useTrailingSlashMatch) {
        this.useTrailingSlashMatch = useTrailingSlashMatch;
    }

    public void setCodelessEntityMetadataMap(
        Map<String, CodelessEntityMetadata> codelessEntityMetadataMap) {
        this.codelessEntityMetadataMap = codelessEntityMetadataMap;
    }

    @Override
    public void afterPropertiesSet() {
        this.config = new RequestMappingInfo.BuilderConfiguration();
        this.config.setUrlPathHelper(getUrlPathHelper());
        this.config.setPathMatcher(getPathMatcher());
        this.config.setSuffixPatternMatch(this.useSuffixPatternMatch);
        this.config.setTrailingSlashMatch(this.useTrailingSlashMatch);
        this.config.setRegisteredSuffixPatternMatch(this.useRegisteredSuffixPatternMatch);
        this.config.setContentNegotiationManager(getContentNegotiationManager());
        super.afterPropertiesSet();
    }

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    protected void initHandlerMethods() {
        if (codelessEntityMetadataMap == null) {
            return;
        }
        for (String codelessName : codelessEntityMetadataMap.keySet()) {
            Object handler = getApplicationContext().getBean(CodelessControllerProxy.class);
            registerMapping(createRequestMappingInfo(codelessName, "add"), handler,
                CodelessControllerMethodFactory.getAddMethod());
            registerMapping(createRequestMappingInfo(codelessName, "modify"), handler,
                CodelessControllerMethodFactory.getModifyMethod());
            registerMapping(createRequestMappingInfo(codelessName, "delete"), handler,
                CodelessControllerMethodFactory.getDeleteMethod());
            registerMapping(createRequestMappingInfo(codelessName, "get"), handler,
                CodelessControllerMethodFactory.getGetMethod());
            registerMapping(createRequestMappingInfo(codelessName, "list"), handler,
                CodelessControllerMethodFactory.getListMethod());
            registerMapping(createRequestMappingInfo(codelessName, "listById"), handler,
                CodelessControllerMethodFactory.getListByIdMethod());
            registerMapping(createRequestMappingInfo(codelessName, "listByPage"), handler,
                CodelessControllerMethodFactory.getListByPageMethod());
        }
    }

    private RequestMappingInfo createRequestMappingInfo(String codelessName, String action) {
        RequestMappingInfo.Builder builder = RequestMappingInfo
            .paths(resolveEmbeddedValuesInPatterns(new String[] {"/" + codelessName + "/" + action}));
        if ("add".equals(action)) {
            builder.methods(RequestMethod.POST);
        }
        if ("modify".equals(action)) {
            builder.methods(RequestMethod.PUT);
        }
        if ("delete".equals(action)) {
            builder.methods(RequestMethod.DELETE);
        }
        if ("get".equals(action) || Pattern.matches("list.*", action)) {
            builder.methods(RequestMethod.GET);
        }
        builder.mappingName("[" + codelessEntityMetadataMap.get(codelessName).getEntityClass().getSimpleName() + "] "
            + action
            + " mapping");
        return builder.options(this.config).build();
    }

}
