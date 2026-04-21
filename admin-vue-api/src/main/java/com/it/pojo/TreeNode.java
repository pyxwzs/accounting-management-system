package com.it.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: 明思梨: https://space.bilibili.com/486686697
 * @description:
 * @date: 2024/8/20
 */
@Data
public class TreeNode<T> implements Serializable {

    /**
     * id
     */

    private String id;

    /**
     * 上级id
     */

    private String parent_id;


    /**
     * 子集数据
     */
    @TableField(exist = false)
    private List<T> children = new ArrayList<>();
}
