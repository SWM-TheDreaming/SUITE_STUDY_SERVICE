package com.suite.suite_study_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoAuditing
@SpringBootApplication
@EnableJpaRepositories(basePackages = {
        "com.suite.suite_study_service.dashboard",
        "com.suite.suite_study_service.mission"
})
@EnableMongoRepositories(basePackages = "com.suite.suite_study_service.attendance")
public class SuiteStudyServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SuiteStudyServiceApplication.class, args);
    }

}
