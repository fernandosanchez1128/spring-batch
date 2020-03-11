package com.example.demo;


import java.text.ParseException;
import java.text.SimpleDateFormat;


import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;


public class RecordFieldSetMapper implements FieldSetMapper<Transaction>  {
	  public Transaction mapFieldSet(FieldSet fieldSet) throws BindException {

	        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	        Transaction transaction = new Transaction();
	        // you can either use the indices or custom names
	        // I personally prefer the custom names easy for debugging and
	        // validating the pipelines
	        transaction.setUsername(fieldSet.readString("username"));
	        transaction.setUserId(fieldSet.readInt("user_id"));
	        transaction.setAmount(fieldSet.readDouble(3));
	        // Converting the date
	        String dateString = fieldSet.readString(2);
	        try {
	            transaction.setTransactionDate(dateFormat.parse(dateString));
	        } catch (ParseException e) {
	            e.printStackTrace();
	        }
	        System.out.println("trasanction: " + transaction);
	        return transaction;


	    }

}
