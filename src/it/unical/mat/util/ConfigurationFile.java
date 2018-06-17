package it.unical.mat.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConfigurationFile {
	
	private Lock lock = new ReentrantLock();
	private String file_path = "configuration/config.properties";
	
	public ConfigurationFile() {
	}

	public Lock getLock() {
		return lock;
	}

	public void setLock(Lock lock) {
		this.lock = lock;
	}

	public String getFile_path() {
		return file_path;
	}

	public void setFile_path(String file_path) {
		this.file_path = file_path;
	}
}
