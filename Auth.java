package teste_googledrive;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
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
import java.util.Collection;
import javax.activation.MimetypesFileTypeMap;
import javax.swing.text.FlowView;


public class Auth {
    private static String CLIENT_ID = 
        "15613803486-s564g121r1o383p2sbk1f4a4fro3lbbo.apps.googleusercontent.com";
    private static String CLIENT_SECRET = "T_SsHRTGTa6DBlgd-PkmmnyX";
    private static String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";
    private String a_Token;
    private String r_Token;
    private HttpTransport httpTransport = new NetHttpTransport();
    private JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    private AuthBehavior auth = new AuthBehaviorImpl();
    private FileHandler handler = new FileHandler();
    
    public GoogleTokenResponse storeTokens(String code, GoogleAuthorizationCodeFlow flow) throws IOException{
        GoogleTokenResponse tokenResponse = flow.newTokenRequest(code)
                .setRedirectUri(REDIRECT_URI).execute();
        GoogleCredential credential = auth.doCredentialRefreshToken(httpTransport,
                jsonFactory, CLIENT_ID, CLIENT_SECRET, tokenResponse);
        setAToken(credential.getAccessToken());
        setRToken(credential.getRefreshToken());
        return tokenResponse;
    }
    
    public Drive useStoredTokens(GoogleTokenResponse tokenResponse){
        GoogleCredential credential = auth.doCredentialRefreshToken(httpTransport, 
                jsonFactory, CLIENT_ID, CLIENT_SECRET, tokenResponse);
        credential.setAccessToken(getAToken());
        credential.setRefreshToken(getRToken());
        return new Drive.Builder(httpTransport, jsonFactory, credential).build();
    }
    
    public void run() throws IOException{
        String scopes = DriveScopes.DRIVE;
        
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
            httpTransport, jsonFactory, CLIENT_ID, CLIENT_SECRET, Arrays.asList(scopes))
            .setAccessType("offline")
            .setApprovalPrompt("auto").build();
        System.out.println("Digite aqui o caminho completo do arquivo:");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String path = br.readLine();
        
        java.io.File fileContent = handler.setFileToUpload(path);
        File testFile = new File();
        testFile.setTitle(handler.setFileTitle(path));
        testFile.setDescription("Um documento de teste!");
        String mime = new MimetypesFileTypeMap()
                .getContentType(fileContent);
        testFile.setMimeType(mime);
        testFile.setFileExtension(handler.setFileExtension(path));
        
        String authURL = auth.getAuthURI(flow, httpTransport, jsonFactory, 
                CLIENT_ID, CLIENT_SECRET, REDIRECT_URI, Arrays.asList(scopes));
        auth.setRedirectURI(authURL);
        
        System.out.println("Cole aqui o seu código:");
        BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
        String code = br1.readLine();
        System.out.println("Código: " + code);
        GoogleTokenResponse tokenResponse = storeTokens(code, flow);
        Drive service = useStoredTokens(tokenResponse);
        FileContent mediaContent = new FileContent(mime, fileContent);
        File file = service.files().insert(testFile, mediaContent).execute();
        System.out.println("File ID: " + file.getId());
    }
    
    public String getAToken() {
        return a_Token;
    }

    public void setAToken(String aToken) {
        this.r_Token = aToken;
    }

    public String getRToken() {
        return r_Token;
    }

    public void setRToken(String r_Token) {
        this.r_Token = r_Token;
    }
}
