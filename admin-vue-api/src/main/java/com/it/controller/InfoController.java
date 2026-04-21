package com.it.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.it.config.Operation;
import com.it.entity.Info;
import com.it.pojo.PageResult;
import com.it.service.InfoService;
import com.it.util.AuthUserUtils;
import com.it.util.BusinessException;
import com.it.util.ListUtils;
import com.it.util.ResponseCode;
import com.it.vo.resp.MainResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/info")
@Api(tags = "账单模块")
public class InfoController {
    @Resource
    private InfoService InfoService;
    @Resource
    private AuthUserUtils authUserUtils;


    /**
     * @param entity
     * @return
     */
    @PostMapping("infos")
    @ApiOperation(value = "账单分页查询接口", notes = "传入的参数为:\n" +
            "    1.查询条件:Info对象(属性非必传)\n" +
            "    2.page:页数\n" +
            "    3.limit:条数\n")
    public PageResult<Info> pageInfos(@RequestBody Info entity) {
        IPage<Info> selectPage = InfoService.selectPage(entity, entity.getCurrent(), entity.getSize());
        List<Info> InfoList = selectPage.getRecords();
        for (Info Info : InfoList) {
            Info.setCreate_time(Info.getCreate_time().substring(0, 19));
        }
        return PageResult.getPage(selectPage.getRecords(), selectPage.getTotal());
    }

    /**
     * @param ids
     * @return
     */
    @PostMapping("/delete")
    @Operation("删除账单记录")
    @ApiOperation(value = "账单删除接口", notes = "传入的参数为:\n" +
            "   Info主键id\n")
    public void deleteLog(@RequestBody List<String> ids) {
        boolean delete = InfoService.deleteByPrimaryKey(ids);
        if (!delete) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }


    /**
     * @param entity
     * @return
     */
    @PostMapping("/info")
    @Operation("新增账单记录")
    @ApiOperation(value = "账单新增接口", notes = "传入的参数为:\n" +
            "   Info对象\n")
    public void insertRole(@RequestBody Info entity) {
        boolean insert = InfoService.insert(entity);
        if (!insert) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }


    /**
     * @param entity
     * @return
     */
    @PutMapping("/info")
    @Operation("编辑账单记录")
    @ApiOperation(value = "账单编辑接口", notes = "传入的参数为:\n" +
            "   Info对象\n")
    public void editRole(@RequestBody Info entity) {
        boolean insert = InfoService.updateByPrimaryKey(entity);
        if (!insert) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }

    /**
     * @param id
     * @return
     */
    @GetMapping("/info/{id}")
    @ApiOperation(value = "账单单个对象查询接口", notes = "传入的参数为:\n" +
            "  Info主键id\n")
    public Info selectOne(@PathVariable("id") String id) {
        Info entity = InfoService.selectByPrimaryKey(id);
        if (entity == null) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
        return entity;
    }


