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

import com.alibaba.codeless.framework.core.utils.JsonUtils;
import com.alibaba.codeless.framework.core.utils.StringUtils;
import com.alibaba.codeless.framework.web.BusinessException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author xiaolongzuo
 */
public class CodelessControllerProxy implements ApplicationContextAware {

    private Map<String, CodelessEntityMetadata> codelessEntityMetadataMap;

    private CodelessServiceProxy serviceProxy;

    public CodelessControllerProxy() {
    }

    public void setCodelessEntityMetadataMap(
        Map<String, CodelessEntityMetadata> codelessEntityMetadataMap) {
        this.codelessEntityMetadataMap = codelessEntityMetadataMap;
    }

    private String getCodelessName(HttpServletRequest request) {
    	String uri =  request.getRequestURI();
    	// add by phoema 支持server.servlet.context-path自定义
    	uri = uri.substring(request.getContextPath().length());
        String codelessName = uri.split("/")[0];
        if (StringUtils.isEmpty(codelessName)) {
            codelessName = uri.split("/")[1];
        }
        return codelessName;
    }

    private CodelessEntityMetadata getCodelessEntityMetadata(HttpServletRequest request) {
        return codelessEntityMetadataMap.get(getCodelessName(request));
    }

    private <T> Class<T> getEntityClass(HttpServletRequest request) {
        return getEntityClass(getCodelessEntityMetadata(request));
    }

    private <T> Class<T> getEntityClass(CodelessEntityMetadata codelessEntityMetadata) {
        Class<T> entityClass = (Class<T>)codelessEntityMetadata.getEntityClass();
        return entityClass;
    }

    private <T, ID> ID checkIdNotNull(CodelessEntityMetadata codelessEntityMetadata, Class<T> entityClass, T entity) {
        Method getIdMethod = codelessEntityMetadata.getGetIdMethod();
        if (getIdMethod == null) {
            throw new BusinessException("YA-420", new Object[] {"实体" + entityClass.getName() + "的GetIdMethod"});
        }
        try {
            ID id = (ID)getIdMethod.invoke(entity, new Object[0]);
            if (id == null) {
                throw new BusinessException("YA-420", new Object[] {"实体ID"});
            }
            return id;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T add(HttpServletRequest request, @RequestBody String jsonParam) {
        Class<T> entityClass = getEntityClass(request);
        return serviceProxy.save(entityClass, JsonUtils.fromJson(jsonParam, entityClass));
    }

    public <T> T modify(HttpServletRequest request, @RequestBody String jsonParam) {
        CodelessEntityMetadata codelessEntityMetadata = getCodelessEntityMetadata(request);
        Class<T> entityClass = getEntityClass(codelessEntityMetadata);
        T entity = JsonUtils.fromJson(jsonParam, entityClass);
        checkIdNotNull(codelessEntityMetadata, entityClass, entity);
        return serviceProxy.save(entityClass, entity);
    }

    public <T> void delete(HttpServletRequest request) {
        CodelessEntityMetadata codelessEntityMetadata = getCodelessEntityMetadata(request);
        Class<T> entityClass = getEntityClass(codelessEntityMetadata);
        String idString = request.getParameter(codelessEntityMetadata.getIdField().getName());
        serviceProxy.delete(entityClass, codelessEntityMetadata.getIdValue(idString));
    }

    public <T> T get(HttpServletRequest request) {
        CodelessEntityMetadata codelessEntityMetadata = getCodelessEntityMetadata(request);
        Class<T> entityClass = getEntityClass(codelessEntityMetadata);
        String idString = request.getParameter(codelessEntityMetadata.getIdField().getName());
        return serviceProxy.getOne(entityClass, codelessEntityMetadata.getIdValue(idString));
    }

    public <T> List<T> list(HttpServletRequest request) {
        return serviceProxy.getAll(getEntityClass(request));
    }

    public <T, ID> List<T> listById(HttpServletRequest request) {
        CodelessEntityMetadata codelessEntityMetadata = codelessEntityMetadataMap.get(getCodelessName(request));
        Class<T> entityClass = (Class<T>)codelessEntityMetadata.getEntityClass();
        String[] ids = request.getParameterValues(codelessEntityMetadata.getIdField().getName());
        List<ID> idList = new ArrayList<>();
        for (String id : ids) {
            idList.add((ID)codelessEntityMetadata.getIdValue(id));
        }
        return serviceProxy.getAllById(entityClass, idList);
    }

    public <T> CodelessPageResult<T> listByPage(HttpServletRequest request) {
        String pageString = request.getParameter("page");
        String sizeString = request.getParameter("size");
        if (pageString == null || sizeString == null) {
            throw new BusinessException("YA-420", new Object[] {"page或size"});
        }
        Integer page;
        Integer size;
        try {
            page = Integer.valueOf(pageString) - 1;
            size = Integer.valueOf(sizeString);
        } catch (Exception e) {
            throw new BusinessException("YA-501", new Object[] {e.getMessage()});
        }
        if (page < 0) {
            throw new BusinessException("YA-421", new Object[] {"page must be more than 1."});
        }
        if (size <= 0) {
            throw new BusinessException("YA-421", new Object[] {"page must be more than 0."});
        }
        return serviceProxy.getAllByPage(getEntityClass(request), PageRequest.of(page, size));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.serviceProxy = (CodelessServiceProxy)applicationContext.getBean("codelessServiceProxy");
    }

}
