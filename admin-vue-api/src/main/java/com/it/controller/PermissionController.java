package com.it.controller;

import com.it.config.Operation;
import com.it.entity.Permission;
import com.it.entity.RolePermission;
import com.it.entity.User;
import com.it.service.PermissionService;
import com.it.util.AuthUserUtils;
import com.it.util.BusinessException;
import com.it.util.ResponseCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/permission")
@Api(tags = "权限模块")
public class PermissionController {
    @Resource
    private PermissionService permissionService;
    @Resource
    private AuthUserUtils authUserUtils;

    /**
     * @param entity
     * @return
     */
    @PostMapping("/permissions")
    @ApiOperation(value = "权限查询接口", notes = "传入的参数为:\n" +
            "    1.查询条件:Permission(属性非必传)\n")
    public List<Permission> listPermission(@RequestBody Permission entity) {
        List<Permission> permissionList = permissionService.selectPage(entity);
        return permissionList;
    }


    /**
     * @param
     * @return
     */
    @GetMapping("/permissions")
    @ApiOperation(value = "权限查询接口", notes = "传入的参数为:\n" +
            "    1.查询条件:Permission(属性非必传)\n")
    public List<Permission> listPermission() {
        List<Permission> permissionList = permissionService.selectPage(new Permission());
        return permissionList;
    }

    /**
     * @param entity
     * @return
     */
    @PostMapping("/permission")
    @Operation("新增权限")
    @ApiOperation(value = "权限新增接口", notes = "传入的参数为:\n" +
            "   Permission对象\n")
    public void insertPermission(@RequestBody Permission entity) {
        boolean insert = permissionService.insert(entity);
        if (!insert) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }

    /**
     * @param id
     * @return
     */
    @DeleteMapping("/permission/{id}")
    @Operation("删除权限")
    @ApiOperation(value = "权限删除接口", notes = "传入的参数为:\n" +
            "   Permission主键id\n")
    public void deletePermission(@PathVariable String id) {
        boolean delete = permissionService.deleteByPrimaryKey(id);
        if (!delete) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }

    /**
     * @param entity
     * @return
     */
    @PutMapping("/permission")
    @Operation("编辑权限")
    @ApiOperation(value = "权限编辑接口", notes = "传入的参数为:\n" +
            "   Permission对象\n")
    public void editPermission(@RequestBody Permission entity) {
        boolean insert = permissionService.updateByPrimaryKey(entity);
        if (!insert) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }

    /**
     * @param id
     * @return
     */
    @GetMapping("/permission/{id}")
    @ApiOperation(value = "权限单个对象查询接口", notes = "传入的参数为:\n" +
            "   permission主键id\n")
    public Permission selectOne(@PathVariable("id") String id) {
        Permission permission = permissionService.selectByPrimaryKey(id);
        if (permission == null) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
        return permission;
    }

    /**
     * @return
     */
    @GetMapping("/perms")
    @ApiOperation(value = "获取当前登录用户的权限集合")
    public List<String> perms() {
        ArrayList<String> permsList = new ArrayList<>();
        User loginUser = authUserUtils.getLoginUser();
        if (loginUser == null) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
        List<RolePermission> rolePermissions = permissionService.selectRolePermissionByType(loginUser.getRoleId(), "1");
        for (RolePermission rolePermission : rolePermissions) {
            Permission permission = permissionService.selectByPrimaryKey(rolePermission.getItem_id());
            if (permission != null) {
                permsList.add(permission.getMark());
            }
        }
        return permsList;
    }
}
