package com.example.service.impl;

import com.example.entity.SysUser;
import com.example.dao.SysUserMapper;
import com.example.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.utill.ShiroUtils;
import com.example.vo.UserModel;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 系统用户 服务实现类
 * </p>
 *
 * @author zxs
 * @since 2020-03-12
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
@Autowired
SysUserMapper sysmapper;
    @Override
    public SysUser getUserById() {
        return sysmapper.getUserById();
    }

    @Override
    public boolean save(SysUser user) {
        //user.setCreateTime(new Date());
        //sha256加密
        String salt = RandomStringUtils.randomAlphanumeric(20);
        user.setSalt(salt);
        user.setPassword(ShiroUtils.sha256(user.getPassword(), user.getSalt()));
        //保存用户与角色关系
        //sysUserRoleService.saveOrUpdate(user.getUserId(), user.getRoleIdList());
        return super.save(user);
    }

    @Override
    public List<UserModel> queryExcel() {
        return sysmapper.queryExcel();
    }
}
