package com.hello.springboot.demo04_transaction;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**Spring事物
 *      Spring支持声明式事务和编程式事务，其提供的最原始事务管理方式是基于TransactionDefinition、
 *      PlatFormTransactionManager、TransactionStatus编程式事务；
 *      而TransactionTemplate的编程式事务管理 是使用模板方法设计模式对原始事务管理方式的封装。
 */
@Component
@Slf4j
@Order(value = 3)
public class TransactionDemo implements CommandLineRunner {

    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private FooService fooService;

    @Override
    public void run(String... args) throws Exception {
        programming();
        statement();
    }

    private long getCount() {
        return (long) jdbcTemplate.queryForList("SELECT COUNT(1) AS CNT FROM FOO" ).get(0).get("CNT");
    }

    //编程式事物
    private void programming () {
        log.info("COUNT BEFORE TRANSACTION:{} " + getCount());
        /**transactionTemplate.execute()参数有两种选择：
         *      TransactionCallBack 有返回值
         *      TransactionCallBackWithOutResult 无返回值
         *      相关参数设置：
         *      //设置事务传播属性
         *      transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
         *      // 设置事务的隔离级别,设置为读已提交（默认是ISOLATION_DEFAULT:使用的是底层数据库的默认的隔离级别）
         *      transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
         *      // 设置是否只读，默认是false
         *      transactionTemplate.setReadOnly(true);
         *      // 默认使用的是数据库底层的默认的事务的超时时间
         *      transactionTemplate.setTimeout(30000);
         */
        transactionTemplate.execute(new TransactionCallbackWithoutResult(){
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                jdbcTemplate.execute("INSERT INTO FOO (BAR) VALUES ('abc' )");
                log.info("COUNT IN TRANSACTION:{} " + getCount());
                //回滚
                status.setRollbackOnly();
            }
        });
        log.info("COUNT AFTER TRANSACTION : {} " + getCount());
    }


    //声明式事物
    public void statement() {
        fooService.insertRecord();
        log.info("AAA:{}",
                jdbcTemplate.queryForObject("SELECT COUNT(1) FROM FOO WHERE BAR = 'AAA'", Long.class));

        try {
            fooService.insertThenRollback();
        } catch (RollbackException e) {
            log.info("BBB:{}",
                    jdbcTemplate.queryForObject("SELECT COUNT(1) FROM FOO WHERE BAR = 'BBB'", Long.class));
        }

        try {
            fooService.invokeInsertThenRollback();
        } catch (RollbackException e) {
            log.info("BBB:{}",
                    jdbcTemplate.queryForObject("SELECT COUNT(1) FROM FOO WHERE BAR = 'BBB'", Long.class));
        }

    }
}
