package com.learn.demobatch.users.batch;

import com.learn.demobatch.users.entity.User;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class UserBatchConfiguration {

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, Step step) {
        return jobBuilderFactory.get("ETL-Load")
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }

    @Bean
    public Step step(
            StepBuilderFactory stepBuilderFactory,
            ItemReader<User> itemReader,
            ItemProcessor<User, User> itemProcessor,
            ItemWriter<User> itemWriter
    ) {
        return stepBuilderFactory.get("ETL-file-load")
                .<User, User>chunk(100)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<User> itemReader(@Value("classpath:usersdynamic.csv") Resource resource) {
        FlatFileItemReader<User> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(resource);
        itemReader.setName("CSV-Reader");
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(lineMapper(resource));
        return itemReader;
    }

    private LineMapper<User> lineMapper(Resource resource) {
        SimpleDynamicLineTokenizer lineTokenizer = getLineTokenizer(resource);
        BeanWrapperFieldSetMapper<User> fieldSetMapper = getUserBeanWrapperFieldSetMapper();
        DefaultLineMapper<User> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    private SimpleDynamicLineTokenizer getLineTokenizer(Resource resource) {
        SimpleDynamicLineTokenizer lineTokenizer = new SimpleDynamicLineTokenizer(resource);
        lineTokenizer.setNames("id", "name", "dept", "salary");
        return lineTokenizer;
    }

    private BeanWrapperFieldSetMapper<User> getUserBeanWrapperFieldSetMapper() {
        BeanWrapperFieldSetMapper<User> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(User.class);
        return fieldSetMapper;
    }
}