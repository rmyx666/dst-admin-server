package com.tugos.dst.admin.config.shiro;


import cn.hutool.crypto.digest.DigestUtil;
import com.tugos.dst.admin.entity.User;
import com.tugos.dst.admin.service.DataService;
import lombok.extern.log4j.Log4j2;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author qinming
 * @date 2020-5-16
 * <p> 自定义身份校验 </p>
 */
@Component
@Log4j2
public class AuthRealm extends AuthorizingRealm {

    @Autowired
    DataService dataService;

    /**
     * 授权逻辑
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principal) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        // 管理员拥有所有权限
        info.addRole("admin");
        info.addStringPermission("*:*:*");
        return info;
    }

    /**
     * 认证逻辑
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        LoginToken token = (LoginToken) authenticationToken;
        User user = new User();
        user.setUsername(token.getUsername());
        user.setPassword(String.valueOf(token.getPassword()));
        return new SimpleAuthenticationInfo(user, user.getPassword(), getName());
    }

    /**
     * 自定义密码验证匹配器
     */
    @PostConstruct
    public void initCredentialsMatcher() {
        setCredentialsMatcher(new SimpleCredentialsMatcher() {
            @Override
            public boolean doCredentialsMatch(AuthenticationToken authenticationToken, AuthenticationInfo authenticationInfo) {
                LoginToken token = (LoginToken) authenticationToken;
                SimpleAuthenticationInfo info = (SimpleAuthenticationInfo) authenticationInfo;
                // 获取明文密码及密码盐
                String password = String.valueOf(token.getPassword());
                String username = token.getUsername();
                String timestamp = token.getTimestamp();
                String host = token.getHost();

                if (DigestUtil.md5Hex("user" + dataService.getUser().getUsername() + timestamp).equals(username)
                        && DigestUtil.md5Hex("password" + dataService.getUser().getPassword() + timestamp).equals(password)) {
                    log.info(host+"登录成功");
                    return true;
                }
                log.info(host+"登陆失败");
                return false;
            }
        });
    }
}
