package dzialaj.GoogleAPI;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class TokenVerifierAndParser {
    private static final String GOOGLE_CLIENT_ID =GoogleSecret.getGoogleClientId();

    public static GoogleIdToken.Payload getPayload (String tokenString) {

        JacksonFactory jacksonFactory = new JacksonFactory();
        GoogleIdTokenVerifier googleIdTokenVerifier =
                            new GoogleIdTokenVerifier(new NetHttpTransport(), jacksonFactory);

        GoogleIdToken token = null;
        try {
            token = GoogleIdToken.parse(jacksonFactory, tokenString);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            assert token != null;
            if (googleIdTokenVerifier.verify(token)) {
                GoogleIdToken.Payload payload = token.getPayload();
                if (!GOOGLE_CLIENT_ID.equals(payload.getAudience())) {
                    throw new IllegalArgumentException("Blad");
                } else if (!GOOGLE_CLIENT_ID.equals(payload.getAuthorizedParty())) {
                    throw new IllegalArgumentException("Blad1");
                }
                return payload;
            } else {
                throw new IllegalArgumentException("Blad token nie do zweryfikowania");
            }
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        return token.getPayload();
    }
}