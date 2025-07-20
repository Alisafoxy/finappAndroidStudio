package com.dtafox.finalappro.ui;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dtafox.finalappro.models.task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddTaskViewModel extends ViewModel {

    private static final String TAG = "AddTaskViewModel";
    private static final String TASKS_COLLECTION = "tasks";
    
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();

    public interface OnTaskSavedListener {
        void onSuccess();
        void onFailure(String error);
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

    public void saveTaskToFirebase(String title, String description, String date, OnTaskSavedListener listener) {
        Log.d(TAG, "saveTaskToFirebase called");
        
        // Input validation
        if (title == null || title.trim().isEmpty()) {
            Log.e(TAG, "Title is null or empty");
            listener.onFailure("Title cannot be empty");
            return;
        }
        
        if (description == null || description.trim().isEmpty()) {
            Log.e(TAG, "Description is null or empty");
            listener.onFailure("Description cannot be empty");
            return;
        }
        
        if (date == null || date.trim().isEmpty()) {
            Log.e(TAG, "Date is null or empty");
            listener.onFailure("Date cannot be empty");
            return;
        }
        
        Log.d(TAG, "Title: " + title);
        Log.d(TAG, "Description: " + description);
        Log.d(TAG, "Date: " + date);
        
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "No user logged in");
            listener.onFailure("User not logged in");
            return;
        }

        String userId = currentUser.getUid();
        String userEmail = currentUser.getEmail();
        Log.d(TAG, "Current user: " + userEmail + " (ID: " + userId + ")");

        if (userId == null || userId.isEmpty()) {
            Log.e(TAG, "User ID is null or empty");
            listener.onFailure("Invalid user ID");
            return;
        }

        isLoading.setValue(true);
        errorMessage.setValue("");
        successMessage.setValue("");

        // Create task map for Firestore with proper validation
        Map<String, Object> taskData = new HashMap<>();
        taskData.put("title", title.trim());
        taskData.put("description", description.trim());
        taskData.put("date", date.trim());
        taskData.put("userId", userId);
        taskData.put("timestamp", System.currentTimeMillis());

        Log.d(TAG, "Saving task data to Firestore: " + taskData.toString());

        try {
            db.collection(TASKS_COLLECTION)
                    .add(taskData)
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "Task saved successfully with ID: " + documentReference.getId());
                        isLoading.setValue(false);
                        successMessage.setValue("Task saved successfully!");
                        listener.onSuccess();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to save task", e);
                        isLoading.setValue(false);
                        String error = "Failed to save task: " + e.getMessage();
                        errorMessage.setValue(error);
                        listener.onFailure(error);
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception while saving task", e);
            isLoading.setValue(false);
            String error = "Error saving task: " + e.getMessage();
            errorMessage.setValue(error);
            listener.onFailure(error);
        }
    }
}
