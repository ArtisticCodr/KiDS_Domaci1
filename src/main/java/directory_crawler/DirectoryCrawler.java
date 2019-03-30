package directory_crawler;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import cli.SharedObjCollection;

public class DirectoryCrawler implements Runnable {

	private ArrayList<String> directories;
	private ReentrantLock DirectoryCrawler_Lock;
	private String file_corpus_prefix;
	private long dir_crawler_sleep_time;

	public DirectoryCrawler(SharedObjCollection sharedColl) {
		this.directories = sharedColl.directories;
		this.DirectoryCrawler_Lock = sharedColl.DirectoryCrawler_Lock;

		this.file_corpus_prefix = sharedColl.file_corpus_prefix;
		this.dir_crawler_sleep_time = sharedColl.dir_crawler_sleep_time;
	}

	@Override
	public void run() {

		while (true) {
			DirectoryCrawler_Lock.lock();
			int size = directories.size();
			DirectoryCrawler_Lock.unlock();

			for (int i = 0; i < size; i++) {
				DirectoryCrawler_Lock.lock();
				String dir = directories.get(i);
				DirectoryCrawler_Lock.unlock();

				searchDirectory(dir);
			}

			try {
				Thread.sleep(dir_crawler_sleep_time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private void searchDirectory(String directoryName) {
		File directory = new File(directoryName);

		if (directory.getName().startsWith(file_corpus_prefix)) {
			createJob(directory.getAbsolutePath());
			return;
		}

		File[] fList = directory.listFiles();
		if (fList != null)
			for (File file : fList) {
				if (file.isDirectory()) {
					if (file.getName().startsWith(file_corpus_prefix)) {
						createJob(file.getAbsolutePath());
					} else {
						searchDirectory(file.getAbsolutePath());
					}
				}
			}
	}

	private void createJob(String directory) {
		System.out.println("creating job for: " + directory);
	}

}
