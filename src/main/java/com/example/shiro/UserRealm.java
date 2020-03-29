package com.example.shiro;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.example.dao.SysMenuMapper;
import com.example.dao.SysUserMapper;
import com.example.entity.SysMenu;
import com.example.entity.SysUser;
import com.example.utill.Constant;
import com.example.utill.ShiroUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * @author zxs
 */
public class UserRealm  extends AuthorizingRealm {
    @Autowired
    private SysUserMapper sysUserDao;
    @Autowired
    private SysMenuMapper sysMenuDao;

    @Override
    public void setName(String name) {
        super.setName("userRealm");
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
      SysUser user= (SysUser) principals.getPrimaryPrincipal();
        Long userId = user.getUserId();
        //存放权限
        List<String> permsList;

        //系统管理员，拥有最高权限
        if(userId == Constant.SUPER_ADMIN){
            List<SysMenu> menuList = sysMenuDao.selectList(null);
            permsList = new ArrayList<>(menuList.size());
            for(SysMenu menu : menuList){
                permsList.add(menu.getPerms());
            }
        }else{
            permsList = sysUserDao.queryAllPerms(userId);
        }

        //用户权限列表
        Set<String> permsSet = new HashSet<>();
        for(String perms : permsList){
            if(StringUtils.isBlank(perms)){
                continue;
            }
            permsSet.addAll(Arrays.asList(perms.trim().split(",")));
        }

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setStringPermissions(permsSet);
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken)authcToken;

        SysUser user = sysUserDao.selectOne(new Wrapper<SysUser>() {
            @Override
            public SysUser getEntity() {
                return new SysUser(token.getUsername());
            }

            @Override
            public MergeSegments getExpression() {
                return null;
            }

            @Override
            public String getCustomSqlSegment() {
                return null;
            }

            @Override
            public String getSqlSegment() {
                return null;
            }
        });
        //账号不存在
        if(user == null) {
            throw new UnknownAccountException("账号或密码不正确");
        }

        //账号锁定
        if(user.getStatus() == 0){
            throw new LockedAccountException("账号已被锁定,请联系管理员");
        }

        /**
         * 第一个参数，有的人传的是userInfo对象对用的用户名。在学习过程中，传入的都是user对象，没有尝试过对象对应的用户名，
         * 但是从前辈们的经验看得到，此处也可以传用户名，因人而异吧。
         *
         * 第二个参数，传的是从数据库中获取的password，然后再与token中的password进行对比，匹配上了就通过，匹配不上就报异常。
         *
         * 第三个参数，盐–用于加密密码对比，–获取的经验：为了防止两用户的初始密码是一样的，
         * –巨佬们的解答：四个参数，防止两用户可能初始密码相同时候用，token 用simplehash（四个参数的构造） 加密默认用了MD5
         * 迭代一次加密，info中在密码比对调用new SimpleHash(String algorithmName, Object source）这个实例化对象默认迭代一次了，
         * 所以当你用三个参数加密时候可能两 个初始密码相同人的就没能区别开 （因此realm中密码要从数据库的查的原因），通过设置reaml 中credentialsMatcher
         * 属性的各项属性可实现
         *
         *第四个参数：当前realm的名字。
         */
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, user.getPassword(), ByteSource.Util.bytes(user.getSalt()), getName());
        return info;
    }

    @Override
    public void setCredentialsMatcher(CredentialsMatcher credentialsMatcher) {
        HashedCredentialsMatcher shaCredentialsMatcher = new HashedCredentialsMatcher();
        shaCredentialsMatcher.setHashAlgorithmName(ShiroUtils.hashAlgorithmName);
        shaCredentialsMatcher.setHashIterations(ShiroUtils.hashIterations);
        super.setCredentialsMatcher(shaCredentialsMatcher);
    }
}
