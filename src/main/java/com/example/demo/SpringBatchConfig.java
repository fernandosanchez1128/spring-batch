package com.example.demo;



import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@EnableBatchProcessing
@Configuration
public class SpringBatchConfig {
	 	@Autowired
	    private JobBuilderFactory jobs;
	 
	    @Autowired
	    private StepBuilderFactory stepBuilderFactory;
	 
	    @Value("input/record.csv")
	    private Resource inputCsv;
	 
	    @Value("input/output")
	    private Resource outputXml;
	    
	    @Autowired 
	    DataSource dataSource;
	 
	    @Bean
	    public ItemReader<Transaction> itemReader()
	      throws UnexpectedInputException, ParseException {
	        FlatFileItemReader<Transaction> reader = new FlatFileItemReader<Transaction>();
	        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
	        String[] tokens = { "username", "user_id", "transaction_date", "transaction_amount" };
	        tokenizer.setNames(tokens);
	        reader.setResource(inputCsv);
	        DefaultLineMapper<Transaction> lineMapper = 
	          new DefaultLineMapper<Transaction>();
	        lineMapper.setLineTokenizer(tokenizer);
	        lineMapper.setFieldSetMapper(new RecordFieldSetMapper());
	        reader.setLineMapper(lineMapper);
	        return reader;
	    }
	 
	    public ItemReader<Transaction> classReader( ) throws Exception {
	    	JdbcPagingItemReader<Transaction> itemReader = new JdbcPagingItemReader<>();
	        itemReader.setDataSource(dataSource);
	        itemReader.setQueryProvider(queryProvider());
	        itemReader.setPageSize(1000);
	        
	        itemReader.setRowMapper(new DataBaseMapper());
	        itemReader.afterPropertiesSet();

	        return itemReader;
	    }
	    private MySqlPagingQueryProvider queryProvider() {
			// TODO Auto-generated method stub
	    	MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
	        queryProvider.setSelectClause("SELECT firstname");
	        queryProvider.setFromClause("FROM  PERSONS");
	        Map<String, Order> order = new HashMap<>();
	        queryProvider.isUsingNamedParameters();
	        order.put("firstname", Order.ASCENDING);
	        queryProvider.setSortKeys(order);
	        System.out.println ("named: " + queryProvider.isUsingNamedParameters());

	        return queryProvider;
		}

		@Bean
	    public ItemProcessor<Transaction, Transaction> itemProcessor() {
	        return new CustomItemProcessor();
	    }
	 
	    @Bean
	    public ItemWriter<Transaction> itemWriter(Marshaller marshaller)
	      throws MalformedURLException {
	        StaxEventItemWriter<Transaction> itemWriter = 
	          new StaxEventItemWriter<Transaction>();
	        itemWriter.setMarshaller(marshaller);
	        itemWriter.setRootTagName("transactionRecord");
	        itemWriter.setResource(outputXml);
	        return itemWriter;
	    }
	 
	    @Bean
	    public Marshaller marshaller() {
	        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
	        marshaller.setClassesToBeBound(new Class[] { Transaction.class });
	        return marshaller;
	    }
	 
	    @Bean
	    protected Step step1(@Qualifier("itemProcessor") ItemProcessor<Transaction, Transaction> processor, ItemWriter<Transaction> writer) throws Exception {
	        return stepBuilderFactory
	                .get("step1")
	                .<Transaction, Transaction> chunk(2)
	                .reader(classReader())
	                .processor(processor)
	                .writer(new NoOpItemWriter())
	                .build();
	    }

	 
	    @Bean(name = "firstBatchJob")
	    public Job job(@Qualifier("step1") Step step1) {
	        return jobs.get("firstBatchJob")
	        		.start(step1)
	        		.incrementer(new RunIdIncrementer())
	        		.build();
	    }
	
}
