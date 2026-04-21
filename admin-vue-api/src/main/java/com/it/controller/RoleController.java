package com.it.controller;

import com.it.config.Operation;
import com.it.entity.Menu;
import com.it.entity.Permission;
import com.it.entity.Role;
import com.it.entity.RolePermission;
import com.it.service.MenuService;
import com.it.service.PermissionService;
import com.it.service.RoleService;
import com.it.util.BusinessException;
import com.it.util.ResponseCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/role")
@Api(tags = "角色模块")
public class RoleController {
    @Resource
    private RoleService roleService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private MenuService menuService;

    /**
     * @return
     */
    @PostMapping("/roles")
    @ApiOperation(value = "角色列表查询接口", notes = "传入的参数为:\n" +
            "    1.查询条件:role对象(属性非必传)\n")
    public List<Role> pageRoles() {
        List<Role> selectPage = roleService.selectPage();
        for (Role role : selectPage) {
            //创建操作权限回显文本集合
            List<String> controlsPermissionList = new ArrayList<>();
            //查询到RolePermission表中该角色下为类型1的数据
            List<RolePermission> rolePermissionsTypeOne = permissionService.selectRolePermissionByType(role.getId(), "1");
            for (RolePermission rolePermission : rolePermissionsTypeOne) {
                //循环将permission的名称添加到controlsPermissionList集合中
                Permission permission = permissionService.selectByPrimaryKey(rolePermission.getItem_id());
                if (permission != null) {
                    controlsPermissionList.add(permission.getName());
                }
            }
            role.setControlsPermissionList(controlsPermissionList);
            //=================================================
            //创建菜单权限回显文本集合
            List<String> menuPermissionList = new ArrayList<>();
            //查询到RolePermission表中该角色下为类型0的数据
            List<RolePermission> rolePermissionsTypeZro = permissionService.selectRolePermissionByType(role.getId(), "0");
            for (RolePermission rolePermission : rolePermissionsTypeZro) {
                //循环将permission的名称添加到menuPermissionList集合中
                Menu menu = menuService.selectByPrimaryKey(rolePermission.getItem_id());
                if (menu != null) {
                    menuPermissionList.add(menu.getName());
                }
            }
            role.setMenuPermissionList(menuPermissionList);
        }
        return selectPage;
    }

    /**
     * @return
     */
    @PostMapping("/roleList")
    @ApiOperation(value = "角色列表查询接口", notes = "传入的参数为:\n" +
            "    1.查询条件:role对象(属性非必传)\n")
    public List<Role> roleList() {
        List<Role> selectPage = roleService.selectPage();
        return selectPage;
    }

    /**
     * @param entity
     * @return
     */
    @PostMapping("/role")
    @Operation("新增角色")
    @ApiOperation(value = "角色新增接口", notes = "传入的参数为:\n" +
            "   role对象\n")
    public void insertRole(@RequestBody Role entity) {
        boolean insert = roleService.insert(entity);
        if (!insert) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }

    /**
     * @param id
     * @return
     */
    @DeleteMapping("/role/{id}")
    @Operation("删除角色")
    @ApiOperation(value = "角色删除接口", notes = "传入的参数为:\n" +
            "   role主键id\n")
    public void deleteRole(@PathVariable String id) {
        boolean delete = roleService.deleteByPrimaryKey(id);
        if (!delete) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }

    /**
     * @param entity
     * @return
     */
    @PutMapping("/role")
    @Operation("编辑角色")
    @ApiOperation(value = "角色编辑接口", notes = "传入的参数为:\n" +
            "   role对象\n")
    public void editRole(@RequestBody Role entity) {
        boolean insert = roleService.updateByPrimaryKey(entity);
        if (!insert) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }

    /**
     * @param id
     * @return
     */
    @GetMapping("/role/{id}")
    @ApiOperation(value = "角色单个对象查询接口", notes = "传入的参数为:\n" +
            "   role主键id\n")
    public Role selectOne(@PathVariable("id") String id) {
        Role role = roleService.selectByPrimaryKey(id);
        if (role == null) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
        //查询该角色下所有为类型1的rolePermission数据,并封装到role下的controlsPermission属性
        List<RolePermission> rolePermissionsTypeOne = permissionService.selectRolePermissionByType(role.getId(), "1");
        List<String> controlsPermission = new ArrayList<>();
        for (RolePermission rolePermission : rolePermissionsTypeOne) {
            controlsPermission.add(rolePermission.getItem_id());
        }
        //=========================================================
        //查询该角色下所有为类型0的rolePermission数据,并封装到role下的menuPermission属性
        List<RolePermission> rolePermissionsTypeZro = permissionService.selectRolePermissionByType(role.getId(), "0");
        List<String> menuPermission = new ArrayList<>();
        for (RolePermission rolePermission : rolePermissionsTypeZro) {
            Menu menu = menuService.selectByPrimaryKey(rolePermission.getItem_id());
            if (menu != null) {
                menuPermission.add(rolePermission.getItem_id());
            }

        }
        role.setControlsPermission(controlsPermission);
        role.setMenuPermission(menuPermission);
        return role;
    }
}
