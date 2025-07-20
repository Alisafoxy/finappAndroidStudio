//package com.dtafox.finalappro.ui;
//
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//import androidx.lifecycle.ViewModel;
//
//public class LoginViewModel extends ViewModel {
//
//    // שדות לאחסון אימייל וסיסמה בזמן ריצה
//    private final MutableLiveData<String> email = new MutableLiveData<>("");
//    private final MutableLiveData<String> password = new MutableLiveData<>("");
//
//    public LiveData<String> getEmail() {
//        return email;
//    }
//
//    public void setEmail(String value) {
//        email.setValue(value);
//    }
//
//    public LiveData<String> getPassword() {
//        return password;
//    }
//
//    public void setPassword(String value) {
//        password.setValue(value);
//    }
//
//}
package com.dtafox.finalappro.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginViewModel extends ViewModel {

    // שדות לאחסון אימייל וסיסמה – אפשרי ל־DataBinding או UI
    private final MutableLiveData<String> email = new MutableLiveData<>("");
    private final MutableLiveData<String> password = new MutableLiveData<>("");

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public LiveData<String> getEmail() {
        return email;
    }

    public void setEmail(String value) {
        email.setValue(value);
    }

    public LiveData<String> getPassword() {
        return password;
    }

    public void setPassword(String value) {
        password.setValue(value);
    }

    // ממשק Callback ל־LoginFragment
    public interface LoginCallback {
        void onSuccess(FirebaseUser user, boolean isNewUser);
        void onError(String message);
    }

    // פונקציה להתחברות (או רישום אם נכשל)
    public void loginOrRegister(String email, String password, LoginCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        callback.onSuccess(user, false); // התחברות קיימת
                    } else {
                        // ננסה לרשום משתמש חדש
                        firebaseAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(createTask -> {
                                    if (createTask.isSuccessful()) {
                                        FirebaseUser newUser = firebaseAuth.getCurrentUser();
                                        callback.onSuccess(newUser, true); // משתמש חדש נוצר
                                    } else {
                                        String error = (createTask.getException() != null)
                                                ? createTask.getException().getMessage()
                                                : "שגיאה לא ידועה בהרשמה";
                                        callback.onError(error);
                                    }
                                });
                    }
                });
    }
}

