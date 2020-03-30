package FireBase;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.cloud.firestore.Query;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FireBaseAccess {
    public Firestore db;

    public FireBaseAccess() throws IOException {
        FileInputStream serviceAccount =
                new FileInputStream("./gosecuriepsi-firebase-adminsdk-gc6mg-c0c10802bc.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://gosecuriepsi.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);
        this.db = FirestoreClient.getFirestore();
    }



 public boolean GetDataFromDatabase() throws ExecutionException, InterruptedException {
     CollectionReference users = db.collection("Users");
     Query query = users.orderBy("name");
     ApiFuture<QuerySnapshot> querySnapshot = query.get();
     List<DocumentSnapshot> documents = new ArrayList<DocumentSnapshot>();


     for(DocumentSnapshot document : querySnapshot.get().getDocuments()){
         documents.add(document);
     }

     for(DocumentSnapshot doc : documents){
         System.out.println(doc.getData());
     }

     return true;
 }
}
