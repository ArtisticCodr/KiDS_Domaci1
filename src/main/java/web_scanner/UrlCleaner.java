package web_scanner;

import cli.SharedObjCollection;
import threadSafeObj.ThreadSafeList;

public class UrlCleaner implements Runnable {

	private ThreadSafeList<String> scanedUrls;
	private long urlRefreshTime;

	public UrlCleaner(SharedObjCollection sharedColl) {
		this.scanedUrls = sharedColl.scanedUrls;
		this.urlRefreshTime = sharedColl.getUrl_refresh_time();
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(urlRefreshTime);
				scanedUrls.clear();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

}
