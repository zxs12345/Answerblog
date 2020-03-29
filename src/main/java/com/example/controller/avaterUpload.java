package com.example.controller;

import com.example.utill.QiniuUploadUtil;
import com.example.utill.R;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.apache.commons.codec.binary.Base64;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class avaterUpload {
    //@RequestMapping("/upload/{id}")
    public R upload(@PathVariable String id, @RequestParam(name="file") MultipartFile file ) throws IOException {
        //1.调用service保存图片（获取到图片的访问地址（dataUrl | http地址））
       // String imgUrl = userService.uploadImage(id,file);
        //2.返回数据
       // return new Result(ResultCode.SUCCESS,imgUrl);
        String encode = "data:image/png;base64,"+Base64.encodeBase64String(file.getBytes());
        System.out.println("图片==========="+encode);
        return R.ok(encode);
    }

    /**
     * 使用七牛云存储文件
     * @param id 对应上传的key
     * @param file  转文件流上传到七牛云
     * @return
     * @throws IOException
     */
    @RequestMapping("/upload/{id}")
public R qiNiuYunUpload(@PathVariable String id, @RequestParam(name="file") MultipartFile file) throws IOException {
        QiniuUploadUtil qin = new QiniuUploadUtil();
             //1.根据id查询用户
            //User user = userDao.findById(id).get();
        // String imgUrl = qin.upload(user.getId(), file.getBytes());
        String imgUrl = qin.upload(id, file.getBytes());
       //3.更新用户头像地址，StaffPhoto数据库中可不使用mediumtext
        //user.setStaffPhoto(imgUrl);
        return R.ok(imgUrl);
}
    /**
     * 完成图片处理
     * @param id        ：用户id
     * @param file      ：用户上传的头像文件
     * @return          ：请求路径
     */
//    public String uploadImage(String id, MultipartFile file) throws IOException {
//        //1.根据id查询用户
//        User user = userDao.findById(id).get();
//        //2.使用DataURL的形式存储图片（对图片byte数组进行base64编码）
//        String encode = "data:image/png;base64,"+Base64.encode(file.getBytes());
//        System.out.println(encode);
//        //3.更新用户头像地址，StaffPhoto数据库中使用mediumtext
//        user.setStaffPhoto(encode);
//        userDao.save(user);
//        //4.返回
//        return encode;
//    }
}
