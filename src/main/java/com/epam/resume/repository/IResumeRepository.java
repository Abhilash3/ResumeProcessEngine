package com.epam.resume.repository;

import com.epam.resume.Resume;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IResumeRepository extends MongoRepository<Resume, String> {
}
