package jobs;

public class CounterWithError extends TimerJob{

	@Override
	protected void run() {
		int count = jobParams.getInt("count");
		for (int i = 0; i < count; i++) {
			if (i % 1000 == 0) {
				log("PROCESSING ......" + i);
			}
			
			if (i > count / 2) 
				throw new RuntimeException("JUST BECAUSE I WANT TO");
		}
	}

}
