package com.minejava.kafkastreams.subjectservice.repository;

import com.minejava.kafkastreams.subjectservice.entity.SubjectModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SubjectModelRepository extends CrudRepository<SubjectModel, Long> {

	@Transactional(readOnly = true)
	List<SubjectModel> findById(long id);
}
