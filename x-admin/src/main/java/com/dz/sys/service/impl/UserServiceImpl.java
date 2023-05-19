package com.dz.sys.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dz.common.utils.JwtUtil;
import com.dz.sys.entity.Menu;
import com.dz.sys.entity.User;
import com.dz.sys.entity.UserRole;
import com.dz.sys.mapper.UserMapper;
import com.dz.sys.mapper.UserRoleMapper;
import com.dz.sys.service.IMenuService;
import com.dz.sys.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dz
 * @since 2023-04-29
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Autowired
    private IMenuService menuService;
    @Override
    public Map<String, Object> login(User user) {
        LambdaQueryWrapper<User>  wrapper =  new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername,user.getUsername());
        User  loginUser =  this.baseMapper.selectOne(wrapper);
        if(loginUser != null && passwordEncoder.matches(user.getPassword(),loginUser.getPassword())){
//        String  key   = "user:" + UUID.randomUUID();
        //存入redis
//            创建jwt
          String token  =   jwtUtil.createToken(loginUser);
            loginUser.setPassword(null);
            //redisTemplate.opsForValue().set(key,loginUser,30, TimeUnit.MINUTES);

            //返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("token",token);
            return  data;
        }

        return null;
    }

    @Override
    public Map<String, Object> getUserInfo(String token) {
        // 从redis查询token
        //Object obj = redisTemplate.opsForValue().get(token);
        // 反序列化
        User loginuser = null;
        try {
            loginuser  = jwtUtil.parseToken(token, User.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
            //User user = JSON.parseObject(JSON.toJSONString(obj),User.class);
        if(loginuser != null){
            Map<String, Object> data =  new HashMap<>();
            data.put("name",loginuser.getUsername());
            data.put("avatar",loginuser.getAvatar());
            List<String> roleList = this.getBaseMapper().getRoleNamesByUserId(loginuser.getId());
            data.put("roles", roleList);

         List<Menu> menuList  = menuService.getMenuListByUserId(loginuser.getId());
         data.put("menuList",menuList);
         return data;
        }
        return null;
    }

    @Override
    public void logout(String token) {
        //redisTemplate.delete(token);
    }

    @Override
    @Transactional
    public void addUser(User user) {
            this.baseMapper.insert(user);
        List<Integer> roleIdList  = user.getRoleIdList();
        if(roleIdList != null){
            for(Integer roleId : roleIdList){
                userRoleMapper.insert(new UserRole(null,user.getId(),roleId));
            }
        }
    }

    @Override
    public User getUserById(Integer id) {
     User user = this.baseMapper.selectById(id);

     LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
     wrapper.eq(UserRole::getUserId,id);
     List<UserRole> userRoleList = userRoleMapper.selectList(wrapper);

     List<Integer> roleIdList = userRoleList.stream()
             .map(userRole -> {return userRole.getRoleId();})
             .collect(Collectors.toList());
        user.setRoleIdList(roleIdList);
        return user;
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        this.baseMapper.updateById(user);

        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId,user.getId());
        userRoleMapper.delete(wrapper);

        List<Integer> roleIdList = user.getRoleIdList();
        if(roleIdList != null){
            for(Integer roleId : roleIdList){
                userRoleMapper.insert(new UserRole(null,user.getId(),roleId));
            }
        }
    }

    @Override
    public void deleteUserById(Integer id) {
        this.baseMapper.deleteById(id);
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId,id);
        userRoleMapper.delete(wrapper);
    }
}
