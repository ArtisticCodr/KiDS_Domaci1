package job_dispatcher;

import java.util.concurrent.BlockingQueue;

import cli.SharedObjCollection;
import job.Job;
import job.ScanType;

public class JobDispatcher implements Runnable {

	private BlockingQueue<Job> jobQueue;

	public JobDispatcher(SharedObjCollection sharedColl) {
		this.jobQueue = sharedColl.jobQueue;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Job job = jobQueue.take();
				job.initiate();

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
