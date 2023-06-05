package com.minejava.kafkastreams.utilservice.dto;

import java.util.List;

public record UserserviceAg(String id,
						   String username,
						   String email,
						   String name,
						   String serviceAddress,
						   List<SubjectUtilAg> subjects) {
}
