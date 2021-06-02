package io.spring.dataflow.sample.usagecostlogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

import io.spring.dataflow.sample.domain.Bmi;

@EnableBinding(Sink.class)
public class BmiLogger {

	private static final Logger logger = LoggerFactory.getLogger(BmiLogger.class);

	@StreamListener(Sink.INPUT)
	public void process(Bmi bmi) {
		logger.info(bmi.toString());
	}
}
