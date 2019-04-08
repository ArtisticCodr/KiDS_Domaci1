package job_dispatcher;

import java.util.concurrent.BlockingQueue;

import cli.SharedObjCollection;
import file_scanner.FileScanner;
import job.Job;
import job.ScanType;
import web_scanner.WebScanner;

public class JobDispatcher implements Runnable {

	private BlockingQueue<Job> jobQueue;
	private SharedObjCollection sharedColl;

	public JobDispatcher(SharedObjCollection sharedColl) {
		this.jobQueue = sharedColl.jobQueue;
		this.sharedColl = sharedColl;
	}

	@Override
	public void run() {
		while (sharedColl.isStoped() == false) {
			try {
				Job job = jobQueue.take();
				if (job.isPoinson()) {
					break;
				}
				if (job.getType().equals(ScanType.FILE)) {
					sharedColl.submitToFileScannerPool(new FileScanner(job, sharedColl));

				} else {
					sharedColl.submitToWebScannerService(new WebScanner(job, sharedColl));
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Job Dispatcher stopping..");
	}

}
