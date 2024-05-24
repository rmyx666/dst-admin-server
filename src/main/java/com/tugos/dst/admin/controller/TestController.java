package com.tugos.dst.admin.controller;


import com.tugos.dst.admin.common.ResultCodeEnum;
import com.tugos.dst.admin.common.ResultVO;
import com.tugos.dst.admin.entity.User;
import com.tugos.dst.admin.service.EhcacheDataService;

import com.tugos.dst.admin.vo.UpdatePwdVO;
import com.tugos.dst.admin.vo.UpdateUserDetailVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author qinming
 * @date 2020-05-16
 * <p> 用户管理控制器 </p>
 */
@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    private EhcacheDataService userService;

    @GetMapping("/get")
    @ResponseBody
    public User getUser() {

        User user = userService.getUser();
        return user;
    }

    @PostMapping("/update")
    @ResponseBody
    public User updateUser(@RequestParam String username, @RequestParam String password, @RequestParam String nickname) {
        try {
            User user = userService.updatePassword( password);
            return user;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }


}
