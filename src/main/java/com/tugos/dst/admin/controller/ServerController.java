package com.tugos.dst.admin.controller;


import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/server")
public class ServerController {

    @GetMapping("/index")
    @RequiresAuthentication
    public String index() {
        return "/server/index";
    }


}

