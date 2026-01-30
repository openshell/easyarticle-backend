package com.easyarticle.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
* 
*@author caiqingzhou
*@since 2026/01/30  14:59
*/
@Configuration
public class SpringDocConfig {
    /**
     * 配置OpenAPI信息
     * @return OpenAPI实例
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("EasyArticle API")
                        .version("1.0")
                        .description("EasyArticle后端API文档"));
    }
}