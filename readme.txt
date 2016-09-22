1. In Java Standalone:
		DailyJobApp dailyJobApp=DailyJobApp.getInstance();
		String schedule="0 0 12 * * ?"; //Every day 12am
		dailyJobApp.runDailyJob(schedule);
		
	This schedule will be shutdown in GC phase or call 	dailyJobApp.finalizs() to shutdown.

2. In Spring(use @Scheduled):
   @Scheduled(cron = "0 0 0 * * *")
    //excute this schedule on 0:00 each day
    public void runDailyJob() {
		......
	}
		
		
	