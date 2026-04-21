package com.it.controller;

import com.it.config.Operation;
import com.it.entity.Menu;
import com.it.service.MenuService;
import com.it.util.AuthUserUtils;
import com.it.util.BusinessException;
import com.it.util.ResponseCode;
import com.it.util.TreeUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/menu")
@Api(tags = "菜单模块")
public class MenuController {
    @Resource
    private MenuService menuService;
    @Resource
    private AuthUserUtils authUserUtils;

    /**
     * @param
     * @return
     */
    @PostMapping("/menus")
    @ApiOperation(value = "菜单管理数查询接口/需要判断用户权限,显示不同的菜单信息")
    public List<Menu> listMenu() {
        List<Menu> menus = menuService.selectPageByUserId(authUserUtils.getLoginUserId());
        List<Menu> menuList = TreeUtils.buildTree(menus);
        return menuList;
    }

    @PostMapping("/loadMenus")
    @ApiOperation(value = "菜单树形查询接口/显示所有菜单信息")
    public List<Menu> listMenuV2() {
        List<Menu> menuList = TreeUtils.buildTree(menuService.selectPage());
        return menuList;
    }

    /**
     * @param entity
     * @return
     */
    @PostMapping("/menu")
    @Operation("新增菜单")
    @ApiOperation(value = "菜单新增接口", notes = "传入的参数为:\n" +
            "   menu对象\n")
    public void insertMenu(@RequestBody Menu entity) {
        boolean insert = menuService.insert(entity);
        if (!insert) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }

    /**
     * @param entity
     * @return
     */
    @PutMapping("/menu")
    @Operation("编辑菜单")
    @ApiOperation(value = "菜单编辑接口", notes = "传入的参数为:\n" +
            "   menu对象\n")
    public void editMenu(@RequestBody Menu entity) {
        boolean insert = menuService.updateByPrimaryKey(entity);
        if (!insert) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }

    /**
     * @param id
     * @return
     */
    @DeleteMapping("/menu/{menuId}")
    @Operation("删除菜单")
    @ApiOperation(value = "菜单删除接口", notes = "传入的参数为:\n" +
            "   menu主键id\n")
    public void deleteMenu(@PathVariable("menuId") String id) {
        boolean delete = menuService.deleteByPrimaryKey(id);
        if (!delete) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }


    @GetMapping("/menu/{menuId}")
    @ApiOperation(value = "菜单单个对象查询接口", notes = "传入的参数为:\n" +
            "   menu主键id\n")
    public Menu selectOne(@PathVariable("menuId") String id) {
        Menu menu = menuService.selectByPrimaryKey(id);
        if (menu == null) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
        return menu;
    }

}
