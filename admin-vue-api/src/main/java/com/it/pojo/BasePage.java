package com.it.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

@Data
public class BasePage {
    /**
     * 分页大小
     */
    @NotNull(message = "分页大小值不能为空")
    @Max(value = 1000, message = "最大只能查询1000条数据")
    @TableField(exist = false)
    private Integer size;

    /**
     * 当前页页码
     */
    @NotNull(message = "当前页页码不能为空")
    @TableField(exist = false)
    private Integer current;
}
