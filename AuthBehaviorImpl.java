
package teste_googledrive;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.drive.DriveScopes;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;

public class AuthBehaviorImpl implements AuthBehavior{
    
    @Override
    public final GoogleCredential doCredentialRefreshToken(HttpTransport httpTransport,
            JsonFactory jsonFac, String clientID, String clientSecret, TokenResponse response){
        
        return new GoogleCredential.Builder().setTransport(httpTransport)
                .setJsonFactory(jsonFac)
                .setClientSecrets(clientID, clientSecret)
                .build()
                .setFromTokenResponse(response);
    }
    
    @Override
    public final String getAuthURI(GoogleAuthorizationCodeFlow flow, HttpTransport transport,
            JsonFactory jsonFactory, String clientID, String clientSecret, String redirectStr, 
            Collection<String> scopes){
        
        flow = new GoogleAuthorizationCodeFlow
                .Builder(transport, jsonFactory, 
                clientID, clientSecret, Arrays.asList(DriveScopes.DRIVE))
                .setAccessType("offline")
                .setApprovalPrompt("force").build();
        return flow.newAuthorizationUrl().setRedirectUri(redirectStr).build();
    }
    
    @Override
    public final void setRedirectURI(String authURL){
        try{
            Desktop desktop = Desktop.getDesktop();
            URI uri = new URI(authURL);
            desktop.browse(uri);
        }catch(IOException | URISyntaxException ex){
            System.err.println("Erro ao abrir página");
            ex.printStackTrace();
        }
    }
}
