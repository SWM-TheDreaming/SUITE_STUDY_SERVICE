package com.suite.suite_study_service.attendance.repository;

import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;
import org.bson.Document;

public class MongoOperation implements AggregationOperation {
    private final Document operation;

    public MongoOperation(Document operation) {
        this.operation = operation;
    }

    @Override
    public Document toDocument(AggregationOperationContext context) {
        return this.operation;
    }
}
