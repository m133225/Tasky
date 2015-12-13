package storage;


import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import logic.TaskAbstraction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestJsonFormatStorage {
    JsonFormatStorage storageObject;
    ArrayList<TaskAbstraction> result ;
    String TEST_ITEMS = "item11\nitem12\nitem13\n";

    @Before
    public void setup(){
        try {
            File newSaveFile = new File("newsave.txt");
            newSaveFile.delete();
            File saveFile = new File("save.txt");
            saveFile.delete();
            storageObject = new JsonFormatStorage(true);
            result = new ArrayList<TaskAbstraction>();
            
            String[] strArr = TEST_ITEMS.split(" ");
            for(int i = 0; i < strArr.length; i++){
                TaskAbstraction newTask = new TaskAbstraction(strArr[i]);
                result.add(newTask);
            }
            
            storageObject.writeItemList(result); // should manually write this in json
                                                // instead of calling the function
    
            /*
            BufferedWriter buffWriter = new BufferedWriter(new FileWriter(saveFile));
            buffWriter.write(TEST_ITEMS);
            buffWriter.close();
            */
        } catch (IOException e){
            e.printStackTrace();
        }
    
    }

    //test write tasks to file
    @Test
    public void testWriteItemList() throws IOException{
        boolean message = storageObject.writeItemList(result);
        assertEquals(true, message);
    }

    //test save to new path
    @Test
    public void testSaveFileToPath() throws IOException{
        boolean message = storageObject.saveFileToPath("newsave.txt");
        assertEquals(true, message);
    }

    //test to get item from saved file
    @Test
    public void testGetItemList() throws IOException{
        ArrayList<TaskAbstraction> message = storageObject.getItemList();
        String resultStr = "";
        
        for(int i = 0; i < message.size(); i++){
            resultStr += message.get(i).getName();
        }
            
        assertEquals(TEST_ITEMS, resultStr);
    }

    @After
    public void cleanup(){
        File saveFile = new File("save.txt");
        File anotherSaveFile = new File("newsave.txt");
        saveFile.delete();
        anotherSaveFile.delete();
    }
}
