package com.example.movieapp.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.movieapp.R;
import com.example.movieapp.databinding.LoginLayoutBinding;

public class LoginFragment extends Fragment {

    private LoginLayoutBinding binding;
    private AuthViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = LoginLayoutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        setupLoginButton();
        setupSignupLink();
        viewModel.loginSignupUiState.observe(getViewLifecycleOwner(), this::renderState);
        viewModel.checkSession();
    }

    private void setupLoginButton() {
        binding.loginButton.setOnClickListener(v -> {
            String email = binding.loginEmailInput.getText().toString().trim();
            String password = binding.loginPasswordInput.getText().toString().trim();
            viewModel.login(email, password);
        });
    }

    private void setupSignupLink() {
        binding.signupLinkButton.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(R.id.action_loginFragment_to_signupFragment);
        });
    }

    private void showError(String message) {
        binding.loginError.setText(message);
        binding.loginError.setVisibility(View.VISIBLE);
    }

    private void renderState(LoginSignupUiState state) {
        if (state == null) {
            return;
        }

        switch (state.getStatus()) {
            case AUTHENTICATED:
                binding.loginError.setVisibility(View.GONE);
                Navigation.findNavController(binding.getRoot())
                        .navigate(R.id.action_loginFragment_to_movieListFragment);
                break;
            case ERROR:
                if (state.getMessage() != null) {
                    showError(state.getMessage());
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
