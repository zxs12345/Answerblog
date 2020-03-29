package com.example.config;

import com.example.shiro.UserRealm;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.shiro.mgt.SecurityManager;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author zxs
 */
@Configuration
public class ShiroConfig {
    /**
     * 获取bean自定义realm
     * @return
     */
    @Bean
public UserRealm getUserRealm(){
    return new UserRealm();
}
    //2.创建安全管理器
    @Bean
    public SecurityManager getSecurityManager(UserRealm userRealm) {
        // TODO Auto-generated method stub
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(userRealm);
        return securityManager;
    }
    //3.配置shiro的过滤器工厂

    /**
     * 再web程序中，shiro进行权限控制全部是通过一组过滤器集合进行控制
     * @return
     *
     */
    @Bean
    public ShiroFilterFactoryBean getShiroFilter(SecurityManager securityManager) {
        //1.创建过滤器工厂
        ShiroFilterFactoryBean filterFactory = new ShiroFilterFactoryBean();
        //2.设置安全管理器
        filterFactory.setSecurityManager(securityManager);
        //3.通用配置（跳转登录页面，为授权跳转的页面）
        filterFactory.setLoginUrl("/autherror?code=1");
        filterFactory.setUnauthorizedUrl("/autherror?code=2");
      //需要放行的路径
        Map<String,String> filterMap = new LinkedHashMap<>();
        filterMap.put("/login","anon");
        filterMap.put("/autherror","anon");
        filterMap.put("/defaultKaptcha","anon");

        //filterMap.put("/sys/queryPer","perms[point-setting-update]");
       // filterMap.put("/**","authc");

        filterFactory.setFilterChainDefinitionMap(filterMap);
        return filterFactory;
    }
    //开启对shior注解的支持
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }
}
