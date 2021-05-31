package com.chenk.qiniu.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author: chenke
 * @since: 2021/5/31
 */
@Data
@Configuration
public class CloudStorageConfig {

    @Value("${qiniu.config.ACCESS_KEY}")
    private String ACCESS_KEY;
    @Value("${qiniu.config.SECRET_KEY}")
    private String SECRET_KEY;
    @Value("${qiniu.config.bucketName}")
    private String bucketName;
    @Value("${qiniu.config.domain}")
    private String domain;
}
