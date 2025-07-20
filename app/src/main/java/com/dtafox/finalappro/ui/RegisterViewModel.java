package com.dtafox.finalappro.ui;

import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterViewModel extends ViewModel {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface RegisterCallback {
        void onSuccess();
        void onError(String errorMessage);
    }

    public void register(String fullName, String email, String password, RegisterCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("fullName", fullName);
                        userMap.put("email", email);

                        db.collection("users")
                                .document(user.getUid())
                                .set(userMap)
                                .addOnSuccessListener(aVoid -> callback.onSuccess())
                                .addOnFailureListener(e -> callback.onError("שגיאה בשמירת פרטי המשתמש: " + e.getMessage()));
                    } else {
                        callback.onError("שגיאה בהרשמה: " + task.getException().getMessage());
                    }
                });
    }
}
