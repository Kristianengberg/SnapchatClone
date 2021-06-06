package com.example.snapchat.adapter.repo;


import android.graphics.Bitmap;


import com.example.snapchat.TaskListener;
import com.example.snapchat.Updatable;
import com.example.snapchat.model.Snap;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Repo {
    private static Repo repo = new Repo();

    private final String finalDb = "snaps";

    public List<Snap> snaps = new ArrayList<>();

    private Updatable activity;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseStorage storage = FirebaseStorage.getInstance();


    public void setup(Updatable update, List<Snap> _snaps) {
        snaps = _snaps;
        activity = update;
        startListener();
    }

    public Snap getSnapWithID(String id) {
        for (Snap snap : snaps) {
            if (snap.getId().equals(id)) {
                return snap;
            }
        }
        return null;
    }

    /*
     the addSnap button is called when a picture has been taken and the Send button has been pressed.
     It refers to snaps collection in Firestore and creates a map with the field "title" and a randomly generated ID from the UUID class.
     Then it adds the map to firestore and calls the addImage function with the bitmap provided from the argument and the ID generated from the UUID.
     */
    public void addSnap(Bitmap bitmap) {
        DocumentReference ref = db.collection(finalDb).document();
        Map<String, String> map = new HashMap<>();
        map.put("title", UUID.randomUUID().toString());
        db.collection(finalDb).add(map);
        System.out.println("Done inserting " + ref.getId());
        addImage(bitmap, map.get("title"));
    }

    public void startListener() {

        db.collection(finalDb).addSnapshotListener((values, error) -> {
            snaps.clear();
            for (DocumentSnapshot snap : values.getDocuments()) {
                Object title = snap.get("title");
                if (title != null) {
                    snaps.add(new Snap((String) title));
                }
                System.out.println("Snap: " + snap.toString());
            }
            activity.update(null);
        });
    }

    public void addImage(Bitmap bitmap, String id) {
        StorageReference ref = storage.getReference("snaps/" + id);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        ref.putBytes(baos.toByteArray()).addOnCompleteListener(snap -> {
            System.out.println("OK to upload " + snap);
        }).addOnFailureListener(exception -> {
            System.out.println("failure to upload " + exception);
        });
    }

    public void downloadBitmap(String id, TaskListener taskListener) {
        StorageReference ref = storage.getReference("snaps/" + id);//+ id + ".JPEG"
        int max = 1024 * 1024; // you are free to set the limit here
        ref.getBytes(max).addOnSuccessListener(bytes -> {
            taskListener.receive(bytes); // god linie!
            System.out.println("Download OK");
        }).addOnFailureListener(ex -> {
            System.out.println("error in download " + ex);
        });
    }
/*
This function both deletes the reference to the image in Firestore and deletes the image in in the storage.
By taking the ID as an argument it will reference the image in both places. It executes a query in Firebase and searches for a document with the title that matches the ID.
it then gets the reference to the document and calls the delete() on it.
Deleting the image in storage was much simpler and just required to refer to it and call delete, no need to go through them all. Perhaps theres a way to do the same for documents? Needs further investigation
 */
    public void deleteImageFromServer(String id) {
        StorageReference stoRef = storage.getReference("snaps/" + id);
      //  db.collection(finalDb).document(id).delete(); - Snak om dette til eksamen, tror det er det Jon tænkte når han sagde man kunne delete by ID og det er også rigtig nok, det kræver et re-write af hvordan dataen bliver gemt.
        db.collection(finalDb).addSnapshotListener((values, error) -> {
            for (DocumentSnapshot snap : values.getDocuments()) {
                String checkId = snap.getString("title");
                if (checkId.equals(id)) {
                    try {
                        snap.getReference().delete();
                        stoRef.delete();
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }
            activity.update(null);
        });
    }

    public static Repo r() {
        return repo;
    }
}
