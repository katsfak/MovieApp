package com.example.movieapp.ui.profile;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
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

        binding.showFavoritesButton.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(R.id.action_profileFragment_to_favoritesFragment);
        });

        binding.switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!buttonView.isPressed()) {
                return;
            }
            viewModel.updateDarkMode(isChecked);
        });

        binding.logoutButton.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.logout)
                    .setMessage(R.string.logout_confirmation)
                    .setPositiveButton(R.string.logout, (dialog, which) -> viewModel.logout())
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        });

        viewModel.profileUiState.observe(getViewLifecycleOwner(), this::renderState);
        viewModel.loadProfile();
    }

    private void showEditCredentialsDialog() {
        LinearLayout container = new LinearLayout(requireContext());
        container.setOrientation(LinearLayout.VERTICAL);
        int pad = dp(16);
        container.setPadding(pad, pad, pad, 0);

        EditText emailInput = new EditText(requireContext());
        emailInput.setHint(getString(R.string.email_hint));
        emailInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailInput.setText(viewModel.getRawEmail());
        emailInput.setLayoutParams(defaultInputLayoutParams());

        EditText passwordInput = new EditText(requireContext());
        passwordInput.setHint(getString(R.string.password_hint));
        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordInput.setText(viewModel.getRawPassword());
        passwordInput.setLayoutParams(defaultInputLayoutParams());

        container.addView(emailInput);
        container.addView(passwordInput);

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.edit_credentials)
                .setView(container)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    String newEmail = emailInput.getText().toString().trim();
                    String newPassword = passwordInput.getText().toString().trim();
                    viewModel.updateCredentials(newEmail, newPassword);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private LinearLayout.LayoutParams defaultInputLayoutParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(12);
        return params;
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

    private int dp(int value) {
        return Math.round(value * requireContext().getResources().getDisplayMetrics().density);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
