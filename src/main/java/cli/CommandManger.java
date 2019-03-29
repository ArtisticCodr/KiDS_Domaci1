package cli;

public class CommandManger {

	SharedObjCollection sharedColl;

	public CommandManger(SharedObjCollection sharedColl) {
		this.sharedColl = sharedColl;
	}

	public void ad(String line) {
		try {
			line = line.substring(3);
			
			sharedColl.DirectoryCrawler_Lock.lock();
			sharedColl.directories.add(line);
			sharedColl.DirectoryCrawler_Lock.unlock();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void aw(String line) {
		try {
			line = line.substring(3);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void get(String line) {
		try {
			line = line.substring(4);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void query(String line) {
		try {
			line = line.substring(6);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void cws() {

	}

	public void cfs() {

	}

	public void stop() {

	}
}
