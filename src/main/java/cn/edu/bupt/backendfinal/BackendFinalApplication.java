package cn.edu.bupt.backendfinal;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("cn.edu.bupt.backendfinal.mapper")
@ComponentScan("cn.edu.bupt.backendfinal.controller")
@ComponentScan("cn.edu.bupt.backendfinal.services")
public class BackendFinalApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendFinalApplication.class, args);
	}

}
