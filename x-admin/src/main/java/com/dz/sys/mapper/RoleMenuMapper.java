package com.dz.sys.mapper;

import com.dz.sys.entity.RoleMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author dz
 * @since 2023-04-29
 */
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {
    public List<Integer> getMenuIdListByRoleId(Integer roleId);
}
