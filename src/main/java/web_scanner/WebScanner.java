package web_scanner;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cli.SharedObjCollection;
import job.Job;
import job.ScanType;
import threadSafeObj.ThreadSafeList;

public class WebScanner implements Callable<Map<String, Integer>> {

	private AtomicInteger hopCount;
	private String url;
	private BlockingQueue<Job> jobQueue;
	private ThreadSafeList<String> keywords;

	private SharedObjCollection sharedColl;

	private Job job = null;

	// konstruktor za inicializacuju Job-a
	public WebScanner(Job job) {
		this.job = job;
	}

	// konstruktor za pokretanje posla
	public WebScanner(int hopCount, String url, BlockingQueue<Job> jobQueue, String[] keywords,
			SharedObjCollection sharedColl) {
		this.url = url;
		this.hopCount = new AtomicInteger(hopCount);
		this.jobQueue = jobQueue;
		this.keywords = new ThreadSafeList<String>(Arrays.asList(keywords));
		this.sharedColl = sharedColl;
	}

	@Override
	public Map<String, Integer> call() throws Exception {
		if (job != null) {
			Future<Map<String, Integer>> result = job.initiate();

			// stavljas result u result retriever
			return null;
		}

		Map<String, Integer> toReturn = new HashMap<String, Integer>();

		if (hopCount.get() > 0) {
			hop();
		}

		toReturn = countKeywords();
		return toReturn;
	}

	private void hop() {
		try {
			Document doc = Jsoup.connect(url).get();
			Elements links = doc.select("a[href]");

			for (Element link : links) {
				String lnk = link.attr("abs:href");
				Job job = new Job(ScanType.WEB, lnk, sharedColl, hopCount.get() - 1);
				jobQueue.put(job);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Map<String, Integer> countKeywords() {
		Map<String, Integer> returnMap = new HashMap<String, Integer>();
		for (int i = 0; i < keywords.size(); i++) {
			returnMap.put(keywords.get(i), 0);
		}
		try {
			System.out.println("Counting " + url);
			Document doc = Jsoup.connect(url).get();
			String text = doc.text();

			String[] words = text.split("\\s+");
			for (String word : words) {
				if (keywords.contains(word)) {
					returnMap.put(word, returnMap.get(word) + 1);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return returnMap;
	}
}
