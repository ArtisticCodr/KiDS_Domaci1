package threadSafeObj;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Connector {
	private final Lock lock = new ReentrantLock();

	public Document getDocument(String url) {
		lock.lock();
		Document doc = null;

		try {
			doc = Jsoup.connect(url).get();
		} catch (Exception e) {
			System.err.println("WebScanner Error at URL: " + url + "\nMessage: " + e.getMessage());
			doc = null;
		} finally {
			lock.unlock();
		}

		return doc;
	}

}
