package com.example.controller;


import com.example.service.SysDeptService;
import com.example.utill.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  * 部门管理 前端控制器
 *  * </p>
 *
 * @author zxs
 * @since 2020-03-13
 */
@RestController
@RequestMapping("/sys-dept")
public class SysDeptController {
    @Autowired
   private SysDeptService sysDeptService;
    @GetMapping("all")
    public R getall(){
        Map map=new HashMap();
        map.put("data",sysDeptService.list());
        return R.ok(map);
    }

}

