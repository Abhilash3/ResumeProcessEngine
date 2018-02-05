package com.epam.resume.gateway;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface IResumeRepository extends MongoRepository<Resume, String> {
}
