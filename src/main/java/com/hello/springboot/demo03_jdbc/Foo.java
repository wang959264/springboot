package com.hello.springboot.demo03_jdbc;

import lombok.Builder;
import lombok.Data;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@Data
@Builder
@EntityScan
public class Foo {
    private String bar;
    private Long id;
}
