package job_dispatcher;

import java.util.concurrent.BlockingQueue;

import cli.SharedObjCollection;
import job.Job;
import job.ScanType;

public class JobDispatcher implements Runnable {

	private BlockingQueue<Job> jobQueue;

	public JobDispatcher(SharedObjCollection sharedColl) {
		this.jobQueue = sharedColl.jobQueue;
		// web scanner thread pool
		// file scanner thread pool
	}

	@Override
	public void run() {
		while (true) {
			try {
				Job job = jobQueue.take();
				if (job.getType().equals(ScanType.WEB)) {
					System.out.println("dispatching WEB job: " + job.getQuery());
				} else {
					System.out.println("dispatching FILE job: " + job.getQuery());
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
