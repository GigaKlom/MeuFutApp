package br.com.gigatron.futbusiness.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

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
import br.com.gigatron.futbusiness.model.Fut;
import br.com.gigatron.futbusiness.model.JogadorFut;
import br.com.gigatron.futbusiness.model.Usuario;
import br.com.gigatron.futbusiness.ui.view.FormularioJogadorView;
import br.com.gigatron.futbusiness.util.NetworkCheck;

import static br.com.gigatron.futbusiness.Keys.ABRE_FORMULARIO_JOGADOR_MODO_CRIA_EXTRA_FUT;
import static br.com.gigatron.futbusiness.Keys.ABRE_FORMULARIO_JOGADOR_MODO_EDITA_EXTRA_JOGADOR;
import static br.com.gigatron.futbusiness.Keys.EXTRA_FUT;
import static br.com.gigatron.futbusiness.Keys.EXTRA_USUARIO;

public class FormularioJogadorActivity extends AppCompatActivity {

    private String appbarTitle;
    private final Context context = FormularioJogadorActivity.this;
    private FormularioJogadorView formularioJogadorView;
    private Fut fut;
    private JogadorFut jogadorFut;
    private boolean modoEditaJogador;

    private EditText nome;
    private ImageButton clear;
    private EditText valorAvulso;
    private CheckBox padraoAvulso;
    private EditText valorMensal;
    private CheckBox padraoMensal;
    private CheckBox jogadorMensalista;
    private Group informacoesJogador;
    private TextView presenteAvulso;
    private TextView tituloPresenteMensal;
    private TextView presenteMensal;
    private TextView calotesDados;
    private TextView vezesQueFurou;
    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_jogador);
        inicializaCampos();
        verificaIntent();
        networkCheck();
        setTitle(appbarTitle);
        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color=\"#6ed616\">" + appbarTitle + "</font>"));
        inicializaAds();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        networkCheck();
        return super.onTouchEvent(event);
    }

//Level 1 ------------------------------------------------------------------------------------------
    private void inicializaCampos() {
        nome =
                findViewById(R.id.activity_formulario_jogador_edittext_nome);

        clear =
                findViewById(R.id.activity_formulario_jogador_imagebutton_clear);

        valorAvulso =
                findViewById(R.id.activity_formulario_jogador_edittext_avulso);

        padraoAvulso =
                findViewById(R.id.activity_formulario_jogador_checkbox_padrao_avulso);

        valorMensal =
                findViewById(R.id.activity_formulario_jogador_edittext_mensal);

        padraoMensal =
                findViewById(R.id.activity_formulario_jogador_checkbox_padrao_mensal);

        jogadorMensalista =
                findViewById(R.id.activity_formulario_jogador_checkbox_mensalista);

        informacoesJogador =
                findViewById(R.id.activity_formulario_jogador_group_informacoes_jogador);

        presenteAvulso =
                findViewById(R.id.activity_formulario_jogador_textview_presente_avulso);

        tituloPresenteMensal =
                findViewById(R.id.activity_formulario_jogador_textview_titulo_presente_mensal);

        presenteMensal =
                findViewById(R.id.activity_formulario_jogador_textview_presente_mensal);

        calotesDados =
                findViewById(R.id.activity_formulario_jogador_textview_calotes_dados);

        vezesQueFurou =
                findViewById(R.id.activity_formulario_jogador_textview_furos);

        configuraClickClear();
        configuraBotaoClearVisibility();
        configuraOnCheckedChange();
    }

    private void verificaIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(ABRE_FORMULARIO_JOGADOR_MODO_CRIA_EXTRA_FUT)) {
            fut = (Fut) intent.getSerializableExtra(ABRE_FORMULARIO_JOGADOR_MODO_CRIA_EXTRA_FUT);
            ativaModoCriaJogador();
            Log.i("TAG", "verificaIntent: CRIA");
        } else if (intent.hasExtra(EXTRA_FUT)) {
            ativaModoEditarJogador(intent);
            Log.i("TAG", "verificaIntent: EDITA");
        }
        if (intent.hasExtra(EXTRA_USUARIO)) {
            usuario = intent.getParcelableExtra(EXTRA_USUARIO);
        }
    }

    private void networkCheck() {
        new NetworkCheck().check(context, isConnected -> {});
    }

    private void inicializaAds() {
        MobileAds.initialize(this);
        adRequest = new AdRequest.Builder().build();

        AdView bannerTopo = findViewById(R.id.activity_formulario_jogador_banner_topo);
        bannerTopo.loadAd(adRequest);

        AdView bannerBase = findViewById(R.id.activity_formulario_jogador_banner_base);
        bannerBase.loadAd(adRequest);
    }

