package teste_googledrive;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import javax.activation.MimetypesFileTypeMap;


public class Teste {
    private static final String CLIENT_ID = 
        "15613803486-s564g121r1o383p2sbk1f4a4fro3lbbo.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "T_SsHRTGTa6DBlgd-PkmmnyX";
    private static final String REDIRECT = "urn:ietf:wg:oauth:2.0:oob";
    
    public void run() throws IOException{
        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();
        
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, CLIENT_ID, CLIENT_SECRET, 
        Arrays.asList(DriveScopes.DRIVE))
                .setAccessType("online").setApprovalPrompt(
                "auto").build();
        
        String urlAuth = flow.newAuthorizationUrl().
                setRedirectUri(REDIRECT).build();
        
        System.out.println("Digite o caminho do arquivo: ");
        BufferedReader b1 = new BufferedReader(new InputStreamReader(System.in));
        String path = b1.readLine();
        
        try{
            Desktop desktop = Desktop.getDesktop();
            URI uri = new URI(urlAuth);
            desktop.browse(uri);
        }catch(IOException | URISyntaxException ex){
            System.err.println("Erro ao abrir página");
            ex.printStackTrace();
        }
        
        System.out.println("Cole aqui o seu código:");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String code = br.readLine();
        
        System.out.println("Código: " + code);
        GoogleTokenResponse response = flow.newTokenRequest(code)
                .setRedirectUri(REDIRECT).execute();
        GoogleCredential credential = new GoogleCredential()
                .setFromTokenResponse(response);
        
        Drive service = new Drive.Builder(httpTransport, jsonFactory, credential)
                .build();
        
        java.io.File fileContent = new java.io.File(path);
        String arq = path.substring(path.indexOf("."), path.length());
        File testFile = new File();
        testFile.setTitle(path.substring(path.lastIndexOf("\\") + 1));
        testFile.setTitle(path.substring(path.lastIndexOf("/") + 1));
        testFile.setDescription("Um documento de teste!");
        String mime = new MimetypesFileTypeMap()
                .getContentType(fileContent);
        testFile.setMimeType(mime);
        testFile.setFileExtension(arq.substring(arq.indexOf("."), arq.length()));
        
        FileContent mediaContent = new FileContent(mime, fileContent);
        File file = service.files().insert(testFile, mediaContent).execute();
        System.out.println("File ID: " + file.getId());
    }
}