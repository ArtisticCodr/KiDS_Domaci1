package directory_crawler;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class DirectoriesList {

	private ArrayList<String> directories;
	private ReentrantLock lock;

	public DirectoriesList() {
		directories = new ArrayList<String>();
		lock = new ReentrantLock();
	}

	public String get(int index) {
		lock.lock();
		String value = directories.get(index);
		lock.unlock();

		return value;
	}

	public void add(String value) {
		lock.lock();
		directories.add(value);
		lock.unlock();
	}

	public int size() {
		lock.lock();
		int size = directories.size();
		lock.unlock();

		return size;
	}

}
