package pl.coreservices.FileListener;


import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import pl.coreservices.model.reader.FileReaderClass;

public class FileListener {
	
	public static final Path Folder = Paths.get("z:\\4");
	
	public void run() throws IOException, InterruptedException {
		FileReaderClass read = new FileReaderClass();
		
		
		WatchService watchService = FileSystems.getDefault().newWatchService();
		Folder.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
	
		boolean valid = true;
		do {
			WatchKey watchKey = watchService.take();
	
			for (WatchEvent event : watchKey.pollEvents()) {
				WatchEvent.Kind kind = event.kind();
				if (StandardWatchEventKinds.ENTRY_CREATE.equals(event.kind())) {
					String fileName = event.context().toString();
					System.out.println("File Created:" + fileName);
					read.readFile(Folder + "\\" + fileName);
					//dodaj do configa ta sciezke ! faxFolder
					deleteFile(fileName);
				}
			}
			valid = watchKey.reset();
	
		} while (valid);
	}
	
	
	
	//read all files in directory on the first run
	public void readFirstFiles() throws IOException {
		FileReaderClass read = new FileReaderClass();


		File[] files = new File(Folder.toString()).listFiles();
		//If this pathname does not denote a directory, then listFiles() returns null. 

		for (File file : files) {
		    if (file.isFile()) {
		    	read.readFile(file.getAbsolutePath());
		    }
		}
		for(int i=files.length-1;i>-1;i--) {
			deleteFile(files[i].getName());
		}
		
	}
	
	public void deleteFile(String fileName) throws IOException {
		try {
			
			//add necessary delay, files may be still in use
			Thread.sleep(1000);
			
			Files.delete(Paths.get(Folder + "\\" + fileName));
			System.out.println("File Deleted:" + fileName);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
