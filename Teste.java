package teste_googledrive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import javax.activation.MimetypesFileTypeMap;


public class Teste {
    private HttpTransport httpTransport = new NetHttpTransport();
    private JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    private static final String APP_NAME = "Teste_Upload";
    private static FileDataStoreFactory dataStoreFactory;
    
   private static String OS = null;
   public static String getOsName(){
      if(OS == null)
          OS = System.getProperty("os.name");
      return OS;
   }
      
   public static boolean isWindows(){
      return getOsName().toLowerCase().startsWith("windows");
   }

    private static String getStorageFullPath(){
        if(isWindows())
            return System.getProperty("user.home") + "\\.credentials\\" + APP_NAME;
        else 
            return System.getProperty("user.home") + "/.credentials/" + APP_NAME;
    }
    
    private static final String STORAGE_PATH = getStorageFullPath();
    private static java.io.File DATA_STORAGE_CREDENTIALS;  
    static {
        try{
            DATA_STORAGE_CREDENTIALS = new java.io.File(STORAGE_PATH);
            dataStoreFactory = new FileDataStoreFactory(DATA_STORAGE_CREDENTIALS);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
    
    private static final List<String> SCOPES 
        = Arrays.asList(DriveScopes.DRIVE_FILE);
    public Credential doAuth() throws IOException{
        java.io.File jsonFile = new java.io.File("src/teste_googledrive/resources/client_secret.json");
        FileInputStream fin = new FileInputStream(jsonFile);
        GoogleClientSecrets sec = GoogleClientSecrets.load(jsonFactory, 
                new InputStreamReader(fin));
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        httpTransport, jsonFactory, sec, SCOPES)
                .setDataStoreFactory(dataStoreFactory)
                .setAccessType("offline")
                .build();
            Credential credential = new AuthorizationCodeInstalledApp(flow, 
                    new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORAGE_CREDENTIALS.getAbsolutePath());
        
        return credential;
    }
    
    private Drive getService() throws IOException{
        return new Drive.Builder(httpTransport, jsonFactory, doAuth())
                .setApplicationName(APP_NAME)
                .build();
    }
    
    private void doUpload() throws IOException{
        System.out.println("Digite o caminho do arquivo: ");
        BufferedReader b1 = new BufferedReader(new InputStreamReader(System.in));
        String path = b1.readLine();
        
        java.io.File fileContent = new java.io.File(path);
        String arq = path.substring(path.indexOf("."), path.length());
        File testFile = new File();
        if(isWindows())
            testFile.setTitle(path.substring(path.lastIndexOf("\\") + 1));
        else 
            testFile.setTitle(path.substring(path.lastIndexOf("/") + 1));
        testFile.setDescription("Um documento de teste!");
        String mime = new MimetypesFileTypeMap()
                .getContentType(fileContent);
        testFile.setMimeType(mime);
        testFile.setFileExtension(arq.substring(arq.indexOf("."), arq.length()));
        
        FileContent mediaContent = new FileContent(mime, fileContent);
        File file = getService().files().insert(testFile, mediaContent).execute();
        System.out.println("File ID: " + file.getId());
    }
    
    public void run() throws IOException{
        doUpload();
        try{
            Desktop desktop = Desktop.getDesktop();
            URI uri = new URI("https://drive.google.com/drive/my-drive");
            desktop.browse(uri);
        }catch(IOException | URISyntaxException ex){
            System.err.println("Erro ao abrir página");
            ex.printStackTrace();
        }
    }
}
