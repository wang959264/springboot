package com.hello;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@Slf4j
@RestController
public class SpringbootApplication {
	public static void main(String[] args) {
		SpringApplication.run(SpringbootApplication.class, args);
	}

	@RequestMapping("/hello")
	public String index(){
		return "hello 我的！";
	}


	/**
	 * 多数据源配置:
	 * 		在@SpringBootApplication上添加
	 * 		(exclude = { DataSourceAutoConfiguration.class,
	 * 		DataSourceTransactionManagerAutoConfiguration.class, JdbcTemplateAutoConfiguration.class})
	 * 		以排除SpringBoot对数据源的自动配置。
	 */
	/**
	 * ConfigurationProperties注解将配置文件application.properties中的属性映射到对象中；
	 * 		若不指定则默认以server开头的属性。
	 * @return foo数据源

	@Bean
	@ConfigurationProperties("foo.datasource")
	public DataSourceProperties fooDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	public DataSource fooDataSource() {
		DataSourceProperties dataSourceProperties = fooDataSourceProperties();
		log.info("foo datasource: {}", dataSourceProperties.getUrl());
		return dataSourceProperties.initializeDataSourceBuilder().build();
	}
	 */
	/**
	 * Spring事物管理
	 * @param fooDataSource
	 * @return

	@Bean
	@Resource
	public PlatformTransactionManager fooTxManager(DataSource fooDataSource) {
		return new DataSourceTransactionManager(fooDataSource);
	}

	@Bean
	@ConfigurationProperties("bar.datasource")
	public DataSourceProperties barDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	public DataSource barDataSource() {
		DataSourceProperties dataSourceProperties = barDataSourceProperties();
		log.info("bar datasource: {}", dataSourceProperties.getUrl());
		return dataSourceProperties.initializeDataSourceBuilder().build();
	}

	@Bean
	@Resource
	public PlatformTransactionManager barTxManager(DataSource barDataSource) {
		return new DataSourceTransactionManager(barDataSource);
	}
	 */
}
