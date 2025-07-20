package com.dtafox.finalappro.firebase;

import com.google.firebase.auth.FirebaseAuth;

public class FirebaseAuthManager {

    private final FirebaseAuth auth;

    public FirebaseAuthManager() {
        auth = FirebaseAuth.getInstance();
    }

    public interface OnLogoutListener {
        void onSuccess();
        void onFailure(String error);
    }

    public void logout(OnLogoutListener listener) {
        try {
            auth.signOut();
            listener.onSuccess();
        } catch (Exception e) {
            listener.onFailure(e.getMessage());
        }
    }

    public boolean isUserLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public String getCurrentUserId() {
        return auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
    }

    public String getCurrentUserEmail() {
        return auth.getCurrentUser() != null ? auth.getCurrentUser().getEmail() : null;
    }
}
