package com.example.controller;


import com.example.dao.SysUserMapper;
import com.example.entity.SysUser;
import com.example.service.SysUserService;
import com.example.utill.DownloadUtils;
import com.example.utill.R;
import com.example.vo.UserModel;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * <p>
 * 系统用户 前端控制器
 * </p>
 *
 * @author zxs
 * @since 2020-03-12
 */
@RestController
@RequestMapping("/sys")
public class SysUserController {
    @Autowired
   private SysUserService sysUserService;
    @Autowired
    private SysUserMapper sysusersao;

   @RequestMapping("allu")
public R findall(){
       Map map=new HashMap();
       map.put("user",sysUserService.list());
       return R.ok(map);
}

    /**
     * poi导入excel进行批量添加用户
     * @param attachment
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public String importDatas(@RequestParam(name = "file") MultipartFile attachment) throws Exception {
        //1.根据Excel文件创建工作簿
        Workbook wb = new XSSFWorkbook(attachment.getInputStream());
        //存放用户集合
        List<SysUser> users=new ArrayList();
        //2.获取Sheet//参数：索引
        Sheet sheet = wb.getSheetAt(0);
        //3.获取Sheet中的每一行，和每一个单元格
        for (int rowNum = 1; rowNum<= sheet.getLastRowNum() ;rowNum ++) {
            //根据索引获取每一个行
            Row row = sheet.getRow(rowNum);
            //创建对象数组
            Object [] valuse=new Object [row.getLastCellNum()];
            //StringBuilder sb = new StringBuilder();
            for(int cellNum=2;cellNum< row.getLastCellNum(); cellNum ++) {
                //根据索引获取每一个单元格
                Cell cell = row.getCell(cellNum);
                //获取每一个单元格的内容
                Object value = getCellValue(cell);
                valuse [cellNum] = value;
                //sb.append(value).append("-");

            }
            //一行一个用户对象
            SysUser user = new SysUser(valuse);
            users.add(user);
            //System.out.println(sb.toString());
        }
       //批量保存
        System.out.println(users);
        sysUserService.saveBatch(users);
        return "上传ok";
    }

    public  Object getCellValue(Cell cell) {
        //1.获取到单元格的属性类型
        CellType cellType = cell.getCellType();
        //2.根据单元格数据类型获取数据
        Object value = null;
        switch (cellType) {
            case STRING:
                value = cell.getStringCellValue();
                break;
            case BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case NUMERIC:
                if(DateUtil.isCellDateFormatted(cell)) {

                    value = cell.getDateCellValue();
                }else{

                    value = cell.getNumericCellValue();
                }
                break;
            case FORMULA:
                value = cell.getCellFormula();
                break;
            default:
                break;
        }
        return value;
    }

    /**
     * 保存用户
     */

    @PostMapping("/save")
    public R save(@RequestBody SysUser user){
        sysUserService.save(user);
        return R.ok();
    }

    /**
     获取权限
     * @return
     */
    @GetMapping("queryPer")
   @RequiresPermissions("sys:schedule:save")
    public List<String> queryPer(){
       return sysusersao.queryAllPerms((long) 5);
    }

@GetMapping("export")
    public void export(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //构造数据
    List<UserModel> users = sysUserService.queryExcel();
    //创建工作簿
    XSSFWorkbook workBook = new XSSFWorkbook();
    //Workbook wb = new XSSFWorkbook();
    //构造sheet
    XSSFSheet sheet = workBook.createSheet();
    //构造sheet
    String [] titles="姓名,密码,状态,手机号,邮箱,入职时期".split(",");
   //构造excel标题
    //第一行标题
    Row row = sheet.createRow(0);
    int titleIndex=0;
    for (String title : titles) {
        Cell cell = row.createCell(titleIndex++);
        cell.setCellValue(title);
    }

    //将user插入其他行
    int rowIndex=1;
    Cell cell=null;
    for (UserModel user : users) {
         row = sheet.createRow(rowIndex++);
        //用户名
         cell = row.createCell(0);
        cell.setCellValue(user.getUsername());
        //密码
        cell = row.createCell(1);
        cell.setCellValue(user.getPassword());
        //状态
        cell = row.createCell(2);
        cell.setCellValue(user.getStatus());
        //手机号
        cell = row.createCell(3);
        cell.setCellValue(user.getMobile());
        //邮箱
        cell = row.createCell(4);
        cell.setCellValue(user.getEmail());
        //创建时间
        cell = row.createCell(5);
       // cell.setCellValue( Date.from(user.getCreateTime().atZone(ZoneId.systemDefault()).toInstant()));
        cell.setCellValue(user.getCreateTime().toString());
    }
    //3.完成下载
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    workBook.write(os);
    new DownloadUtils().download(os,response,"人事报表.xlsx");


}

/**
 * 采用模板打印的形式完成报表生成
 *      模板
 *  参数：
 *      年月-月（2018-02%）
 *
 *      sxssf对象不支持模板打印
 */
@GetMapping("exportModel")
public  void exportByModel(HttpServletRequest request, HttpServletResponse response) throws Exception{
    //构造数据
    List<UserModel> users = sysUserService.queryExcel();
    //加载模板(改动1)
       Resource resource = new ClassPathResource("excel-template/userModel.xlsx");
       FileInputStream fis = new FileInputStream(resource.getFile());
    //创建工作簿(改动2)
    XSSFWorkbook workBook = new XSSFWorkbook(fis);
    //XSSFWorkbook wb = new XSSFWorkbook();
    //获取当前sheet(改动3)
    XSSFSheet sheet = workBook.getSheetAt(0);
    //构造sheet
    //String [] titles="姓名,密码,状态,手机号,邮箱,入职时期".split(",");
    //构造excel标题
    //第一行标题
    /*Row row = sheet.createRow(0);
    int titleIndex=0;
    for (String title : titles) {
        Cell cell = row.createCell(titleIndex++);
        cell.setCellValue(title);
    }*/
    //抽取公共样式(改动4)
       Row row = sheet.getRow(2);
       CellStyle styles [] = new CellStyle[row.getLastCellNum()];
       for(int i=0;i<row.getLastCellNum();i++) {
           Cell cell = row.getCell(i);
          styles[i] = cell.getCellStyle();
       }

    //将user插入其他行(改动5:数据插入从第三行开始)
    int rowIndex=2;
    Cell cell=null;
    for (UserModel user : users) {
        row = sheet.createRow(rowIndex++);
        //用户名(改动6:数据添加模板样式)
        cell = row.createCell(0);
        cell.setCellValue(user.getUsername());
        cell.setCellStyle(styles[0]);
        //密码
        cell = row.createCell(1);
        cell.setCellValue(user.getPassword());
        cell.setCellStyle(styles[1]);
        //状态
        cell = row.createCell(2);
        cell.setCellValue(user.getStatus());
        cell.setCellStyle(styles[2]);
        //手机号
        cell = row.createCell(3);
        cell.setCellValue(user.getMobile());
        cell.setCellStyle(styles[3]);
        //邮箱
        cell = row.createCell(4);
        cell.setCellValue(user.getEmail());
        cell.setCellStyle(styles[4]);
        //创建时间
        cell = row.createCell(5);
        // cell.setCellValue( Date.from(user.getCreateTime().atZone(ZoneId.systemDefault()).toInstant()));
        cell.setCellValue(user.getCreateTime().toString());
        cell.setCellStyle(styles[5]);
    }
    //3.完成下载
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    workBook.write(os);
    new DownloadUtils().download(os,response,"模板人事报表.xlsx");
}
}

