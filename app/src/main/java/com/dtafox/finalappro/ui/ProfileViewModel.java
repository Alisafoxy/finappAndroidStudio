//package com.dtafox.finalappro.ui;
//
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//import androidx.lifecycle.ViewModel;
//
//import com.dtafox.finalappro.firebase.FirebaseAuthManager;
//import com.dtafox.finalappro.firebase.FirestoreRepository;
//import com.dtafox.finalappro.models.User;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
//public class ProfileViewModel extends ViewModel {
//
//    private final FirebaseAuthManager authManager;
//    private final FirestoreRepository firestoreRepository;
//
//    private final MutableLiveData<User> user = new MutableLiveData<>();
//    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
//    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
//    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
//    private final MutableLiveData<Boolean> logoutSuccess = new MutableLiveData<>(false);
//
//    public ProfileViewModel() {
//        authManager = new FirebaseAuthManager();
//        firestoreRepository = new FirestoreRepository();
//    }
//
//    public LiveData<User> getUser() {
//        return user;
//    }
//
//    public LiveData<Boolean> getIsLoading() {
//        return isLoading;
//    }
//
//    public LiveData<String> getErrorMessage() {
//        return errorMessage;
//    }
//
//    public LiveData<String> getSuccessMessage() {
//        return successMessage;
//    }
//
//    public LiveData<Boolean> getLogoutSuccess() {
//        return logoutSuccess;
//    }
//
//    public void loadUserProfile() {
//        isLoading.setValue(true);
//        errorMessage.setValue("");
//
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser != null) {
//            firestoreRepository.getUserProfile(currentUser.getUid(),
//                new FirestoreRepository.OnUserProfileLoadedListener() {
//                    @Override
//                    public void onSuccess(User loadedUser) {
//                        isLoading.setValue(false);
//                        user.setValue(loadedUser);
//                    }
//
//                    @Override
//                    public void onFailure(String error) {
//                        isLoading.setValue(false);
//                        // If user profile doesn't exist, create one with basic info
//                        User newUser = new User(currentUser.getEmail(),
//                                              currentUser.getDisplayName() != null ?
//                                              currentUser.getDisplayName() : "");
//                        newUser.setUserId(currentUser.getUid());
//                        user.setValue(newUser);
//                    }
//                });
//        } else {
//            isLoading.setValue(false);
//            errorMessage.setValue("No user logged in");
//        }
//    }
//
//    public void saveUserProfile(User userToSave) {
//        isLoading.setValue(true);
//        errorMessage.setValue("");
//        successMessage.setValue("");
//
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser != null) {
//            userToSave.setUserId(currentUser.getUid());
//            userToSave.setEmail(currentUser.getEmail());
//
//            firestoreRepository.saveUserProfile(userToSave,
//                new FirestoreRepository.OnUserProfileSavedListener() {
//                    @Override
//                    public void onSuccess() {
//                        isLoading.setValue(false);
//                        successMessage.setValue("Profile saved successfully!");
//                        user.setValue(userToSave);
//                    }
//
//                    @Override
//                    public void onFailure(String error) {
//                        isLoading.setValue(false);
//                        errorMessage.setValue("Failed to save profile: " + error);
//                    }
//                });
//        } else {
//            isLoading.setValue(false);
//            errorMessage.setValue("No user logged in");
//        }
//    }
//
//    public void logout() {
//        authManager.logout(new FirebaseAuthManager.OnLogoutListener() {
//            @Override
//            public void onSuccess() {
//                logoutSuccess.setValue(true);
//            }
//
//            @Override
//            public void onFailure(String error) {
//                errorMessage.setValue("Logout failed: " + error);
//            }
//        });
//    }
//}
package com.dtafox.finalappro.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dtafox.finalappro.firebase.FirebaseAuthManager;
import com.dtafox.finalappro.firebase.FirestoreRepository;
import com.dtafox.finalappro.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileViewModel extends ViewModel {

    private final FirebaseAuthManager authManager;
    private final FirestoreRepository firestoreRepository;

    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> logoutSuccess = new MutableLiveData<>(false);

    public ProfileViewModel() {
        authManager = new FirebaseAuthManager();
        firestoreRepository = new FirestoreRepository();
    }

    public LiveData<User> getUser() {
        return user;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public LiveData<Boolean> getLogoutSuccess() {
        return logoutSuccess;
    }

    public void loadUserProfile() {
        isLoading.setValue(true);
        errorMessage.setValue("");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            firestoreRepository.getUserProfile(currentUser.getUid(),
                    new FirestoreRepository.OnUserProfileLoadedListener() {
                        @Override
                        public void onSuccess(User loadedUser) {
                            isLoading.setValue(false);
                            user.setValue(loadedUser);
                        }

                        @Override
                        public void onFailure(String error) {
                            isLoading.setValue(false);

                            // יצירת פרופיל חדש עם נתונים בסיסיים
                            User newUser = new User(
                                    currentUser.getEmail(),
                                    currentUser.getDisplayName() != null ? currentUser.getDisplayName() : ""
                            );
                            newUser.setUserId(currentUser.getUid());

                            // שמירת המשתמש החדש בבסיס הנתונים
                            firestoreRepository.saveUserProfile(newUser, new FirestoreRepository.OnUserProfileSavedListener() {
                                @Override
                                public void onSuccess() {
                                    user.setValue(newUser);
                                    successMessage.setValue("New profile created successfully.");
                                }

                                @Override
                                public void onFailure(String saveError) {
                                    errorMessage.setValue("Failed to create new profile: " + saveError);
                                }
                            });
                        }
                    });
        } else {
            isLoading.setValue(false);
            errorMessage.setValue("No user logged in");
        }
    }

    public void saveUserProfile(User userToSave) {
        isLoading.setValue(true);
        errorMessage.setValue("");
        successMessage.setValue("");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userToSave.setUserId(currentUser.getUid());
            userToSave.setEmail(currentUser.getEmail());

            firestoreRepository.saveUserProfile(userToSave,
                    new FirestoreRepository.OnUserProfileSavedListener() {
                        @Override
                        public void onSuccess() {
                            isLoading.setValue(false);
                            successMessage.setValue("Profile saved successfully!");
                            user.setValue(userToSave);
                        }

                        @Override
                        public void onFailure(String error) {
                            isLoading.setValue(false);
                            errorMessage.setValue("Failed to save profile: " + error);
                        }
                    });
        } else {
            isLoading.setValue(false);
            errorMessage.setValue("No user logged in");
        }
    }

    public void logout() {
        authManager.logout(new FirebaseAuthManager.OnLogoutListener() {
            @Override
            public void onSuccess() {
                logoutSuccess.setValue(true);
            }

            @Override
            public void onFailure(String error) {
                errorMessage.setValue("Logout failed: " + error);
            }
        });
    }
}