    @PostMapping("load")
    @ApiOperation(value = "统计图数据加载接口")
    public MainResultVO load(@RequestBody Info entity) {
        MainResultVO mainResultVO = new MainResultVO();
        List<Info> selectList01 = InfoService.selectList(entity);
        //收入支出
        Map<String, List<Info>> resMap01 = ListUtils.groupBy(selectList01, new ListUtils.GroupBy<String, Info>() {
            @Override
            public String groupBy(Info row) {
                return row.getType();
            }
        });
        List<Map<String, String>> mapList01 = new ArrayList<>();
        for (Map.Entry<String, List<Info>> m : resMap01.entrySet()) {
            Map<String, String> map = new HashMap<>(16);
            float price = 0;
            for (Info info : m.getValue()) {
                price = price + info.getPrice();
            }
            map.put("value", price + "");
            map.put("name", m.getKey());
            mapList01.add(map);
        }

        mainResultVO.setMapList01(mapList01);
        //=========================================================
        entity.setType("收入");
        List<Info> selectList02 = InfoService.selectList(entity);
        Map<String, List<String>> incomeMapList = new HashMap<>();

        //按照日期分组
        Map<String, List<Info>> mapList02 = ListUtils.groupBy(selectList02, new ListUtils.GroupBy<String, Info>() {
            @Override
            public String groupBy(Info row) {
                return row.getDay();
            }
        });
        List<String> incomeTimeList = new ArrayList<>();
        List<String> incomePriceList = new ArrayList<>();
        for (Map.Entry<String, List<Info>> m : mapList02.entrySet()) {
            int i = 0;
            float price = 0;
            for (Info info : m.getValue()) {
                price = price + info.getPrice();
            }

            incomePriceList.add(price + "");
            incomeTimeList.add(m.getKey());
        }
        incomeMapList.put("incomeTimeList", incomeTimeList);
        incomeMapList.put("incomePriceList", incomePriceList);
        mainResultVO.setMapList02(incomeMapList);
        //=========================================================
        Map<String, List<String>> incomeTypeMapList = new HashMap<>();
        //按照分类分组
        Map<String, List<Info>> mapList04 = ListUtils.groupBy(selectList02, new ListUtils.GroupBy<String, Info>() {
            @Override
            public String groupBy(Info row) {
                return row.getClassify();
            }
        });
        List<String> incomeTypeTimeList = new ArrayList<>();
        List<String> incomeTypePriceList = new ArrayList<>();
        for (Map.Entry<String, List<Info>> m : mapList04.entrySet()) {
            int i = 0;
            float price = 0;
            for (Info info : m.getValue()) {
                price = price + info.getPrice();
            }

            incomeTypePriceList.add(price + "");
            incomeTypeTimeList.add(m.getKey());
        }
        incomeTypeMapList.put("incomeTypeTimeList", incomeTypeTimeList);
        incomeTypeMapList.put("incomeTypePriceList", incomeTypePriceList);
        mainResultVO.setMapList04(incomeTypeMapList);

        //=========================================================
        entity.setType("支出");
        List<Info> selectList03 = InfoService.selectList(entity);
        Map<String, List<String>> expenditureMapList = new HashMap<>();
        //按照日期分组
        Map<String, List<Info>> mapList03 = ListUtils.groupBy(selectList03, new ListUtils.GroupBy<String, Info>() {
            @Override
            public String groupBy(Info row) {
                return row.getDay();
            }
        });
        List<String> expenditureTimeList = new ArrayList<>();
        List<String> expenditurePriceList = new ArrayList<>();
        for (Map.Entry<String, List<Info>> m : mapList03.entrySet()) {
            Map<String, String> map = new HashMap<>(16);
            int i = 0;
            float price = 0;
            for (Info info : m.getValue()) {
                price = price + info.getPrice();
            }

            expenditurePriceList.add(price + "");
            expenditureTimeList.add(m.getKey());
        }
        expenditureMapList.put("expenditureTimeList", expenditureTimeList);
        expenditureMapList.put("expenditurePriceList", expenditurePriceList);
        mainResultVO.setMapList03(expenditureMapList);
        //=========================================================
        Map<String, List<String>> expenditureTypeMapList = new HashMap<>();
        //按照分类分组
        Map<String, List<Info>> mapList05 = ListUtils.groupBy(selectList03, new ListUtils.GroupBy<String, Info>() {
            @Override
            public String groupBy(Info row) {
                return row.getClassify();
            }
        });
        List<String> expenditureTypeTimeList = new ArrayList<>();
        List<String> expenditureTypePriceList = new ArrayList<>();
        for (Map.Entry<String, List<Info>> m : mapList05.entrySet()) {
            int i = 0;
            float price = 0;
            for (Info info : m.getValue()) {
                price = price + info.getPrice();
            }

            expenditureTypePriceList.add(price + "");
            expenditureTypeTimeList.add(m.getKey());
        }
        expenditureTypeMapList.put("expenditureTypeTimeList", expenditureTypeTimeList);
        expenditureTypeMapList.put("expenditureTypePriceList", expenditureTypePriceList);
        mainResultVO.setMapList05(expenditureTypeMapList);
        //净资产
        float expenditure = 0;
        float income = 0;
        for (Info info : selectList02) {
            income = income + info.getPrice();
        }
        for (Info info : selectList03) {
            expenditure = expenditure + info.getPrice();
        }
        mainResultVO.setPrice(income - expenditure);
        return mainResultVO;
    }

}
