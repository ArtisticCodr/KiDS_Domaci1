package cli;

import job.Job;
import job.ScanType;

public class CommandManger {

	SharedObjCollection sharedColl;

	public CommandManger(SharedObjCollection sharedColl) {
		this.sharedColl = sharedColl;
	}

	public void ad(String line) {
		try {
			line = line.substring(3);
			sharedColl.directoriesList.add(line);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void aw(String line) {
		try {
			line = line.substring(3);

			// ubacuje novi job u JobQueue
			Job job = new Job(ScanType.WEB, line);
			sharedColl.jobQueue.put(job);

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
