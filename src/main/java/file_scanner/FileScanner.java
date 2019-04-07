package file_scanner;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

import cli.SharedObjCollection;
import job.Job;
import threadSafeObj.ThreadSafeList;

public class FileScanner extends RecursiveTask<Map<String, Integer>> {

	private static final long serialVersionUID = -1031759288181723247L;

	private String directoyPath;
	private long limit;
	private boolean managed;
	private Stack<File> files;
	private Stack<Stack<File>> work;
	private ThreadSafeList<String> keywords;

	private Job job = null;
	private SharedObjCollection sharedColl;

	// konstruktor za inicializacuju Job-a
	public FileScanner(Job job, SharedObjCollection sharedColl) {
		this.job = job;
		this.sharedColl = sharedColl;
	}

	// konstructor za podelu posla
	public FileScanner(String directoyPath, long limit, String[] keywords) {
		this.directoyPath = directoyPath;
		this.limit = limit;
		this.keywords = new ThreadSafeList<String>(Arrays.asList(keywords));
		this.managed = false;
	}

	// konstruktor za pokretanje posla
	public FileScanner(Stack<Stack<File>> work, ThreadSafeList<String> keywords) {
		this.work = work;
		this.keywords = keywords;
		this.managed = true;
	}

	@Override
	protected Map<String, Integer> compute() {
		if (job != null) {
			Future<Map<String, Integer>> result = job.initiate();
			String[] path = job.getQuery().split("/");
			String name = path[path.length - 1];

			// stavljas result u result retriever
			sharedColl.resultRetriever.addCorpusResult(name, result);
			return null;
		}

		Map<String, Integer> toReturn = new HashMap<String, Integer>();

		if (managed == false) {
			sort();
			splitWork();
			managed = true;
		}

		if (work.size() == 1) {
			Stack<File> myWork = work.pop();
			toReturn = countKeywords(myWork);
		} else {
			// splitujemo work na 2 dela
			Stack<Stack<File>> newWork1 = new Stack<Stack<File>>();
			Stack<Stack<File>> newWork2 = new Stack<Stack<File>>();
			int mid = work.size() / 2;
			for (int i = 0; i < work.size(); i++) {
				if (i < mid) {
					newWork1.push(work.pop());
				} else {
					newWork2.push(work.pop());
				}
			}

			ForkJoinTask<Map<String, Integer>> forkTask = new FileScanner(newWork1, keywords);
			FileScanner callTask = new FileScanner(newWork2, keywords);

			forkTask.fork();
			Map<String, Integer> forkResult = callTask.compute();
			Map<String, Integer> callResult = forkTask.join();

			toReturn = merge(forkResult, callResult);
		}

		return toReturn;
	}

	private Map<String, Integer> countKeywords(List<File> files) {
		Map<String, Integer> returnMap = new HashMap<String, Integer>();
		for (int i = 0; i < keywords.size(); i++) {
			returnMap.put(keywords.get(i), 0);
		}
		for (File file : files) {
			try (Scanner sc = new Scanner(new FileInputStream(file))) {
				String word = new String();
				while (sc.hasNext()) {
					word = sc.next();
					if (keywords.contains(word)) {
						returnMap.put(word, returnMap.get(word) + 1);
					}
				}
				sc.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return returnMap;
	}

	// izvlacimo sve fajlove iz dir i sortiramo ih u stack po velicini
	private void sort() {
		files = new Stack<File>();
		File directory = new File(directoyPath);

		File[] fList = directory.listFiles();
		if (fList != null) {
			for (File file : fList) {
				if (file.isFile()) {
					files.push(file);
				}
			}
		}

		// nokon sorta je na vrhu stacka najveci file
		files.sort(Comparator.comparing(File::length));
	}

	private void splitWork() {
		work = new Stack<Stack<File>>();

		Stack<File> tmp = new Stack<File>();
		while (!files.isEmpty()) {
			tmp.push(files.pop());
			if (getSum(tmp) > limit) {
				work.push(tmp);
				tmp = new Stack<File>();
			}
		}

		if (!tmp.isEmpty()) {
			work.push(tmp);
		}
	}

	private long getSum(List<File> list) {
		long sum = 0;
		for (File f : list) {
			sum += f.length();
		}
		return sum;
	}

	private Map<String, Integer> merge(Map<String, Integer> m1, Map<String, Integer> m2) {
		Map<String, Integer> returnMap = new HashMap<String, Integer>();

		returnMap.putAll(m1);

		for (String key : m2.keySet()) {
			if (returnMap.containsKey(key)) {
				Integer x = returnMap.get(key) + m2.get(key);
				returnMap.put(key, x);
			} else {
				returnMap.put(key, m2.get(key));
			}
		}

		return returnMap;
	}

}
