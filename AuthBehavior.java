package teste_googledrive;
    
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;

import java.util.Collection;

public interface AuthBehavior{
    
    GoogleCredential doCredentialRefreshToken(HttpTransport httpTransport,
            JsonFactory jsonFac, String clientID, String clientSecret, TokenResponse response);
    
    String getAuthURI(GoogleAuthorizationCodeFlow flow, HttpTransport transport,
            JsonFactory jsonFactory, String clientID, String clientSecret, String redirectStr, 
            Collection<String> scopes);
    
    void setRedirectURI(String authURL);
}
