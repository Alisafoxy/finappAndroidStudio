//package com.dtafox.finalappro.ui;
//
//import androidx.lifecycle.ViewModelProvider;
//
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.navigation.fragment.NavHostFragment;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ProgressBar;
//import android.widget.Toast;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.auth.AuthResult;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//
//
//import com.dtafox.finalappro.R;
//
////
//public class LoginFragment extends Fragment {
//
//    private ProgressBar progressBar;
//    private FirebaseAuth firebaseAuth;
//    private LoginViewModel mViewModel;
//    private Button buttonLogin;
//    private EditText editTextEmail, editTextPassword;
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_login, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        progressBar = view.findViewById(R.id.progressBarLogin);
//        progressBar.setVisibility(View.VISIBLE);
//        buttonLogin.setEnabled(false);
//
//        mViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
//
//        editTextEmail = view.findViewById(R.id.editTextEmail);
//        editTextPassword = view.findViewById(R.id.editTextPassword);
//        firebaseAuth = FirebaseAuth.getInstance();
//        buttonLogin = view.findViewById(R.id.buttonLogin);
//
//        // כאשר המשתמש לוחץ על כפתור ההתחברות
//
//        buttonLogin.setOnClickListener(v -> {
//            String email = editTextEmail.getText().toString().trim();
//            String password = editTextPassword.getText().toString().trim();
//
//            if (email.isEmpty() || password.isEmpty()) {
//                Toast.makeText(getContext(), "יש למלא אימייל וסיסמה", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            progressBar.setVisibility(View.VISIBLE);
//            buttonLogin.setEnabled(false);
//
//            firebaseAuth.signInWithEmailAndPassword(email, password)
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            FirebaseUser user = firebaseAuth.getCurrentUser();
//
//                            SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
//                            sharedPreferences.edit().putBoolean(getString(R.string.user_logon_state), true).apply();
//
//                            Toast.makeText(getContext(), "ברוך הבא " + user.getEmail(), Toast.LENGTH_SHORT).show();
//                            progressBar.setVisibility(View.GONE);
//                            buttonLogin.setEnabled(true);
//                            NavHostFragment.findNavController(this).navigate(R.id.taskListFragment);
//                        } else {
//                            // יצירת משתמש חדש
//                            firebaseAuth.createUserWithEmailAndPassword(email, password)
//                                    .addOnCompleteListener(createTask -> {
//                                        progressBar.setVisibility(View.GONE);
//                                        buttonLogin.setEnabled(true);
//
//                                        if (createTask.isSuccessful()) {
//                                            FirebaseUser newUser = firebaseAuth.getCurrentUser();
//                                            Toast.makeText(getContext(), "משתמש חדש נוצר בהצלחה!", Toast.LENGTH_SHORT).show();
//                                            NavHostFragment.findNavController(this).navigate(R.id.taskListFragment);
//                                        } else {
//                                            Toast.makeText(getContext(), "ההתחברות נכשלה: " + createTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                        }
//                    });
//        });
//
//    }
//}
package com.dtafox.finalappro.ui;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {

    private ProgressBar progressBar;
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin, buttonRegister;
    private LoginViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // קישור לרכיבי UI
        progressBar     = view.findViewById(R.id.progressBarLogin);
        editTextEmail   = view.findViewById(R.id.editTextEmail);
        editTextPassword= view.findViewById(R.id.editTextPassword);
        buttonLogin     = view.findViewById(R.id.buttonLogin);
        buttonRegister  = view.findViewById(R.id.buttonRegister);

        progressBar.setVisibility(View.GONE);

        // ViewModel
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // לחיצה על כפתור "התחבר / הירשם"
        buttonLogin.setOnClickListener(v -> {
            String email    = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(getContext(), "יש למלא אימייל וסיסמה", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            buttonLogin.setEnabled(false);

            viewModel.loginOrRegister(email, password, new LoginViewModel.LoginCallback() {
                @Override
                public void onSuccess(FirebaseUser user, boolean isNewUser) {
                    // שמירת מצב התחברות
                    SharedPreferences prefs = requireActivity()
                            .getPreferences(Context.MODE_PRIVATE);
                    prefs.edit()
                            .putBoolean(getString(R.string.user_logon_state), true)
                            .apply();

                    String msg = isNewUser
                            ? "משתמש חדש נוצר!"
                            : "ברוך הבא " + user.getEmail();
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();

                    progressBar.setVisibility(View.GONE);
                    buttonLogin.setEnabled(true);

                    // מעבר לרשימת המשימות
                    NavHostFragment.findNavController(LoginFragment.this)
                            .navigate(R.id.taskListFragment);
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(getContext(), "שגיאה: " + message, Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    buttonLogin.setEnabled(true);
                }
            });
        });

        // לחיצה על כפתור "יצירת חשבון"
        buttonRegister.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_loginFragment_to_registerFragment);
        });
    }
}
