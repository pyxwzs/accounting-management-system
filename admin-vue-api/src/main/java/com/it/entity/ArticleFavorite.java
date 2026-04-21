package com.it.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("sys_article_favorite")
@ApiModel("资讯收藏")
public class ArticleFavorite {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    @ApiModelProperty("用户id")
    private String user_id;
    @ApiModelProperty("资讯id")
    private String article_id;
    @ApiModelProperty("创建时间")
    private String create_time;
}
