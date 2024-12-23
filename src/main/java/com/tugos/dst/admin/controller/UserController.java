package com.tugos.dst.admin.controller;


import com.tugos.dst.admin.common.ResultCodeEnum;
import com.tugos.dst.admin.common.ResultVO;
import com.tugos.dst.admin.entity.User;
import com.tugos.dst.admin.service.DataService;
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
@RequestMapping("/system/user")
public class UserController {

    @Autowired
    private DataService dataService;

    /**
     * 用户信息页
     */
    @GetMapping("/detail")
    @RequiresAuthentication
    public String detail(Model model) {
        UpdateUserDetailVO user = new UpdateUserDetailVO();
        model.addAttribute("user", user);
        return "/system/user/detail";
    }

    /**
     * 修改密码页
     */
    @GetMapping("/updatePwd")
    @RequiresAuthentication
    public String updatePwd() {
        return "/system/user/updatePwd";
    }

    @PostMapping("/setNewPwd")
    @RequiresAuthentication
    @ResponseBody
    public ResultVO setNewPwd(@RequestBody UpdatePwdVO vo) {
        //暂时先修改成这样，从用户信息中获取的值是加密的，现在不太好搞处理
//        User userInfo = (User) SecurityUtils.getSubject().getPrincipal();
        User userInfo = dataService.getUser();
        if (StringUtils.isAnyBlank(vo.getOldPwd(), vo.getNewPwd(), vo.getConfirmPwd())) {
            return ResultVO.fail(ResultCodeEnum.UPDATE_PWD_ERROR1);
        }
        if (!userInfo.getPassword().equals(vo.getOldPwd())) {
            return ResultVO.fail(ResultCodeEnum.UPDATE_PWD_ERROR2);
        }
        if (!vo.getNewPwd().equals(vo.getConfirmPwd())) {
            return ResultVO.fail(ResultCodeEnum.UPDATE_PWD_ERROR3);
        }
        int weakPsw = 6;
        if (vo.getNewPwd().length() <= weakPsw) {
            return ResultVO.fail(ResultCodeEnum.UPDATE_PWD_ERROR4);
        }
        dataService.updatePassword(vo.getNewPwd());
        //退出登录
        SecurityUtils.getSubject().logout();
        return ResultVO.success("success");
    }

    /**
     * 创建新用户用的 目前看来没有投入使用
     * @param vo
     * @return
     */
//    @PostMapping("/setNewUserDetail")
//    @RequiresAuthentication
//    @ResponseBody
//    public ResultVO setNewUserDetail(@RequestBody UpdateUserDetailVO vo) {
//        User userInfo = (User) SecurityUtils.getSubject().getPrincipal();
//        if (StringUtils.isAnyBlank(vo.getNickname(),vo.getPicture(),vo.getUsername())){
//            return ResultVO.fail("信息不能为空");
//        }
//
//        DstConfigData.USER_INFO.setNickname(vo.getPicture());
//        DstConfigData.USER_INFO.setUsername(vo.getUsername());
//        DstConfigData.USER_INFO.setPicture(vo.getPicture());
//        //退出登录
//        return ResultVO.success("修改成功");
//    }


    /**
     * 获取用户头像
     */
    @GetMapping("/picture")
    public void picture(String userName, HttpServletResponse response) throws IOException {
        String defaultPath = "/images/user-picture.jpg";
        Resource resource = new ClassPathResource("static" + defaultPath);
        FileCopyUtils.copy(resource.getInputStream(), response.getOutputStream());
    }


}
