package br.com.gigatron.futbusiness.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Objects;

import br.com.gigatron.futbusiness.R;
import br.com.gigatron.futbusiness.asynctask.VerificaUsuarioTask;
import br.com.gigatron.futbusiness.database.FutBusinessDatabase;
import br.com.gigatron.futbusiness.database.dao.UsuarioDao;

public class HomePageActivity extends AppCompatActivity {

    private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Objects.requireNonNull(getSupportActionBar()).hide();
        verificaUsuario();
        inicializaAds();
        configuraBotao();
    }

    @Override
    protected void onResume() {
        super.onResume();
        inicializaAds();
    }

//Level 1 ------------------------------------------------------------------------------------------
    private void verificaUsuario() {
        UsuarioDao usuarioDao = FutBusinessDatabase.getInstance(this).getUsuarioDao();
        new VerificaUsuarioTask(usuarioDao).execute();
    }

    private void inicializaAds() {
        MobileAds.initialize(this);
        AdRequest adRequest = new AdRequest.Builder().build();

        AdView bannerTopo = findViewById(R.id.activity_home_page_banner_topo);
        bannerTopo.loadAd(adRequest);

        AdView bannerBase = findViewById(R.id.activity_home_page_banner_base);
        bannerBase.loadAd(adRequest);
    }

    private void configuraBotao() {
        Button botaoEntrar = findViewById(R.id.activity_homepage_button_entrar);
        botaoEntrar.setOnClickListener((v) -> {
            if (isConnected(HomePageActivity.this)) {
                mostraIntersticialAd();
            } else {
                mostraAlertaErroConexao();
            }
        });
    }

//Level 2 ------------------------------------------------------------------------------------------
    private boolean isConnected(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConnection =
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        NetworkInfo mobileConnection =
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return (wifiConnection != null && wifiConnection.isConnected())
                || (mobileConnection != null && mobileConnection.isConnected());
    }

    private void mostraIntersticialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        Toast.makeText(this, "Carregando...", Toast.LENGTH_SHORT).show();
        InterstitialAd.load(this,"ca-app-pub-7406067620829259/5784281040", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        configuraFullScreenCallback();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        startActivity(new Intent(HomePageActivity.this, MeusFutsActivity.class));
                        mInterstitialAd = null;
                    }
                });
}

    private void mostraAlertaErroConexao() {
        new AlertDialog.Builder(HomePageActivity.this)
                    .setTitle("Desconectado")
                    .setMessage("Por favor, conecte-se à internet")
                    .setPositiveButton("Ok", null)
                    .show();
    }

//Level 3 ------------------------------------------------------------------------------------------
    private void configuraFullScreenCallback() {
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
            @Override
            public void onAdDismissedFullScreenContent() {
                startActivity(new Intent(HomePageActivity.this, MeusFutsActivity.class));
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                startActivity(new Intent(HomePageActivity.this, MeusFutsActivity.class));
            }

            @Override
            public void onAdShowedFullScreenContent() {
                mInterstitialAd = null;
            }
        });

        if (mInterstitialAd != null) {
            mInterstitialAd.show(HomePageActivity.this);
        }
    }
}