package com.dz.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dz.common.vo.Result;
import com.dz.sys.entity.User;
import com.dz.sys.service.IUserService;
import io.lettuce.core.dynamic.annotation.Param;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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
@Api(tags = {"用户接口列表"})
@RestController
@RequestMapping("/user")
//@CrossOrigin 处理跨域
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/a")
    public Result<List<User>> getAllUser(){
        List<User>  list =  userService.list();
        return Result.sucess(list,"查询成功");
    }
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<Map<String,Object>> login(@RequestBody  User user){
     Map<String,Object> data = userService.login(user);
        if(data != null){
            return Result.sucess(data);
        }
        return Result.fail(20002,"用户名或密码错误");


    }
    @GetMapping("/info")
    public Result<?> getUserInfo(@Param("token") String token){
        Map<String,Object> data = userService.getUserInfo(token);
        if(data != null){
            return Result.sucess(data);
        }
        return Result.fail(20003,"用户信息获取失败");
    }

    @PostMapping("/logout")
    public Result<?> logout(@RequestHeader("X-Token") String token){
        userService.logout(token);
        return Result.sucess("注销成功");
    }

    @GetMapping("/list")
    public Result<Map<String,Object>> GetUserList(@RequestParam(value = "username",required = false) String username,
                                              @RequestParam(value = "phone",required = false) String phone,
    @RequestParam(value = "pageNo",required = false) Long pageNo,@RequestParam(value = "pageSize",required = false) Long pageSize){
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper();
        wrapper.eq(username != null, User::getUsername, username);
        wrapper.eq(phone != null, User::getPhone, phone);
        wrapper.orderByDesc(User::getId);
        Page<User> page = new Page<>(pageNo, pageSize);
        userService.page(page, wrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("total", page.getTotal());
        data.put("rows", page.getRecords());

        return Result.sucess(data);

    }

    @PostMapping
    public Result<?> addUser(@RequestBody  User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.addUser(user);
        return Result.sucess("新增用户成功");
    }

    @PutMapping
    public Result<?> updateUser(@RequestBody  User user){
        user.setPassword(null);
        userService.updateUser(user);
        return Result.sucess("修改用户成功");
    }

    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable("id") Integer id){
            User user = userService.getUserById(id);
            return  Result.sucess(user);
    }

    @DeleteMapping("/{id}")
    public Result<User> deleteUserById(@PathVariable("id") Integer id){
        userService.deleteUserById(id);
        return  Result.sucess("删除成功");
    }
}
