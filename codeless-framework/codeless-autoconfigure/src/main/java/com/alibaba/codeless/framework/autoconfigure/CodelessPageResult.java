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

import java.util.List;

/**
 * @author xiaolongzuo
 */
public class CodelessPageResult<T> {

    private int page;

    private int size;

    private long totalNumber;

    private int totalPage;

    private List<T> result;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(long totalNumber) {
        this.totalNumber = totalNumber;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }

    public static <T> CodelessPageResult<T> of(Page<T> page) {
        CodelessPageResult<T> codelessPageResult = new CodelessPageResult<>();
        codelessPageResult.setPage(page.getNumber());
        codelessPageResult.setSize(page.getSize());
        codelessPageResult.setTotalNumber(page.getTotalElements());
        codelessPageResult.setTotalPage(page.getTotalPages());
        codelessPageResult.setResult(page.getContent());
        return codelessPageResult;
    }

}
