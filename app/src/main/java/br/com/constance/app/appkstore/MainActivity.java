package br.com.constance.app.appkstore;

import static android.app.PendingIntent.getActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.example.appkstore.R;
import com.google.android.material.navigation.NavigationView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import br.com.constance.app.appkstore.dataBase.DataBaseHelper;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appkstore.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private SessionManager sessionManager;
    private boolean usuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        copyAssetFileToInternalStorage("USUARIOS.sql");
        copyAssetFileToInternalStorage("TASRTIPOSNEGOCIACAO.sql");
        copyAssetFileToInternalStorage("TASRPEDIDOCABECALHO.sql");
        copyAssetFileToInternalStorage("TASRPEDIDOITENS.sql");
        copyAssetFileToInternalStorage("TASRPARCEIROS.sql");
        copyAssetFileToInternalStorage("TASRPRODUTOS.sql");
        copyAssetFileToInternalStorage("TASRPRODUTOBARRA.sql");
        copyAssetFileToInternalStorage("TASRIMAGEMPRODUTOS.sql");

        mAppBarConfiguration = new AppBarConfiguration.Builder(                R.id.nav_login)
//        R.id.nav_login,R.id.nav_pedidos ,R.id.nav_sincroniza,R.id.nav_cliente,R.id.nav_logoff)
                .setOpenableLayout(drawer)
                .build();


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        SQLiteDatabase sb= dataBaseHelper.getWritableDatabase();
        dataBaseHelper.onCreate(sb);

//        UsuarioPOJO usuarioPOJO = new UsuarioPOJO();
//        usuarioPOJO.setId(1);
//        usuarioPOJO.setNomeUsario("kstore");
//        usuarioPOJO.setSenha("123456");
//        UsuarioDao usuarioDao = new UsuarioDao(getBaseContext());
//        try {
//            usuarioDao.salvarUsuario(usuarioPOJO);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }



        navigationView = findViewById(R.id.nav_view);

        // Obter referência ao cabeçalho do NavigationView
        View headerView = navigationView.getHeaderView(0);

        // Obter referência ao TextView no cabeçalho
        TextView textViewHeader = headerView.findViewById(R.id.usuarioLogado);
        textViewHeader.setText(sessionManager.getUsername());



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    private void copyAssetFileToInternalStorage(String fileName) {
        File file = new File(getFilesDir(), fileName);
        if (!file.exists()) {
            try (InputStream is = getAssets().open(fileName);
                 FileOutputStream fos = new FileOutputStream(file)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}