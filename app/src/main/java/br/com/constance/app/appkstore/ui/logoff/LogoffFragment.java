package br.com.constance.app.appkstore.ui.logoff;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.appkstore.R;
import com.example.appkstore.databinding.FragmentLogoffBinding;
import com.google.android.material.navigation.NavigationView;

import br.com.constance.app.appkstore.SessionManager;

public class LogoffFragment extends Fragment {

    private FragmentLogoffBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflar o layout do Fragment
        View view = inflater.inflate(R.layout.fragment_logoff, container, false);

        // Obter a referência à Activity principal que hospeda o Fragment
        Activity activity = getActivity();
        if (activity != null) {
            // Obter o NavigationView da Activity
            NavigationView navigationView = activity.findViewById(R.id.nav_view);
            if (navigationView != null) {
                // Obter a View do header do NavigationView
                View headerView = navigationView.getHeaderView(0);
                if (headerView != null) {
                    // Obter a referência ao TextView usuarioLogado
                    TextView usuarioLogado = headerView.findViewById(R.id.usuarioLogado);
                    SessionManager sessionManager = SessionManager.getInstance(getContext());
                    boolean isLoggedIn = sessionManager.isLoggedIn();
                    if (isLoggedIn){
                        // Definir o texto no TextView
                        usuarioLogado.setText(R.string.nav_usuario_logado);
                        sessionManager.setLoggedIn(false);
                        sessionManager.setSessionId(null);
                        sessionManager.setUsername(null);
                        sessionManager.setPassword(null);
                        sessionManager.setCodusu(null);
                        Toast.makeText(activity.getApplicationContext(), "Deslogado", Toast.LENGTH_LONG).show();
                        NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
                        NavController navController = navHostFragment.getNavController();
                        navController.navigate(R.id.nav_login);
                    }else{
                        NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
                        NavController navController = navHostFragment.getNavController();
                        navController.navigate(R.id.nav_login);


                    }
                }
            }
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}