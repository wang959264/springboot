package com.hello.springboot.demo01_dataSource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 单数据源配置
 * CommandLineRunner接口：
 *      SpringBoot项目启动完毕后立即执行的接口，与@Component注解共同使用生效，有且只执行一次；
 *      用@Order（value = 1)保证执行顺序，初始值从1开始。
 * Component注解：
 *      将普通的pojo类实例化到Spring容器中，等同于配置文件中<bean id = "" class = "" />
 *
 */

@Component
@Slf4j
@Order (value = 1)
public class dS01  implements CommandLineRunner{

    /**
     * 多数据源情况下DataSource和JdbcTemplate不可用
     */
    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("CommandLineRunner --------------->  Order 1");
        showDataSource();
        showData();
    }

    private void showDataSource() throws SQLException {
        Connection conn = dataSource.getConnection();
        log.info(dataSource.toString());
        log.info(conn.toString());
        conn.close();
    }

    private void showData() {
        jdbcTemplate.queryForList("SELECT * FROM FOO")
                .forEach(row ->log.info(row.toString()));
    }
}
