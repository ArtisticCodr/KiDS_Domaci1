package job_dispatcher;

import java.util.concurrent.BlockingQueue;

import job.Job;

public class JobDispatcher implements Runnable {

	private BlockingQueue<Job> jobQueue;

	public JobDispatcher(BlockingQueue<Job> jobQueue) {
		this.jobQueue = jobQueue;
		// web scanner thread pool
		// file scanner thread pool
	}

	@Override
	public void run() {
		while (true) {
			try {
				Job job = jobQueue.take();
				
				
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
