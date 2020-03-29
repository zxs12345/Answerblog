package com.example.service;

import com.example.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.vo.UserModel;

import java.util.List;

/**
 * <p>
 * 系统用户 服务类
 * </p>
 *
 * @author zxs
 * @since 2020-03-12
 */
public interface SysUserService extends IService<SysUser> {
    SysUser getUserById();
    /**
     * 保存用户
     * @return
     */
    boolean save(SysUser user);


    List<UserModel> queryExcel();
}
