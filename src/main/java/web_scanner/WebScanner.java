package web_scanner;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import job.Job;

public class WebScanner implements Callable<Map<String, Integer>> {

	private AtomicInteger hopCount;
	private String url;
	private BlockingQueue<Job> jobQueue;

	private Job job = null;

	// konstruktor za inicializacuju Job-a
	public WebScanner(Job job) {
		this.job = job;
	}

	// konstruktor za pokretanje posla
	public WebScanner(int hopCount, String url, BlockingQueue<Job> jobQueue) {
		this.url = url;
		this.hopCount = new AtomicInteger(hopCount);
		this.jobQueue = jobQueue;
	}

	@Override
	public Map<String, Integer> call() throws Exception {
		if (job != null) {
			Future<Map<String, Integer>> result = job.initiate();

			// stavljas result u result retriever
			return null;
		}
		
		

		return null;
	}
}
