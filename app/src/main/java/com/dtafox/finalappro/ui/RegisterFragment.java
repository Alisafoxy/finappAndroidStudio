package com.dtafox.finalappro.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.dtafox.finalappro.R;

public class RegisterFragment extends Fragment {

    private RegisterViewModel mViewModel;

    private EditText editTextFullName, editTextEmail, editTextPassword, editTextConfirmPassword;
    private Button buttonRegister;
    private TextView textViewAlreadyHaveAccount;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        // קישור לרכיבים ב־XML
        editTextFullName = view.findViewById(R.id.editTextFullName);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        editTextConfirmPassword = view.findViewById(R.id.editTextConfirmPassword);
        buttonRegister = view.findViewById(R.id.buttonRegister);
        textViewAlreadyHaveAccount = view.findViewById(R.id.textViewAlreadyHaveAccount);

        buttonRegister.setOnClickListener(v -> {
            String fullName = editTextFullName.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String confirmPassword = editTextConfirmPassword.getText().toString().trim();

            if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) ||
                    TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(getContext(), "נא למלא את כל השדות", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(getContext(), "הסיסמאות לא תואמות", Toast.LENGTH_SHORT).show();
                return;
            }

            mViewModel.register(fullName, email, password, new RegisterViewModel.RegisterCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getContext(), "נרשמת בהצלחה!", Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(RegisterFragment.this)
                            .navigate(R.id.action_registerFragment_to_loginFragment);
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });

        // לחיצה על "כבר יש לך חשבון?"
        textViewAlreadyHaveAccount.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_registerFragment_to_loginFragment);
        });
    }
}
