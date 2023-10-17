package com.suite.suite_study_service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@EnableMongoAuditing
@EnableJpaAuditing
@SpringBootApplication
@EnableJpaRepositories(basePackages = {
        "com.suite.suite_study_service.dashboard",
        "com.suite.suite_study_service.mission"
})
@EnableMongoRepositories(basePackages = "com.suite.suite_study_service.attendance")
public class SuiteStudyServiceApplication {

    @PersistenceContext
    private EntityManager entityManager;

    public static void main(String[] args) {
        SpringApplication.run(SuiteStudyServiceApplication.class, args);
    }

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }




}
