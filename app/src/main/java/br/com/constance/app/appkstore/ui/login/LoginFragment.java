package br.com.constance.app.appkstore.ui.login;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.appkstore.R;
import com.example.appkstore.databinding.FragmentLoginBinding;

import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import br.com.constance.app.appkstore.SessionManager;
//import br.com.constance.app.appkstore.backend.controller.UsuarioController;
import br.com.constance.app.appkstore.backend.dao.UsuarioDao;
import br.com.constance.app.appkstore.backend.login.LoginAsync;
import br.com.constance.app.appkstore.backend.login.LoginDataSource;
import br.com.constance.app.appkstore.backend.login.LoginRepository;
import br.com.constance.app.appkstore.backend.services.pojo.UsuarioPOJO;


public class LoginFragment extends Fragment {

    private LoginViewModel loginViewModel;
    private LoginAsync loginAsync;
    private FragmentLoginBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        LoginDataSource loginDataSource = new LoginDataSource();
        LoginRepository loginRepository = new LoginRepository(loginDataSource);
        loginViewModel = new LoginViewModel(loginRepository);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = view.findViewById(R.id.login);

        final ProgressBar loadingProgressBar = binding.loading;
        SessionManager sessionManager = SessionManager.getInstance(getContext());
        boolean isLoggedIn = sessionManager.isLoggedIn();
        NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        if (sessionManager.isLoggedIn()) {
            NavController navController = navHostFragment.getNavController();
//            navController.navigate(R.id.nav_pedidos);
        }

        loginViewModel.getLoginFormState().observe(getViewLifecycleOwner(), new Observer<LoginFormState>() {

            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(getViewLifecycleOwner(), new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
//        hostEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
//                    loginButton.performClick();
//                    hideKeyboard();
//
//                    return true;
//                }
//                return false;
//            }
//        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usernameEditText.getText().toString().isEmpty() ||
                        passwordEditText.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity().getApplicationContext(), "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
                    return; // Interrompe o processo de login se algum campo estiver vazio
                }
                loadingProgressBar.setVisibility(View.VISIBLE);
                UsuarioDao usuarioDao = new UsuarioDao(getContext());
                UsuarioPOJO usuarioPOJO = new UsuarioPOJO();
                try {
                  usuarioPOJO = usuarioDao.buscaUsuario(usernameEditText.getText().toString(), passwordEditText.getText().toString());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }


                if (usuarioPOJO != null) {

                    if (navHostFragment != null) {
                        sessionManager.setLoggedIn(true);
                        sessionManager.setUsername(usernameEditText.getText().toString());
                        sessionManager.setPassword(passwordEditText.getText().toString());
                        JSONObject responseBody = null;
//                        try {
//                            responseBody = response.getJSONObject("responseBody");
//                            JSONObject jsessionIdObj = responseBody.getJSONObject("jsessionid");
//                            String jsessionIdValue = jsessionIdObj.getString("$");
//                            sessionManager.setSessionId(jsessionIdValue);
//                        } catch (JSONException e) {
//                            throw new RuntimeException(e);
//                        }
//                        UsuarioController usuarioController = new UsuarioController(getContext());
//                        usuarioController.execute();


                        TextView usuarioLogado = getActivity().findViewById(R.id.usuarioLogado);
                        usuarioLogado.setText(sessionManager.getUsername());
                        NavController navController = navHostFragment.getNavController();
//                        navController.navigate(R.id.nav_sincroniza);

                    } else {
                        Log.e("SeuFragmentoAtual", "NavHostFragment é nulo");
                    }
                }
            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getUserName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getActivity().getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getActivity().getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        // Find the currently focused view, so we can grab the correct window token from it.
        View view = getActivity().getCurrentFocus();
        // If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(getActivity());
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}