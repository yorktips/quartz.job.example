package com.york.quartzjob.enterprise.quartzexample.job;

import java.io.FileWriter;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class DailyJob implements Job{ 
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String myfile="job.task.log.txt";
		String msg="DailyJob --->>> Hello job! Time is " + new Date() + "\n";
		try{
			FileWriter fw=new FileWriter(myfile,true);			
			fw.write(msg);
			fw.close();
		}catch(Exception e) {
			e.printStackTrace();
		}		
		System.out.println(msg);
	}	
	
}