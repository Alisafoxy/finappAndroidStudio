package com.dtafox.finalappro.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dtafox.finalappro.R;
import com.dtafox.finalappro.adapters.TaskAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class TaskListFragment extends Fragment {

    private static final String TAG = "TaskListFragment";
    private TaskListViewModel mViewModel;
    private TaskAdapter taskAdapter;
    private ProgressBar progressBar;
    private TextView emptyTextView;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        return inflater.inflate(R.layout.fragment_task_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated called");

        // Test Firebase connectivity first
        testFirebaseConnectivity();

        // Check if user is logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "Current user: " + currentUser.getEmail() + " (ID: " + currentUser.getUid() + ")");
        } else {
            Log.w(TAG, "No user logged in!");
        }

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerViewTasks);
        progressBar = view.findViewById(R.id.progressBarTasks);
        emptyTextView = view.findViewById(R.id.textViewEmpty);

        if (recyclerView == null) {
            Log.e(TAG, "RecyclerView not found in layout!");
            return;
        }

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        taskAdapter = new TaskAdapter(new ArrayList<>());
        recyclerView.setAdapter(taskAdapter);
        Log.d(TAG, "RecyclerView and adapter setup complete");

        // Initialize ViewModel
        mViewModel = new ViewModelProvider(this).get(TaskListViewModel.class);
        Log.d(TAG, "ViewModel initialized");

        setupObservers();
        setupTaskAdapter();
        
        // Manually trigger task loading
        Log.d(TAG, "Triggering task refresh");
        mViewModel.refreshTasks();
    }

    private void testFirebaseConnectivity() {
        Log.d(TAG, "Testing Firebase connectivity...");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        
        if (currentUser == null) {
            Log.e(TAG, "Cannot test Firebase - no user logged in");
            return;
        }

        // Simple Firebase test - try to read from tasks collection
        FirebaseFirestore.getInstance()
                .collection("tasks")
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Log.d(TAG, "Firebase connectivity test: SUCCESS");
                    Log.d(TAG, "Firebase is accessible, found " + querySnapshot.size() + " documents in test query");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Firebase connectivity test: FAILED", e);
                    Toast.makeText(getContext(), "Firebase connection error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void setupObservers() {
        Log.d(TAG, "Setting up observers");
        
        // Observe tasks
        mViewModel.getTasks().observe(getViewLifecycleOwner(), tasks -> {
            Log.d(TAG, "Tasks received: " + (tasks != null ? tasks.size() : 0) + " tasks");
            if (tasks != null) {
                for (int i = 0; i < tasks.size(); i++) {
                    Log.d(TAG, "Task " + i + ": " + tasks.get(i).getTitle());
                }
            }
            taskAdapter.setTaskList(tasks != null ? tasks : new ArrayList<>());
            updateEmptyState(tasks == null || tasks.isEmpty());
        });

        // Observe loading state
        mViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            Log.d(TAG, "Loading state: " + isLoading);
            if (progressBar != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        // Observe error messages
        mViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Log.e(TAG, "Error received: " + error);
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupTaskAdapter() {
        Log.d(TAG, "Setting up task adapter");
        
        // Setup long click listener for task deletion
        taskAdapter.setOnTaskLongClickListener(taskToDelete -> {
            Log.d(TAG, "Long click on task: " + taskToDelete.getTitle());
            mViewModel.deleteTask(taskToDelete, new TaskListViewModel.OnTaskDeletedListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "Task deleted successfully");
                    Toast.makeText(getContext(), "Task deleted successfully", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String error) {
                    Log.e(TAG, "Failed to delete task: " + error);
                    Toast.makeText(getContext(), "Delete failed: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void updateEmptyState(boolean isEmpty) {
        Log.d(TAG, "Updating empty state: " + isEmpty);
        if (emptyTextView != null) {
            emptyTextView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");

        // Check login status
        SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
        boolean isUserLoggedIn = sharedPreferences.getBoolean(getString(R.string.user_logon_state), false);
        
        Log.d(TAG, "User logged in (SharedPrefs): " + isUserLoggedIn);
        
        if (!isUserLoggedIn) {
            Log.w(TAG, "User not logged in, navigating to login");
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_global_navigate_to_login);
        } else {
            // Refresh tasks when returning to this fragment
            Log.d(TAG, "Refreshing tasks on resume");
            if (mViewModel != null) {
                mViewModel.refreshTasks();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
    }
}
