package com.dtafox.finalappro.ui;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dtafox.finalappro.models.task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TaskListViewModel extends ViewModel {
    private static final String TAG = "TaskListViewModel";
    private static final String TASKS_COLLECTION = "tasks";
    
    private final MutableLiveData<List<task>> tasks = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public TaskListViewModel() {
        Log.d(TAG, "TaskListViewModel created");
        // Initialize with empty list
        tasks.setValue(new ArrayList<>());
        loadTasksFromFirestore();
    }

    public LiveData<List<task>> getTasks() {
        return tasks;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void refreshTasks() {
        Log.d(TAG, "refreshTasks() called");
        loadTasksFromFirestore();
    }

    private void loadTasksFromFirestore() {
        Log.d(TAG, "loadTasksFromFirestore() started");
        
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.w(TAG, "No user logged in, cannot load tasks");
            tasks.setValue(new ArrayList<>());
            errorMessage.setValue("User not logged in");
            return;
        }

        String userId = user.getUid();
        Log.d(TAG, "Loading tasks for user: " + user.getEmail() + " (ID: " + userId + ")");

        isLoading.setValue(true);
        errorMessage.setValue("");

        // Simplified query without orderBy to avoid FAILED_PRECONDITION error
        db.collection(TASKS_COLLECTION)
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Log.d(TAG, "Firebase query successful, found " + querySnapshot.size() + " documents");
                    isLoading.setValue(false);
                    
                    List<task> taskList = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        try {
                            String id = doc.getId();
                            String title = doc.getString("title");
                            String description = doc.getString("description");
                            String date = doc.getString("date");
                            String docUserId = doc.getString("userId");
                            Long timestamp = doc.getLong("timestamp");
                            
                            Log.d(TAG, "Processing document: " + id);
                            Log.d(TAG, "  Title: " + title);
                            Log.d(TAG, "  Description: " + description);
                            Log.d(TAG, "  Date: " + date);
                            Log.d(TAG, "  UserId: " + docUserId);
                            Log.d(TAG, "  Timestamp: " + timestamp);
                            
                            if (title != null && description != null && date != null) {
                                task newTask = new task(id, title, description, date);
                                newTask.setUserId(docUserId);
                                taskList.add(newTask);
                                Log.d(TAG, "Task added to list: " + title);
                            } else {
                                Log.w(TAG, "Skipping task with null fields - ID: " + id);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error processing document: " + doc.getId(), e);
                        }
                    }
                    
                    // Sort tasks by timestamp locally (newest first)
                    try {
                        Collections.sort(taskList, new Comparator<task>() {
                            @Override
                            public int compare(task t1, task t2) {
                                // For now, sort by title since we don't have timestamp in task model
                                // You can improve this later by adding timestamp to task model
                                return t2.getTitle().compareTo(t1.getTitle());
                            }
                        });
                    } catch (Exception e) {
                        Log.w(TAG, "Could not sort tasks: " + e.getMessage());
                    }
                    
                    Log.d(TAG, "Final task list size: " + taskList.size());
                    tasks.setValue(taskList);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Firebase query failed", e);
                    isLoading.setValue(false);
                    String errorMsg = "Failed to load tasks: " + e.getMessage();
                    errorMessage.setValue(errorMsg);
                    tasks.setValue(new ArrayList<>()); // Show empty list on error
                });
    }

    public void deleteTask(task taskToDelete, OnTaskDeletedListener listener) {
        if (taskToDelete == null || taskToDelete.getId() == null || taskToDelete.getId().isEmpty()) {
            Log.e(TAG, "Cannot delete task - invalid task or ID");
            listener.onFailure("Invalid task ID");
            return;
        }

        String taskId = taskToDelete.getId();
        Log.d(TAG, "Deleting task: " + taskId + " (" + taskToDelete.getTitle() + ")");

        db.collection(TASKS_COLLECTION)
                .document(taskId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Task deleted successfully: " + taskId);
                    listener.onSuccess();
                    refreshTasks(); // Refresh the list
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to delete task: " + taskId, e);
                    listener.onFailure("Failed to delete task: " + e.getMessage());
                });
    }

    public interface OnTaskDeletedListener {
        void onSuccess();
        void onFailure(String error);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "TaskListViewModel cleared");
    }
}
