package com.it.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class MainResultVO {
    @ApiModelProperty(value = "收入支出占比")
    private List<Map<String, String>> mapList01;
    @ApiModelProperty(value = "收入占比")
    private Map<String, List<String>> mapList02;
    @ApiModelProperty(value = "支出占比")
    private Map<String, List<String>> mapList03;
    private Map<String, List<String>> mapList04;
    private Map<String, List<String>> mapList05;

    private Float price;
}
