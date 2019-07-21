package com.hello.springboot.demo03_jdbc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * JdbcTemplate : JdbcTemplate是Spring提供的一个类，是对数据库的操作进行了封装，处理了资源的建立和释放，
 *      我们只需要提供SQL语句和提取结果。使用Spring的注入功能，把DataSource注入到JDBCTemplate中，且该类是线程安全的。
 *      execute方法：可以用于执行任何SQL，一般用于执行DDL（create、alter、drop、truncate）语句；
 *      update方法：用于执行增、删、改等语句，返回一个int值，影响的行数；
 *      batchUpdate：用于执行批处理相关语句；
 *      query相关方法：查询多行数据；
 *          queryForObject(Sql,数据类型.class)方法：查询单个对象；
 *          queryForOBject(Sql,new BeanPropertyRowMapper(类型),参数):查询单个对象，返回一个实体类对象；
 *          queryForMap方法：查询单个对象，返回一个Map对象；
 *          queryForList方法：查询多个对象，返回一个list对象，list对象存储的是Map对象；
 *      call方法：执行存储过程、函数等相关语句；
 */
@Slf4j
@Repository
@Order (value = 2)
public class FooDao implements CommandLineRunner{

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private SimpleJdbcInsert simpleJdbcInsert;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    /** NamedParameterJdbcTemplate 初始化
     *  NamedParameterJdbcTemplate：
     *         此类拓展了JdbcTemplate类，对JdbcTemplate类进行了封装从而支持命名参数特性；
     *         即用冒号(:) 加变量名的方式作为替代符,而JdbcTemplate是用问号(?)来作为参数替代符。
     *         主要提供以下三类方法：execute方法、query及queryForXXX方法、update及batchUpdate方法。
     * @param dataSource
     * @return
     */
    @Bean
    @Autowired
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }


    /**SimpleJdbcInsert 是SpringJDBC提供的一个简化插入操作的类，常用api如下：
     *      withTableName绑定需要操作的的数据库表；
     *      usingGeneratedKeyColumns指定主键；
     *      executeAndReturnKey执行并返回主键，且返回类型为Number；
     *      usingColumns限制插入数据的列
     *
     *      初始化并指定表名和主键
     * @param jdbcTemplate
     * @return
     */
    @Bean
    @Autowired
    public SimpleJdbcInsert simpleJdbcInsert(JdbcTemplate jdbcTemplate) {
        return new SimpleJdbcInsert(jdbcTemplate).withTableName("FOO").usingGeneratedKeyColumns("ID");
    }


    @Override
    public void run(String... args) throws Exception {
        insertData();
        batchInsert();
        listData();
    }

    public void batchInsert() {

        log.info("jdbcTemplate 批处理用法");
        //对于同一结构的带参Sql语句多次进行数据更新操作，通过BatchPreparedStatementSetter回调接口进行批量参数的绑定工作
        jdbcTemplate.batchUpdate("INSERT INTO FOO (BAR) VALUES ( ? )", new BatchPreparedStatementSetter() {
            //为给定的PreparedStatement设置参数
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1,"b-" + i);
            }
            //获取批次的数据大小
            @Override
            public int getBatchSize() {
                return 2;
            }
        });


        log.info("namedParameterJdbcTemplate 批处理用法");
        List<Foo> list = new ArrayList <>();
        list.add(Foo.builder().id(100L).bar("bar-100").build());
        list.add(Foo.builder().id(101L).bar("bar-101").build());

        namedParameterJdbcTemplate.batchUpdate("INSERT INTO FOO (ID, BAR) VALUES (:id, :bar)",
                SqlParameterSourceUtils.createBatch(list));
    }


    public void insertData() {
        List <String> ls = Arrays.asList("b","c");
        ls.forEach(bar ->{
            System.out.println(bar);
            jdbcTemplate.update("INSERT INTO FOO (BAR) VALUES (?)", bar);
        });

        HashMap<String, String> row = new HashMap<>();
        row.put("BAR", "d");
        Number id = simpleJdbcInsert.executeAndReturnKey(row);
        log.info("ID of d: {}", id.longValue());
    }

    public void listData() {
        log.info("COUNT : {}", jdbcTemplate.queryForObject("SELECT COUNT(1) FROM FOO", Long.class));
        List<String> list = jdbcTemplate.queryForList("SELECT bar FROM FOO", String.class);
        list.forEach(s ->log.info("list : {}", s));

        List<Foo> fooList = jdbcTemplate.query("SELECT * FROM FOO", new RowMapper <Foo>() {
            @Override
            public Foo mapRow(ResultSet rs, int rowNum) throws SQLException {
                return Foo.builder().id(rs.getLong(1)).bar(rs.getString(2)).build();
            }
        });
    }





}
