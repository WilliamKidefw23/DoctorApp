package com.doctor.app.doctorapp.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.doctor.app.doctorapp.R;
import com.doctor.app.doctorapp.Utilitario.Common;
import com.doctor.app.doctorapp.Utilitario.Constantes;
import com.doctor.app.doctorapp.Utilitario.Preferences;
import com.doctor.app.doctorapp.fragment.MainFragment;
import com.doctor.app.doctorapp.fragment.MapFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String key_Uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        key_Uid = Preferences.GetPreferencesUid(this);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    key_Uid = user.getUid();
                } else {
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    finish();
                }
            }
        };

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.contenedor_principal, MainFragment.createInstance(key_Uid), Common.TAG_MAIN_FRAGMENT)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment fragmentgenerico =null;
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (id == R.id.nav_perfil) {
            fragmentgenerico = MainFragment.createInstance(key_Uid);
        } else if (id == R.id.nav_mapa) {
            fragmentgenerico = new MapFragment();
        } else if (id == R.id.nav_sign_out) {
            mAuth.signOut();
        }

        if(fragmentgenerico!=null){
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.contenedor_principal,fragmentgenerico)
                    .commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        Log.i(TAG,"onStart");
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onResume() {
        Log.i(TAG,"onResume");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.i(TAG,"onStop");
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MapFragment fragment = (MapFragment) getSupportFragmentManager().findFragmentByTag(Common.TAG_MAP_FRAGMENT);
        switch (requestCode){
            case Constantes.MY_PERMISION_REQUEST_CODE:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Gracias, aceptaste los permisos requeridos para el correcto funcionamiento de esta aplicación.", Toast.LENGTH_SHORT).show();
                    fragment.setUpLocation();
                }else{
                    Toast.makeText(this, "No se aceptó permisos", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
