package com.luanvv.taskdemo.configuration;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.batch.item.ItemProcessor;

import com.luanvv.taskdemo.model.Bmi;
import com.luanvv.taskdemo.model.Metric;

public class BmiProcessor implements ItemProcessor<Metric, Bmi> {
	
	@Override
	public Bmi process(Metric metric) throws InterruptedException {
//		Thread.sleep(ThreadLocalRandom.current().nextInt(1, 50 + 1));
		return Bmi.fromMetric(metric.getHeight(), metric.getWeight());
	}
}
