package com.example.dao;

import com.example.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.vo.UserModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 系统用户 Mapper 接口
 * </p>
 *
 * @author zxs
 * @since 2020-03-12
 */
@Repository
public interface SysUserMapper extends BaseMapper<SysUser> {
 SysUser getUserById();
 /**
  * 查询用户的所有权限
  * @param userId  用户ID
  */

 List<String> queryAllPerms(Long userId);

 List<UserModel> queryExcel();
}
