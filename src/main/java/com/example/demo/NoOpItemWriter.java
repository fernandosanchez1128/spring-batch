package com.example.demo;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

public class NoOpItemWriter implements ItemWriter<Transaction> {

	@Override
	public void write(List<? extends Transaction> items) throws Exception {
		// TODO Auto-generated method stub
		
	}
	  
	}
