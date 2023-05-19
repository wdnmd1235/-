package com.dz.sys.mapper;

import com.dz.sys.entity.User;
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
public interface UserMapper extends BaseMapper<User> {

        public List<String> getRoleNamesByUserId(Integer userId);
}
