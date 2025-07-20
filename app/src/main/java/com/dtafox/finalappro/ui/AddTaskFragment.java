package com.dtafox.finalappro.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.dtafox.finalappro.R;

public class AddTaskFragment extends Fragment {

    private AddTaskViewModel mViewModel;
    private EditText editTextTaskTitle, editTextTaskDescription, editTextTaskDate;
    private Button buttonSaveTask;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(AddTaskViewModel.class);

        // Initialize views
        editTextTaskTitle = view.findViewById(R.id.editTextTaskTitle);
        editTextTaskDescription = view.findViewById(R.id.editTextTaskDescription);
        editTextTaskDate = view.findViewById(R.id.editTextTaskDate);
        buttonSaveTask = view.findViewById(R.id.buttonSaveTask);
        
        // Add progress bar (we'll add this to layout)
        progressBar = view.findViewById(R.id.progressBarAddTask);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        setupObservers();
        setupClickListeners();
    }

    private void setupObservers() {
        // Observe loading state
        mViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (progressBar != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
            buttonSaveTask.setEnabled(!isLoading);
        });

        // Observe error messages
        mViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        // Observe success messages
        mViewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        buttonSaveTask.setOnClickListener(v -> saveTask());
    }

    private void saveTask() {
        String title = editTextTaskTitle.getText().toString().trim();
        String description = editTextTaskDescription.getText().toString().trim();
        String date = editTextTaskDate.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(title)) {
            editTextTaskTitle.setError("Title is required");
            editTextTaskTitle.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            editTextTaskDescription.setError("Description is required");
            editTextTaskDescription.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(date)) {
            editTextTaskDate.setError("Date is required");
            editTextTaskDate.requestFocus();
            return;
        }

        // Clear any previous errors
        editTextTaskTitle.setError(null);
        editTextTaskDescription.setError(null);
        editTextTaskDate.setError(null);

        // Save to Firebase
        mViewModel.saveTaskToFirebase(title, description, date, new AddTaskViewModel.OnTaskSavedListener() {
            @Override
            public void onSuccess() {
                // Clear form
                editTextTaskTitle.setText("");
                editTextTaskDescription.setText("");
                editTextTaskDate.setText("");
                
                // Navigate back to task list
                try {
                    NavHostFragment.findNavController(AddTaskFragment.this)
                            .navigate(R.id.taskListFragment);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Saved successfully! Go to Tasks tab to see your task.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(String error) {
                // Error is already handled by ViewModel observer
            }
        });
    }
}
