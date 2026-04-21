package com.it;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 描述:<主启动类,系统程序入口,起始><br/>
 */
@SpringBootApplication
@ServletComponentScan//扫描@WebFilter、@WebServlet等组件
@EnableScheduling
public class CaseJopApplication {

    public static void main(String[] args) {
        SpringApplication.run(CaseJopApplication.class, args);
    }
}
