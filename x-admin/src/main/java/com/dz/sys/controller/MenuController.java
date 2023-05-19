package com.dz.sys.controller;

import com.dz.common.vo.Result;
import com.dz.sys.entity.Menu;
import com.dz.sys.service.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author dz
 * @since 2023-04-29
 */
@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private IMenuService menuService;

    @GetMapping
    public Result<?> getAllMenu(){
        List<Menu> menuList =  menuService.getAllMenu();
        return Result.sucess(menuList);
    }

}
