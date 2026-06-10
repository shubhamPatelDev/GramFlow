package com.gramflow.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        // Load service account key from classpath (src/main/resources)
        InputStream serviceAccount = FirebaseConfig.class.getClassLoader()
                .getResourceAsStream("firebase-service-account.json");
        if (serviceAccount == null) {
            throw new IllegalStateException("Firebase service account file not found in classpath");
        }
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
        return FirebaseApp.initializeApp(options);
    }

    @Bean
    public com.google.cloud.firestore.Firestore firestore(FirebaseApp firebaseApp) {
        return com.google.firebase.cloud.FirestoreClient.getFirestore(firebaseApp);
    }
}
