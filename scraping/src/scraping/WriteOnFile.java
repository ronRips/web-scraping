package scraping;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

//***********
public class WriteOnFile {
    ArrayList<ArrayList<String>> list;
    //constructor
    WriteOnFile(ArrayList<ArrayList<String>> list){
        this.list = list;
    }
    //************
    void write(){
        try {
            FileWriter myWriter = new FileWriter("list_of_movies.txt");
            for(ArrayList<String> movie: list){
                String details = "";
                for (String str: movie){
                    details += str + "|";
                }
                details = Utility.correct(details);
                myWriter.write(details + "\n");
            }
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
