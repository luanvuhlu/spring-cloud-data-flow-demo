package com.luanvv.taskdemo.configuration;

import java.util.Arrays;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import com.luanvv.taskdemo.model.Bmi;
import com.luanvv.taskdemo.model.Metric;

@Configuration
@EnableTask
@EnableBatchProcessing
public class BmiConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Value("${bmi.file.path}")
	private String filePath;

	@Autowired
	private DataSource datasource;

	private void doBeforeJob() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.execute(
				"CREATE TABLE IF NOT EXISTS BMI ( id INT NOT NULL AUTO_INCREMENT, value float, createdDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (id))");
		jdbcTemplate.execute("DELETE FROM BMI WHERE 1=1");
	}

	@Bean
	public Job job1(ItemReader<Metric> reader, ItemProcessor<Metric, Bmi> itemProcessor, ItemWriter<Bmi> writer) {
		Step step = stepBuilderFactory.get("BmiProcessing")
				.<Metric, Bmi>chunk(100).reader(reader)
				.processor(itemProcessor)
				.faultTolerant()
				.retryLimit(3)
				.retry(DeadlockLoserDataAccessException.class)
				.writer(writer)
				.build();

		return jobBuilderFactory
				.get("BmiJob").incrementer(new RunIdIncrementer())
				.start(step)
				.listener(new JobExecutionListenerSupport() {
					@Override
					public void beforeJob(JobExecution jobExecution) {
						doBeforeJob();
					}
				}).build();
	}

	@Bean
	public ItemReader<Metric> itemReader() {
		return new FlatFileItemReaderBuilder<Metric>().name("MetricReader").resource(new FileSystemResource(filePath))
				.linesToSkip(1).lineMapper(new CsvLineMapper<Metric>(Metric.class, "height", "weight") {

					@Override
					public void setLineTokenizer(LineTokenizer tokenizer) {
						super.setLineTokenizer(tokenizer);
						DelimitedLineTokenizer delimitedLineTokenizer = (DelimitedLineTokenizer) tokenizer;
						delimitedLineTokenizer.setIncludedFields(1, 2);
					}
				}).build();
	}

//	@Bean
	public ItemWriter<Bmi> jdbcBmiWriter(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Bmi>().dataSource(dataSource).itemPreparedStatementSetter((item, ps) -> {
			ps.setFloat(1, item.getValue());
		}).sql("INSERT INTO BMI (value) VALUES (?)").build();
	}

	@Bean
	public ItemWriter<Bmi> consoleWriter() {
		return new ConsoleItemWriter<>();
	}

//	@Bean
//	public ItemWriter<Bmi> writer() {
//		CompositeItemWriter<Bmi> writer = new CompositeItemWriter<>();
//		writer.setDelegates(Arrays.asList(consoleWriter(), jdbcBmiWriter(datasource)));
//		return writer;
//	}

	@Bean
	ItemProcessor<Metric, Bmi> billProcessor() {
		return new BmiProcessor();
	}
}
