package com.it.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class HomeRespVO {


    @ApiModelProperty(value = "使用车位")
    private Integer haveNum;

    @ApiModelProperty(value = "剩余车位")
    private Integer useNum;


    @ApiModelProperty(value = "统计")
    private Map<String, List<String>> parkMapList;

}
