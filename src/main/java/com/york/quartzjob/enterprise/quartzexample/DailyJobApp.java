package com.york.quartzjob.enterprise.quartzexample;

import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.york.quartzjob.enterprise.quartzexample.job.DailyJob;
import com.york.quartzjob.enterprise.quartzexample.job.Job1;
import com.york.quartzjob.enterprise.quartzexample.job.Job2;


/*
**Expression**	 	**Meaning**
0 0 12 * * ?		Fire at 12pm (noon) every day
0 15 10 ? * *		Fire at 10:15am every day
0 15 10 * * ?		Fire at 10:15am every day
0 15 10 * * ? *		Fire at 10:15am every day
0 15 10 * * ? 2005	Fire at 10:15am every day during the year 2005
0 * 14 * * ?		Fire every minute starting at 2pm and ending at 2:59pm, every day
0 0/5 14 * * ?		Fire every 5 minutes starting at 2pm and ending at 2:55pm, every day
0 0/5 14,18 * * ?	Fire every 5 minutes starting at 2pm and ending at 2:55pm, AND fire every 5 minutes starting at 6pm and ending at 6:55pm, every day
0 0-5 14 * * ?		Fire every minute starting at 2pm and ending at 2:05pm, every day
0 10,44 14 ? 3 WED	Fire at 2:10pm and at 2:44pm every Wednesday in the month of March.
0 15 10 ? * MON-FRI	Fire at 10:15am every Monday, Tuesday, Wednesday, Thursday and Friday
0 15 10 15 * ?		Fire at 10:15am on the 15th day of every month
0 15 10 L * ?		Fire at 10:15am on the last day of every month
0 15 10 L-2 * ?		Fire at 10:15am on the 2nd-to-last last day of every month
0 15 10 ? * 6L		Fire at 10:15am on the last Friday of every month
0 15 10 ? * 6L		Fire at 10:15am on the last Friday of every month
0 15 10 ? * 6L 2002-2005	Fire at 10:15am on every last friday of every month during the years 2002, 2003, 2004 and 2005
0 15 10 ? * 6#3		Fire at 10:15am on the third Friday of every month
0 0 12 1/5 * ?		Fire at 12pm (noon) every 5 days every month, starting on the first day of the month.
0 11 11 11 11 ?		Fire every November 11th at 11:11am.	
*/
public class DailyJobApp {

	private static DailyJobApp myapp=null;
	private Scheduler scheduler1;
	
	private DailyJobApp(){}
	
	//Singlton's method
	public static synchronized DailyJobApp getInstance(){
		if (myapp==null) {
			myapp = new DailyJobApp();
		}
		return myapp;
	}
	
	public void runDailyJob(String schedule){
		try{
			String myName=this.getClass().getSimpleName();
			JobDetail job1 = JobBuilder.newJob(DailyJob.class).withIdentity(myName, "group" + myName).build();

			Trigger trigger1 = TriggerBuilder.newTrigger()
				.withIdentity("cronTrigger"+ myName, "group" + myName)
				.withSchedule(CronScheduleBuilder.cronSchedule(schedule))
				.build();
		
			Scheduler scheduler1 = new StdSchedulerFactory().getScheduler();
			scheduler1.start();
			scheduler1.scheduleJob(job1, trigger1);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void finalize() throws Throwable {
	    System.out.println("Run finalize in MyApp" );
	    if ( scheduler1 != null) {
	    	System.out.println("scheduler1 shutdown:" + "cronTrigger1" );
	    	scheduler1.shutdown();
	    }
	}
		
	public void shutdown() throws Throwable{
		finalize();
	}
	
	public void jobTest() {
		try {
			JobDetail job1 = JobBuilder.newJob(Job1.class)
					.withIdentity("job1", "group1").build();

			Trigger trigger1 = TriggerBuilder.newTrigger()
					.withIdentity("cronTrigger1", "group1")
					.withSchedule(CronScheduleBuilder.cronSchedule("0/5 * * * * ?"))
					.build();
			
			scheduler1 = new StdSchedulerFactory().getScheduler();
			scheduler1.start();
			scheduler1.scheduleJob(job1, trigger1);

			JobDetail job2 = JobBuilder.newJob(Job2.class)
					.withIdentity("job2", "group2").build();
			
			Trigger trigger2 = TriggerBuilder.newTrigger()
					.withIdentity("cronTrigger2", "group2")
					.withSchedule(CronScheduleBuilder.cronSchedule(new CronExpression("0/7 * * * * ?")))
					.build();
			
			Scheduler scheduler2 = new StdSchedulerFactory().getScheduler();
			scheduler2.start();
			scheduler2.scheduleJob(job2, trigger2);
			
			Thread.sleep(100000);
			
			scheduler1.shutdown();
			scheduler2.shutdown();
			
			System.out.println("END");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}