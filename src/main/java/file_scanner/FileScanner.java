package file_scanner;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class FileScanner extends RecursiveTask<Map<String, Integer>> {

	private static final long serialVersionUID = -1031759288181723247L;

	private String directoyPath;
	private long limit;
	private boolean managed;
	private Stack<File> files;
	private Stack<Stack<File>> work;
	private String[] keywords;

	public FileScanner(String directoyPath, long limit, String[] keywords) {
		this.directoyPath = directoyPath;
		this.limit = limit;
		this.keywords = keywords;
		this.managed = false;
	}

	public FileScanner(Stack<Stack<File>> work, long limit, String[] keywords) {
		this.work = work;
		this.limit = limit;
		this.keywords = keywords;

		this.managed = true;
	}

	@Override
	protected Map<String, Integer> compute() {
		Map<String, Integer> toReturn = new HashMap<String, Integer>();

		if (managed == false) {
			sort();
			splitWork();
			managed = true;
		}

		if (work.size() == 1) {
			Stack<File> myWork = work.pop();
			// stavljamo u toReturn rezultat obrade
		} else {
			// slitujemo work na 2 dela
			
			ForkJoinTask<Map<String, Integer>> forkTask = new FileScanner(work, limit, keywords);

		}

		return toReturn;
	}

	private int count(String word, File file) {
		return 0;
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

}
