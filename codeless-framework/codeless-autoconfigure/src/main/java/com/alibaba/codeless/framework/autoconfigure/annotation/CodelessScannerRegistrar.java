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

package com.alibaba.codeless.framework.autoconfigure.annotation;

import com.alibaba.codeless.framework.autoconfigure.*;
import com.alibaba.codeless.framework.core.utils.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaolongzuo
 */
public class CodelessScannerRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware, ResourceLoaderAware {

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory();

    private ClassLoader classLoader;

    private Environment environment;

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(
            importingClassMetadata.getAnnotationAttributes(CodelessScan.class.getName()));
        String[] basePackages = annotationAttributes.getStringArray("value");
        if (StringUtils.isEmpty(basePackages)) {
            basePackages = annotationAttributes.getStringArray("basePackages");
        }
        Map<String, CodelessEntityMetadata> codelessEntityMetadataMap = new HashMap<>(10);
        for (String basePackage : basePackages) {
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                resolveBasePackage(basePackage) + '/' + "**/*.class";
            try {
                Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
                for (Resource resource : resources) {
                    processResource(resource, codelessEntityMetadataMap);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        registerController(codelessEntityMetadataMap, registry);
    }

    private void processResource(Resource resource, Map<String, CodelessEntityMetadata> codelessEntityMetadataMap)
        throws IOException, ClassNotFoundException {
        if (!resource.isReadable()) {
            return;
        }
        MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
        Class<?> entityClass = classLoader.loadClass(metadataReader.getClassMetadata().getClassName());
        String classSimpleName = entityClass.getSimpleName();
        String codelessName = classSimpleName.substring(0, 1).toLowerCase() + classSimpleName.substring(1);
        if (metadataReader.getAnnotationMetadata().hasAnnotation(EnableCodeless.class.getName())) {
            codelessEntityMetadataMap.put(codelessName, new CodelessEntityMetadata(entityClass));
        }
    }

    private void registerController(Map<String, CodelessEntityMetadata> codelessEntityMetadataMap,
                                    BeanDefinitionRegistry registry) {
        GenericBeanDefinition handlerMappingBd = new GenericBeanDefinition();
        handlerMappingBd.setBeanClass(CodelessAutoGenerateHandlerMapping.class);
        handlerMappingBd.getPropertyValues().addPropertyValue("codelessEntityMetadataMap", codelessEntityMetadataMap);
        registry.registerBeanDefinition("codelessAutoGenerateHandlerMapping", handlerMappingBd);

        GenericBeanDefinition controllerProxyBd = new GenericBeanDefinition();
        controllerProxyBd.setBeanClass(CodelessControllerProxy.class);
        controllerProxyBd.getPropertyValues().addPropertyValue("codelessEntityMetadataMap", codelessEntityMetadataMap);
        registry.registerBeanDefinition("codelessControllerProxy", controllerProxyBd);

        GenericBeanDefinition serviceProxyBd = new GenericBeanDefinition();
        serviceProxyBd.setBeanClass(CodelessServiceProxy.class);
        serviceProxyBd.setDependsOn("codelessDao");
        registry.registerBeanDefinition("codelessServiceProxy", serviceProxyBd);

        GenericBeanDefinition daoProxyBd = new GenericBeanDefinition();
        daoProxyBd.setBeanClass(CodelessDaoProxy.class);
        registry.registerBeanDefinition("codelessDaoProxy", daoProxyBd);

        GenericBeanDefinition daoFactoryBd = new GenericBeanDefinition();
        daoFactoryBd.setBeanClass(CodelessDaoFactoryBean.class);
        registry.registerBeanDefinition("codelessDao", daoFactoryBd);
    }

    private String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(this.environment.resolveRequiredPlaceholders(basePackage));
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.classLoader = resourceLoader.getClassLoader();
    }

}
