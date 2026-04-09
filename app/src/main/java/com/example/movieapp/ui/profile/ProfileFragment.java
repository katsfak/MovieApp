package com.example.movieapp.ui.profile;

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

import com.example.movieapp.R;
import com.example.movieapp.databinding.FragmentProfileBinding;
import com.example.movieapp.ui.auth.AuthViewModel;
import com.example.movieapp.ui.auth.ProfileUiState;

import dagger.hilt.android.AndroidEntryPoint;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

@AndroidEntryPoint
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private AuthViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        binding.backButton.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        binding.editCredentialsButton.setOnClickListener(v -> showEditCredentialsDialog());

        binding.showFavoritesButton.setOnClickListener(
                v -> Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_favoritesFragment));

        binding.switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!buttonView.isPressed()) {
                return;
            }
            viewModel.updateDarkMode(isChecked);
        });

        binding.logoutButton.setOnClickListener(v -> new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.logout)
                .setMessage(R.string.logout_confirmation)
                .setPositiveButton(R.string.logout, (dialog, which) -> viewModel.logout())
                .setNegativeButton(R.string.cancel, null)
                .show());

        viewModel.profileUiState.observe(getViewLifecycleOwner(), this::renderState);
        viewModel.loadProfile();
    }

    private void showEditCredentialsDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_credentials, null, false);
        TextInputEditText emailInput = dialogView.findViewById(R.id.dialog_email_input);
        TextInputEditText passwordInput = dialogView.findViewById(R.id.dialog_password_input);

        if (emailInput != null) {
            emailInput.setText(viewModel.getRawEmail());
        }
        if (passwordInput != null) {
            passwordInput.setText(viewModel.getRawPassword());
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    String newEmail = String.valueOf(emailInput != null ? emailInput.getText() : null).trim();
                    String newPassword = String.valueOf(passwordInput != null ? passwordInput.getText() : null).trim();
                    viewModel.updateCredentials(newEmail, newPassword);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void renderState(ProfileUiState state) {
        if (state == null) {
            return;
        }

        switch (state.getStatus()) {
            case PROFILE_LOADED:
            case CREDENTIALS_UPDATED:
                binding.emailValue.setText(state.getEmail());
                binding.passwordValue.setText(state.getMaskedPassword());
                binding.switchDarkMode.setChecked(state.isDarkModeEnabled());
                if (state.getStatus() == ProfileUiState.Status.CREDENTIALS_UPDATED && state.getMessage() != null) {
                    Toast.makeText(requireContext(), state.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
            case LOGGED_OUT:
                Navigation.findNavController(binding.getRoot())
                        .navigate(R.id.action_profileFragment_to_loginFragment);
                break;
            case ERROR:
                if (state.getMessage() != null) {
                    Toast.makeText(requireContext(), state.getMessage(), Toast.LENGTH_SHORT).show();
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