//Level 2 ------------------------------------------------------------------------------------------
    private void configuraClickClear() {
    clear.setOnClickListener(v -> nome.setText(""));
}

    private void configuraBotaoClearVisibility() {
        nome.setOnFocusChangeListener((v, hasFocus) -> {
            if (!nome.getText().toString().isEmpty() && hasFocus) {
                clear.setVisibility(View.VISIBLE);
            } else {
                clear.setVisibility(View.GONE);
            }
        });

        nome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                clear.setVisibility(View.GONE);
                if (!nome.getText().toString().isEmpty()) {
                    clear.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clear.setVisibility(View.GONE);
                if (!nome.getText().toString().isEmpty()) {
                    clear.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                clear.setVisibility(View.GONE);
                if (!nome.getText().toString().isEmpty()) {
                    clear.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void configuraOnCheckedChange() {
        padraoAvulso.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                valorAvulso.setVisibility(View.GONE);
            } else {
                valorAvulso.setVisibility(View.VISIBLE);
            }
        });
        padraoMensal.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                valorMensal.setVisibility(View.GONE);
            } else {
                valorMensal.setVisibility(View.VISIBLE);
            }
        });
    }

    private void ativaModoCriaJogador() {
        modoEditaJogador = false;
        jogadorFut = new JogadorFut();
        inicializaFormJogadorView();
        formularioJogadorView.setCheckedEmValoresPadrao();
        formularioJogadorView.escondeInformacoesJogador();
        appbarTitle = "Novo Jogador";
    }

    private void ativaModoEditarJogador(Intent intent) {
        modoEditaJogador = true;
        fut = (Fut) intent.getSerializableExtra(EXTRA_FUT);
        jogadorFut = intent.getParcelableExtra(ABRE_FORMULARIO_JOGADOR_MODO_EDITA_EXTRA_JOGADOR);
        inicializaFormJogadorView();
        formularioJogadorView.preencheCamposComDadosDoJogador();
        appbarTitle = "Editar " + jogadorFut.getNome();
        jogadorMensalista.setVisibility(View.GONE);
    }

//Level 3 ------------------------------------------------------------------------------------------
    private void inicializaFormJogadorView() {
        formularioJogadorView = new FormularioJogadorView(
                context,
                nome,
                valorAvulso,
                valorMensal,
                jogadorMensalista,
                padraoAvulso,
                padraoMensal,
                informacoesJogador,
                presenteAvulso,
                presenteMensal,
                tituloPresenteMensal,
                calotesDados,
                vezesQueFurou,
                fut,
                jogadorFut
        );
        if (!fut.isMensal()) {
            TextView camposParaMensalistas =
                    findViewById(R.id.activity_formulario_jogador_textview_valor_mensalistas);

            formularioJogadorView.escondeCamposParaMensalistas(camposParaMensalistas);
        }
    }

//Level 5 ------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.done_menu_done) {
            aoClicarEmSalvar();
        }
        return super.onOptionsItemSelected(item);
    }

//Level 6 ------------------------------------------------------------------------------------------
    private void aoClicarEmSalvar() {
        if (formularioJogadorView.camposEstaoPreenchidos()) {
            formularioJogadorView.capitalizaPrimeiraLetraDoNome();
            formularioJogadorView.verificaSeJogadorJaExiste(existe -> {
                if (!existe) {
                    mostraAdOuFinaliza();
                }
            });
        }
    }

//Level 7 ------------------------------------------------------------------------------------------
    private void mostraAdOuFinaliza() {
        if (modoEditaJogador) {
            mostraIntersticialAd();
        } else {
            formularioJogadorView.diminuiSaldoNovoJogador(usuario);
            finalizaFormulario();
        }
    }

//Level 8 ------------------------------------------------------------------------------------------
    private void mostraIntersticialAd() {
        InterstitialAd.load(this, "ca-app-pub-7406067620829259/8367331915", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        configuraFullScreenCallback();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        finalizaFormulario();
                        mInterstitialAd = null;
                    }
                });
    }

//Level 9 ------------------------------------------------------------------------------------------
    private void configuraFullScreenCallback() {
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
            @Override
            public void onAdDismissedFullScreenContent() {
                finalizaFormulario();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                finalizaFormulario();
            }

            @Override
            public void onAdShowedFullScreenContent() {
                mInterstitialAd = null;
            }
        });

        if (mInterstitialAd != null) {
            mInterstitialAd.show(FormularioJogadorActivity.this);
        }
    }

//Level 10 -----------------------------------------------------------------------------------------
    private void finalizaFormulario() {
        formularioJogadorView.preencheDadosDoJogadorComValoresNosCampos(modoEditaJogador, this::finish);
    }
}