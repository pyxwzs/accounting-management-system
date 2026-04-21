package com.it.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.it.entity.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 菜单信息数据访问层
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {
    /**
     * 根据UserId获取菜单信息
     *
     * @return
     */
    List<Menu> getMenuInfoByUserId(String userId);
}
