package com.example.uploadingfiles;

import java.nio.file.Path;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class TaskService {

	@Async
	public void post(Path path) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
		map.add("name", "bmi-calculator");
		map.add("properties", "app.bmi-calculator.bmi.file.path=" + path.toAbsolutePath().toString());
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
		restTemplate.postForLocation("http://localhost:9393/tasks/executions", request);
	}
}
