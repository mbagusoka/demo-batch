package com.learn.demobatch.users.batch;

import com.learn.demobatch.users.entity.User;
import com.learn.demobatch.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public FlatFileItemReader<User> itemReader(@Value("classpath:users.csv") Resource resource) {
        FlatFileItemReader<User> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(resource);
        itemReader.setName("CSV-Reader");
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(lineMapper());
        return itemReader;
    }

    private LineMapper<User> lineMapper() {
        DelimitedLineTokenizer lineTokenizer = getDelimitedLineTokenizer();
        BeanWrapperFieldSetMapper<User> fieldSetMapper = getUserBeanWrapperFieldSetMapper();
        DefaultLineMapper<User> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    private DelimitedLineTokenizer getDelimitedLineTokenizer() {
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames("id", "name", "dept", "salary");
        return lineTokenizer;
    }

    private BeanWrapperFieldSetMapper<User> getUserBeanWrapperFieldSetMapper() {
        BeanWrapperFieldSetMapper<User> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(User.class);
        return fieldSetMapper;
    }

}

@Component
@StepScope
class Processor implements ItemProcessor<User, User> {

    private static final Map<String, String> DEPT_NAMES;

    static {
        DEPT_NAMES = new HashMap<>();
        DEPT_NAMES.put("001", "Technology");
        DEPT_NAMES.put("002", "Operations");
        DEPT_NAMES.put("003", "Accounts");
    }

    @Override
    public User process(User user) {
        String deptName = DEPT_NAMES.get(user.getDept());
        user.setDept(deptName);
        return user;
    }
}

@Component
@RequiredArgsConstructor
@StepScope
class JpaWriter implements ItemWriter<User> {

    private final UserRepository userRepository;

    @Override
    public void write(List<? extends User> users) {
        userRepository.saveAll(users);
    }
}