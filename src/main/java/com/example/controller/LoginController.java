package com.example.controller;

import com.example.entity.SysUser;
import com.example.utill.R;
import com.example.utill.ShiroUtils;
import com.google.code.kaptcha.Constants;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zxs
 */
@RestController
public class LoginController {
    @PostMapping("/login")
    public R login(SysUser user, HttpServletRequest httpServletRequest){
        String kaptcha = ShiroUtils.getKaptcha(Constants.KAPTCHA_SESSION_KEY);
        if(!user.getYzm().equalsIgnoreCase(kaptcha)){
            return R.error("验证码不正确");
        }
        try{
            Subject subject = ShiroUtils.getSubject();
            UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), user.getPassword());
            subject.login(token);
            System.out.println(subject.getSession().getId().toString());
        }catch (UnknownAccountException e) {
            return R.error(e.getMessage());
        }catch (IncorrectCredentialsException e) {
            return R.error("账号或密码不正确");
        }catch (LockedAccountException e) {
            return R.error("账号已被锁定,请联系管理员");
        }catch (AuthenticationException e) {
            return R.error("账户验证失败");
        }

        return R.ok();
    }

    @RequestMapping("/autherror")
    public String name(String code) {
        if(code.equals("1")) {
            return "未登录";
        }else if(code.equals("2")) {
            return "无权限";
        }
        return "未知状态";
    }

    /**
     * 退出
     * @return
     */
    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public R logout() {
        ShiroUtils.logout();
        return R.ok("退出登入成功");
    }
}
