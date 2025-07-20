package com.dtafox.finalappro.firebase;

import com.dtafox.finalappro.models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class FirestoreRepository {

    private final FirebaseFirestore db;
    private static final String USERS_COLLECTION = "users";

    public FirestoreRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public interface OnUserProfileLoadedListener {
        void onSuccess(User user);
        void onFailure(String error);
    }

    public interface OnUserProfileSavedListener {
        void onSuccess();
        void onFailure(String error);
    }

    public void getUserProfile(String userId, OnUserProfileLoadedListener listener) {
        db.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            User user = document.toObject(User.class);
                            if (user != null) {
                                user.setUserId(userId);
                                listener.onSuccess(user);
                            } else {
                                listener.onFailure("Failed to parse user data");
                            }
                        } else {
                            listener.onFailure("User profile not found");
                        }
                    } else {
                        listener.onFailure(task.getException() != null ? 
                                         task.getException().getMessage() : "Unknown error");
                    }
                });
    }

    public void saveUserProfile(User user, OnUserProfileSavedListener listener) {
        db.collection(USERS_COLLECTION)
                .document(user.getUserId())
                .set(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onSuccess();
                    } else {
                        listener.onFailure(task.getException() != null ? 
                                         task.getException().getMessage() : "Unknown error");
                    }
                });
    }
}
