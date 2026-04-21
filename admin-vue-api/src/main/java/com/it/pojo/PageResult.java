package com.it.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    @ApiModelProperty(value = "当前页记录数")
    private List<T> list;
    @ApiModelProperty(value = "分页总数")
    private long total;

    private PageResult() {
    }

    public PageResult(List<T> list, long total) {
        this.list = list;
        this.total = total;
    }

    public static <T> PageResult<T> getPage(List<T> list, long total) {
        return new PageResult<>(list, total);
    }
}
