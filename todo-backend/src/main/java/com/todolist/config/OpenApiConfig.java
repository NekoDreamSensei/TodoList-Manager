package com.todolist.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 3 (Swagger 3) 配置类
 * 
 * 配置API文档的生成和显示
 * 
 * @author todolist
 * @version 1.0
 * @since 2025-09-12
 */
@Configuration
public class OpenApiConfig {

    /**
     * 配置OpenAPI信息
     * 
     * @return OpenAPI配置对象
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("待办事项管理系统 API")
                        .description("一个完整的待办事项管理系统，支持用户管理、主题分类、任务管理和待办事项跟踪")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("TodoList Team")
                                .email("contact@todolist.com")
                                .url("https://github.com/todolist"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("开发环境"),
                        new Server()
                                .url("https://api.todolist.com")
                                .description("生产环境")
                ));
    }
} 