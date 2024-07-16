package com.tugos.dst.admin.config.shiro;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.shiro.authc.UsernamePasswordToken;


public class LoginToken extends UsernamePasswordToken {
    private String timestamp;

    public LoginToken() {
        super();
    }

    public LoginToken(String username, char[] password, String timestamp) {
        super(username, password);
        this.timestamp = timestamp;
    }

    public LoginToken(String username, String password, String timestamp) {
        super(username, password);
        this.timestamp = timestamp;
    }

    public LoginToken(String username, String password, String host,String timestamp) {
        super(username, password,host);
        this.timestamp = timestamp;
    }

    public LoginToken(String username, char[] password, boolean rememberMe, String host, String timestamp) {
        super(username, password, rememberMe, host);
        this.timestamp = timestamp;
    }

    public LoginToken(String username, String password, boolean rememberMe, String host, String timestamp) {
        super(username, password, rememberMe, host);
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public void clear() {
        super.clear();
        this.timestamp = null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append(", timestamp=").append(this.timestamp);
        return sb.toString();
    }
}
