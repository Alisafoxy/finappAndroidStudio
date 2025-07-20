package com.dtafox.finalappro.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.dtafox.finalappro.R;
import com.dtafox.finalappro.databinding.FragmentProfileBinding;
import com.dtafox.finalappro.models.User;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            binding = FragmentProfileBinding.inflate(inflater, container, false);
            return binding.getRoot();
        } catch (Exception e) {
            // If view binding fails, create a simple error view
            return createErrorView(inflater, container);
        }
    }

    private View createErrorView(LayoutInflater inflater, ViewGroup container) {
        View errorView = inflater.inflate(android.R.layout.simple_list_item_1, container, false);
        // Show error message
        Toast.makeText(getContext(), "Error loading profile. Please check your app setup.", Toast.LENGTH_LONG).show();
        return errorView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        try {
            if (binding == null) {
                Toast.makeText(getContext(), "Profile view not available", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
            
            setupObservers();
            setupClickListeners();
            
            // Load user data
            viewModel.loadUserProfile();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error initializing profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupObservers() {
        if (viewModel == null) return;
        
        try {
            // Observe user data
            viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
                if (user != null) {
                    populateUserData(user);
                }
            });

            // Observe loading state
            viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
                if (binding != null) {
                    binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                    binding.btnSaveProfile.setEnabled(!isLoading);
                }
            });

            // Observe error messages
            viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
                if (error != null && !error.isEmpty()) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                }
            });

            // Observe success messages
            viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message -> {
                if (message != null && !message.isEmpty()) {
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
            });

            // Observe logout event
            viewModel.getLogoutSuccess().observe(getViewLifecycleOwner(), success -> {
                if (success && binding != null) {
                    try {
                        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_global_navigate_to_login);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Navigation error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error setting up observers: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupClickListeners() {
        if (binding == null) return;
        
        try {
            binding.btnSaveProfile.setOnClickListener(v -> saveProfile());
            binding.btnLogout.setOnClickListener(v -> {
                if (viewModel != null) {
                    viewModel.logout();
                }
            });
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error setting up click listeners: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void populateUserData(User user) {
        if (binding == null || user == null) return;
        
        try {
            if (user.getEmail() != null) {
                binding.etEmail.setText(user.getEmail());
            }
            
            if (user.getName() != null) {
                binding.etName.setText(user.getName());
            }
            
            // Set gender radio button
            if (user.getGender() != null && !user.getGender().isEmpty()) {
                switch (user.getGender().toLowerCase()) {
                    case "male":
                        binding.rbMale.setChecked(true);
                        break;
                    case "female":
                        binding.rbFemale.setChecked(true);
                        break;
                    case "other":
                        binding.rbOther.setChecked(true);
                        break;
                }
            }
            
            if (user.getAge() > 0) {
                binding.etAge.setText(String.valueOf(user.getAge()));
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error populating user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProfile() {
        if (binding == null || viewModel == null) return;
        
        try {
            String name = binding.etName.getText().toString().trim();
            String ageStr = binding.etAge.getText().toString().trim();
            
            if (name.isEmpty()) {
                binding.tilName.setError(getString(R.string.name_required));
                return;
            }
            
            int age = 0;
            if (!ageStr.isEmpty()) {
                try {
                    age = Integer.parseInt(ageStr);
                    if (age <= 0 || age > 150) {
                        binding.tilAge.setError(getString(R.string.age_invalid));
                        return;
                    }
                } catch (NumberFormatException e) {
                    binding.tilAge.setError(getString(R.string.age_invalid));
                    return;
                }
            }
            
            // Clear errors
            binding.tilName.setError(null);
            binding.tilAge.setError(null);
            
            // Get selected gender
            String gender = "";
            if (binding.rbMale.isChecked()) {
                gender = "Male";
            } else if (binding.rbFemale.isChecked()) {
                gender = "Female";
            } else if (binding.rbOther.isChecked()) {
                gender = "Other";
            }
            
            // Create updated user object
            User currentUser = viewModel.getUser().getValue();
            if (currentUser != null) {
                currentUser.setName(name);
                currentUser.setGender(gender);
                currentUser.setAge(age);
                
                viewModel.saveUserProfile(currentUser);
            } else {
                Toast.makeText(getContext(), "No user data available", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error saving profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 