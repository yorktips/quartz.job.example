package com.york.quartzjob.enterprise.quartzexample;

import java.io.FileWriter;
import java.util.Date;

public class MyApp {

	public static void main(String[] args) {
		DailyJobApp dailyJobApp=DailyJobApp.getInstance();
		String schedule="0 0 12 * * ?"; //Every day 12am
		dailyJobApp.runDailyJob(schedule);
		try{
			Thread.sleep(1000 *60 *10); //10 mins
		}catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("END");
	}	

}