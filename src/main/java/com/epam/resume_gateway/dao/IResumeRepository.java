package com.epam.resume_gateway.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface IResumeRepository extends MongoRepository<Resume, String> {
}
