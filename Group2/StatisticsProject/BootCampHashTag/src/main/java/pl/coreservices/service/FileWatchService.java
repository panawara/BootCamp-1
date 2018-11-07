package pl.coreservices.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

import pl.coreservices.amq.MessageSendServiceAMQ;
import pl.coreservices.model.web.FileParser;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;

import static java.nio.file.StandardWatchEventKinds.*;

@RestController
public class FileWatchService {

    @Value("${folder}")
    private String filePath; 


    private FileParser fileParser = new FileParser();

    public void watchDirectoryPath() throws JMSException {
    	
    	MessageSendServiceAMQ producer = new MessageSendServiceAMQ();
    			
        Path path = Paths.get(filePath);
        System.out.println("Watching path: " + path);
        FileSystem fs = path.getFileSystem();
        try (WatchService service = fs.newWatchService()) {
            path.register(service, ENTRY_CREATE);
            path.register(service, ENTRY_DELETE);
            path.register(service, ENTRY_MODIFY);
            WatchKey key;
            while (true) {
                key = service.take();
                for (WatchEvent <?> watchEvent : key.pollEvents()) {
                	System.out.print("File change");
                    String watchedFile = watchEvent.context().toString();
                    List <String> hashTagList =
                            fileParser.parseFileRows(filePath + "\\" + watchedFile);
                    
                    
                    ArrayList<String> hashtags;
                    for(String line : hashTagList) {
                    
                    	hashtags = fileParser.extractHashTags(line);
  
                    	for(String hashtag: hashtags) {
                    		
                    		producer.sendMessage(hashtag);
                    	}
                    }
                    
                  
                 key.reset(); 
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
