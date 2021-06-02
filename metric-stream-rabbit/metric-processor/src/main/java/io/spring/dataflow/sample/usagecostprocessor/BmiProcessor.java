package io.spring.dataflow.sample.usagecostprocessor;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.handler.annotation.SendTo;

import io.spring.dataflow.sample.domain.Bmi;
import io.spring.dataflow.sample.domain.Metric;

@EnableBinding(Processor.class)
public class BmiProcessor {

	@StreamListener(Processor.INPUT)
	@SendTo(Processor.OUTPUT)
	public Bmi processUsageCost(Metric metric) throws InterruptedException {
		Thread.sleep(ThreadLocalRandom.current().nextInt(1, 50 + 1));
		return Bmi.fromMetric(metric.getHeight(), metric.getWeight());
	}
}
