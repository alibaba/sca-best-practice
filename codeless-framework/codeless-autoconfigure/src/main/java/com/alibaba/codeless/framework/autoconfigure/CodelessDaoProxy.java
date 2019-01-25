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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.data.jpa.repository.query.QueryUtils.toOrders;

/**
 * @author xiaolongzuo
 * @author Oliver Gierke
 * @author Eberhard Wolff
 * @author Thomas Darimont
 * @author Mark Paluch
 * @author Christoph Strobl
 * @author Stefan Fussenegger
 * @author Jens Schauder
 */
public class CodelessDaoProxy implements CodelessDao {

    private static final String ID_MUST_NOT_BE_NULL = "The given id must not be null!";

    @Autowired
    private EntityManager entityManager;

    private ConcurrentHashMap<Class<?>, JpaEntityInformation<?, ?>> entityInformationMap = new ConcurrentHashMap<>();

    private <T> JpaEntityInformation<T, ?> getEntityInformation(Class<T> clazz) {
        JpaEntityInformation<T, ?> entityInformation = (JpaEntityInformation<T, ?>)entityInformationMap.get(clazz);
        if (entityInformation != null) {
            return entityInformation;
        }
        entityInformationMap.putIfAbsent(clazz, JpaEntityInformationSupport
            .getEntityInformation(clazz, entityManager));
        return (JpaEntityInformation<T, ?>)entityInformationMap.get(clazz);
    }

    @Transactional
    @Override
    public <T> T save(Class<T> clazz, T entity) {
        EntityInformation<T, ?> entityInformation = getEntityInformation(clazz);
        if (entityInformation.isNew(entity)) {
            entityManager.persist(entity);
            return entity;
        } else {
            return entityManager.merge(entity);
        }
    }

    @Transactional
    @Override
    public <T, ID> void deleteById(Class<T> clazz, ID id) {

        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        delete(findById(clazz, id).orElseThrow(() -> new EmptyResultDataAccessException(
            String.format("No %s entity with id %s exists!", getEntityInformation(clazz).getJavaType(), id), 1)));
    }

    @Transactional
    @Override
    public <T> void delete(T entity) {

        Assert.notNull(entity, "The entity must not be null!");
        entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
    }

    @Override
    public <T, ID> Optional<T> findById(Class<T> clazz, ID id) {

        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return Optional.ofNullable(entityManager.find(clazz, id));
    }

    @Override
    public <T> List<T> findAll(Class<T> clazz) {
        return getQuery(clazz, null, Sort.unsorted()).getResultList();
    }

    @Override
    public <T, ID> List<T> findAllById(Class<T> clazz, Iterable<ID> ids) {

        Assert.notNull(ids, "The given Iterable of Id's must not be null!");

        if (!ids.iterator().hasNext()) {
            return Collections.emptyList();
        }

        JpaEntityInformation<T, ?> entityInformation = getEntityInformation(clazz);

        if (entityInformation.hasCompositeId()) {

            List<T> results = new ArrayList<>();

            for (ID id : ids) {
                findById(clazz, id).ifPresent(results::add);
            }

            return results;
        }

        ByIdsSpecification<T> specification = new ByIdsSpecification<>(entityInformation);
        TypedQuery<T> query = getQuery(clazz, specification, Sort.unsorted());

        return query.setParameter(specification.parameter, ids).getResultList();
    }

    @Override
    public <T> Page<T> findAll(Class<T> clazz, Pageable pageable) {

        if (isUnpaged(pageable)) {
            return new PageImpl<>(findAll(clazz));
        }

        return findAll(clazz, null, pageable);
    }

    public <T> Page<T> findAll(Class<T> clazz, @Nullable Specification<T> spec, Pageable pageable) {

        TypedQuery<T> query = getQuery(clazz, spec, pageable);
        return isUnpaged(pageable) ? new PageImpl<>(query.getResultList())
            : readPage(clazz, query, pageable, spec);
    }

    protected <T> TypedQuery<T> getQuery(Class<T> clazz, @Nullable Specification<T> spec, Pageable pageable) {

        Sort sort = pageable.isPaged() ? pageable.getSort() : Sort.unsorted();
        return getQuery(clazz, spec, sort);
    }

    protected <T> TypedQuery<T> getQuery(Class<T> clazz, @Nullable Specification<T> spec, Sort sort) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(clazz);

        Root<T> root = applySpecificationToCriteria(clazz, spec, query);
        query.select(root);

        if (sort.isSorted()) {
            query.orderBy(toOrders(sort, root, builder));
        }

        return entityManager.createQuery(query);
    }

    protected <T> Page<T> readPage(final Class<T> clazz, TypedQuery<T> query, Pageable pageable,
                                   @Nullable Specification<T> spec) {

        if (pageable.isPaged()) {
            query.setFirstResult((int)pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }

        return PageableExecutionUtils.getPage(query.getResultList(), pageable,
            () -> executeCountQuery(getCountQuery(clazz, spec)));
    }

    protected <T> TypedQuery<Long> getCountQuery(Class<T> clazz, @Nullable Specification<T> spec) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);

        Root<T> root = applySpecificationToCriteria(clazz, spec, query);

        if (query.isDistinct()) {
            query.select(builder.countDistinct(root));
        } else {
            query.select(builder.count(root));
        }

        // Remove all Orders the Specifications might have applied
        query.orderBy(Collections.<Order>emptyList());

        return entityManager.createQuery(query);
    }

    private <S, T> Root<T> applySpecificationToCriteria(Class<T> clazz, @Nullable Specification<T> spec,
                                                        CriteriaQuery<S> query) {

        Assert.notNull(clazz, "Domain class must not be null!");
        Assert.notNull(query, "CriteriaQuery must not be null!");

        Root<T> root = query.from(clazz);

        if (spec == null) {
            return root;
        }

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        Predicate predicate = spec.toPredicate(root, query, builder);

        if (predicate != null) {
            query.where(predicate);
        }

        return root;
    }

    private static long executeCountQuery(TypedQuery<Long> query) {

        Assert.notNull(query, "TypedQuery must not be null!");

        List<Long> totals = query.getResultList();
        long total = 0L;

        for (Long element : totals) {
            total += element == null ? 0 : element;
        }

        return total;
    }

    private static boolean isUnpaged(Pageable pageable) {
        return pageable.isUnpaged();
    }

    private static final class ByIdsSpecification<T> implements Specification<T> {

        private static final long serialVersionUID = 1L;

        private final JpaEntityInformation<T, ?> entityInformation;

        @Nullable
        ParameterExpression<Iterable> parameter;

        ByIdsSpecification(JpaEntityInformation<T, ?> entityInformation) {
            this.entityInformation = entityInformation;
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.data.jpa.domain.Specification#toPredicate(javax.persistence.criteria.Root, javax
         * .persistence.criteria.CriteriaQuery, javax.persistence.criteria.CriteriaBuilder)
         */
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

            Path<?> path = root.get(entityInformation.getIdAttribute());
            parameter = cb.parameter(Iterable.class);
            return path.in(parameter);
        }
    }

}
