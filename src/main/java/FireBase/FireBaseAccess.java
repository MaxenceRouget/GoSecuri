package FireBase;

import Model.Tools;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.cloud.firestore.Query;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.*;
import java.util.*;

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
    public Map<String, Object> GetPhoto(){
        Map<String, Object> photos = new HashMap<>();
        try{
            CollectionReference photoDb = db.collection("User");
            ApiFuture<QuerySnapshot> querySnapshot = photoDb.get();
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                photos = document.getData();
            }
            return photos;
        }catch (Exception e){
            return null;
        }
    }
    public List<DocumentSnapshot> GetDataForTools(){
        try {
            CollectionReference tools = db.collection("Tools");
            Query query = tools.orderBy("Total");
            ApiFuture<QuerySnapshot> querySnapshot = query.get();
            List<DocumentSnapshot> documents = new ArrayList<>();

            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                documents.add(document);
            }
            return documents;
        }
        catch (Exception e){
            return null;
        }
    }
    public boolean SendToolSelected(Tools tool){
        try{
            Map<String, Object> docData = new HashMap<>();
            docData.put("Total", tool.getTotal());
            docData.put("Used", tool.getUsed());
            docData.put("Owner", tool.getOwner());
            ApiFuture<WriteResult> future = db.collection("Tools").document(tool.getName()).set(docData);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}

