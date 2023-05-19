package com.dz.sys.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dz.common.vo.Result;
import com.dz.sys.entity.Role;
import com.dz.sys.entity.User;
import com.dz.sys.service.IRoleService;
import com.dz.sys.service.IUserService;
import io.lettuce.core.dynamic.annotation.Param;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author dz
 * @since 2023-04-29
 */
@RestController
@RequestMapping("/role")
public class UserRoleController {



    }


