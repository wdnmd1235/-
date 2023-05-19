package com.dz.sys.mapper;

import com.dz.sys.entity.Menu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author dz
 * @since 2023-04-29
 */
public interface MenuMapper extends BaseMapper<Menu> {

        public List<Menu> getMenuListByUserId(@Param("userId") Integer userId,
                                              @Param("pid") Integer pid);


}
