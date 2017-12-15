package teste_googledrive;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileHandler {
    
    public final File setFileToUpload(String path) throws IOException{
        BufferedReader b1 = new BufferedReader(new InputStreamReader(System.in));
        path = b1.readLine();
        return new File(path);
    }
    
    public final String setFileTitle(String path){
        return path.substring(path.lastIndexOf("\\") + 1);
    }
    
    public final String setFileExtension(String path){
        return path.substring(path.indexOf("."), path.length());
    }
}
