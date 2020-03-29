package com.example.utill;



import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;

import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import org.junit.Test;

/**
 * @author zxs
 */
public class TestMP {
	
	
	/**
	 * 代码生成    示例代码D:\IdeaProjects\demo
	 */
	@Test
	public void  testGenerator() {
		//1. 全局配置
		GlobalConfig config = new GlobalConfig();
		config.setActiveRecord(false)
			  .setAuthor("zxs")
			  .setOutputDir("D:\\IdeaProjects\\demo\\src\\main\\java")
			  .setFileOverride(true)
			  .setIdType(IdType.AUTO)
			  .setServiceName("%sService")

 			  .setBaseResultMap(true)
 			  .setBaseColumnList(true);
		
		//2. 数据源配置
		DataSourceConfig  dsConfig  = new DataSourceConfig();
		dsConfig.setDbType(DbType.MYSQL)
				.setDriverName("com.mysql.jdbc.Driver")
				.setUrl("jdbc:mysql://127.0.0.1:3306/test?serverTimezone=UTC")
				.setUsername("root")
				.setPassword("root");
		 
		//3. 策略配置
		StrategyConfig stConfig = new StrategyConfig();
		stConfig.setCapitalMode(true)
				//.setDbColumnUnderline(true)
		         .setColumnNaming(NamingStrategy.underline_to_camel)
				.setNaming(NamingStrategy.underline_to_camel)
				//.setTablePrefix("tbl_")
				.setEntityLombokModel(true)
				.setControllerMappingHyphenStyle(true)
				.setInclude("sys_role");
		
		//4. 包名策略配置 
		PackageConfig pkConfig = new PackageConfig();
		pkConfig.setParent("com.example")
				.setMapper("dao")
				.setService("service")
				.setController("controller")
				.setEntity("entity")
				.setXml("mapper");
		
		//5. 整合配置
		AutoGenerator  ag = new AutoGenerator();
		
		ag.setGlobalConfig(config)
		  .setDataSource(dsConfig)
		  .setStrategy(stConfig)
		  .setPackageInfo(pkConfig);
		
		//6. 执行
		ag.execute();
	}
	
	
	
}
