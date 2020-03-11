package com.example.demo;


import java.util.Objects;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
public class Demo {

	 public static void main(final String[] args) throws InterruptedException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
	        // Spring Java config
		    System.out.println("hola");
	        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
	        context.register(DataBaseConfig.class);
	        context.register(SpringBatchConfig.class);
	        

	        context.refresh();

	        // Spring xml config
	        // ApplicationContext context = new ClassPathXmlApplicationContext("spring-batch.xml");

	        JobExecution j = runJob(context, "firstBatchJob");
	        System.out.println("first job finish");
	        
	        //Thread.sleep(100000);
	        //System.out.println("after time");
	        //runJob(context, "skippingBatchJob");
	        //runJob(context, "skipPolicyBatchJob");
	        //runJob(context, "retryBatchJob");

	    }

	    private static JobExecution runJob(AnnotationConfigApplicationContext context, String batchJobName) throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
	        final JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");
	        final Job job = (Job) context.getBean(batchJobName);

	            // To enable multiple execution of a job with the same parameters
	            JobParameters jobParameters = new JobParametersBuilder().addString("jobID","id")
	                .toJobParameters();
	            return jobLauncher.run(job, jobParameters);
	         
	    }

}
