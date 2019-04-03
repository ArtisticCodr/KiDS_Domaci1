package file_scanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import job.Job;

public class FileScanner extends RecursiveTask<Map<String, Integer>> {

	private static final long serialVersionUID = -1031759288181723247L;

	private Job job;

	public FileScanner(Job job) {
		this.job = job;
	}

	@Override
	protected Map<String, Integer> compute() {

		return null;
	}
}
