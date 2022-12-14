package com.pintertamas.befake.notificationservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:config.properties")
public class CustomPropertyConfig {

  @Value("${mailFrom}")
  public String mailFrom;

  @Value("${awsAccessKey}")
  public String awsAccessKey;

  @Value("${awsSecretKey}")
  public String awsSecretKey;

  @Value("${app.firebase-configuration-file}")
  public String firebaseConfigFile;
}