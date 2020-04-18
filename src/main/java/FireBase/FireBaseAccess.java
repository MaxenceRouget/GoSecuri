package FireBase;

import Model.User;
import Utils.Utils;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.cloud.firestore.Query;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FireBaseAccess {
    private static final FireBaseAccess instance = new FireBaseAccess();
    private static Firestore db;
    private FireBaseAccess(){
        try {
            init();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
    public static final FireBaseAccess getInstance()
    {
        return instance;
    }
    private void init() throws IOException {
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

    public void addToDb(String name, String file){
        try{
            Map<String, Object> update = new HashMap<>();
            update.put(name,file);
            ApiFuture<WriteResult> writeResult =
                    db.collection("User")
                            .document("MyUserTest")
                            .set(update, SetOptions.merge());
// ...
            System.out.println("Update time : " + writeResult.get().getUpdateTime());
        }catch (Exception e){

        }
    }
}

