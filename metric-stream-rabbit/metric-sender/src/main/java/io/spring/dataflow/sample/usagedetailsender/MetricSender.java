package io.spring.dataflow.sample.usagedetailsender;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import io.spring.dataflow.sample.domain.Metric;

@EnableScheduling
@EnableBinding(Source.class)
public class MetricSender {

	@Autowired
	private Source source;

	@Value("${bmi.file.path}")
	private String filePath;

	@Scheduled(fixedDelay = 1000)
	public void sendEvents() throws IOException {
		if (filePath == null) {
			System.out.println("File path is null");
			return;
		}
		Path path = Paths.get(filePath);
		try (Stream<Path> stream = Files.list(path)) {
			stream.filter(file -> !Files.isDirectory(file))
//			.filter(file -> file.endsWith(".csv"))
			.forEach(file -> {
				try {
					System.out.println("Processing " + file);
					Reader in = new FileReader(file.toFile());
					Iterable<CSVRecord> records = CSVFormat.DEFAULT
//							.withHeader("Index", "Height(Inches)", "Weight(Pounds)")
							.withFirstRecordAsHeader()
							.parse(in);
					for (CSVRecord row : records) {
						float height = Float.parseFloat(row.get(1));
						float weight = Float.parseFloat(row.get(2));
						this.source.output().send(MessageBuilder.withPayload(new Metric(height, weight)).build());
					}
					Files.delete(file);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}
//		Metric metric = new Metric();
//		metric.setHeight(new Random().nextFloat());
//		metric.setWeight(new Random().nextFloat());
//		this.source.output().send(MessageBuilder.withPayload(metric).build());
	}
}
