package com.minejava.kafkastreams.dto;

import java.util.List;

public record UserserviceComag(String id,
							   String username,
							   String email,
							   String name,
							   String serviceAddress,
                               List<SubjectServiceComag> subjects) {
}
