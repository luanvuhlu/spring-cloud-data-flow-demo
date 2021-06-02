package com.luanvv.taskdemo.configuration;

import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;

public class CsvLineMapper<T> extends DefaultLineMapper<T> {

	public CsvLineMapper(Class<T> clazz, String... names) {
		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(",");
		tokenizer.setNames(names);
		setLineTokenizer(tokenizer);
		
		BeanWrapperFieldSetMapper<T> mapper = new BeanWrapperFieldSetMapper<>();
		mapper.setTargetType(clazz);
		setFieldSetMapper(mapper);
	}
}
