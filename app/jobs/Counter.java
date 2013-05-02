package jobs;

public class Counter extends TimerJob {
	@Override
	protected void run() {
		int count = jobParams.getInt("count");
		for (int i = 0; i < count; i++) {
			if (i % 100 == 0) {
				log("Count = " + i);
			}
		}
		
	}

}
