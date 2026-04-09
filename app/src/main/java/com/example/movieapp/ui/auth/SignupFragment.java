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
import com.example.movieapp.databinding.SignupLayoutBinding;

public class SignupFragment extends Fragment {

    private SignupLayoutBinding binding;
    private AuthViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = SignupLayoutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        setupSignupButton();
        setupLoginLink();
        viewModel.loginSignupUiState.observe(getViewLifecycleOwner(), this::renderState);
    }

    private void setupSignupButton() {
        binding.signupButton.setOnClickListener(v -> {
            String email = binding.signupEmailInput.getText().toString().trim();
            String password = binding.signupPasswordInput.getText().toString().trim();
            viewModel.signUp(email, password);
        });
    }

    private void setupLoginLink() {
        binding.loginLinkButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigateUp();
        });
    }

    private void showError(String message) {
        binding.signupError.setText(message);
        binding.signupError.setVisibility(View.VISIBLE);
    }

    private void renderState(LoginSignupUiState state) {
        if (state == null) {
            return;
        }

        switch (state.getStatus()) {
            case AUTHENTICATED:
                binding.signupError.setVisibility(View.GONE);
                Navigation.findNavController(binding.getRoot())
                        .navigate(R.id.action_signupFragment_to_movieListFragment);
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
