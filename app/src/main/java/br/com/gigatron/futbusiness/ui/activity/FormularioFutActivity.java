package br.com.gigatron.futbusiness.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

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
import br.com.gigatron.futbusiness.model.Fut;
import br.com.gigatron.futbusiness.model.Usuario;
import br.com.gigatron.futbusiness.ui.view.FormularioFutView;
import br.com.gigatron.futbusiness.util.NetworkCheck;

import static br.com.gigatron.futbusiness.Keys.ABRE_FORMULARIO_DO_FUT_EM_MODO_EDICAO_EXTRA_FUT;
import static br.com.gigatron.futbusiness.Keys.EXTRA_USUARIO;

public class FormularioFutActivity extends AppCompatActivity {

    private String appbarTitle;
    private final Context context = FormularioFutActivity.this;
    private FormularioFutView formularioFutView;
    private Fut fut;
    private boolean modoEditaFut;
    private EditText local;
    private ImageButton clear;
    private EditText valorAvulso;
    private EditText valorMensal;
    private LinearLayout valorMensalLinear;
    private EditText aluguelDaQuadra;
    private CheckBox mensal;
    private LinearLayout diaDaSemanaLayout;
    private RadioGroup diaDaSemanaRadioGroup;
    private TextView horarioTextView;
    private ImageButton horarioButton;
    private RadioButton domingo;
    private RadioButton segunda;
    private RadioButton terca;
    private RadioButton quarta;
    private RadioButton quinta;
    private RadioButton sexta;
    private RadioButton sabado;
    private LinearLayout linearLayoutMensal;
    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_fut);
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
        local = findViewById(R.id.activity_formulario_fut_edittext_local);
        clear = findViewById(R.id.activity_formulario_fut_imagebutton_clear);
        valorAvulso = findViewById(R.id.activity_formulario_fut_edittext_avulso);
        valorMensal = findViewById(R.id.activity_formulario_fut_edittext_mensal);
        valorMensalLinear = findViewById(R.id.activity_formulario_fut_linearlayout_mensal);
        aluguelDaQuadra = findViewById(R.id.activity_formulario_fut_edittext_aluguel_da_quadra);
        linearLayoutMensal = findViewById(R.id.activity_formulario_fut_linearlayout_fut_mensal);
        mensal = findViewById(R.id.activity_formulario_fut_checkbox_fut_mensal);
        diaDaSemanaLayout = findViewById(R.id.activity_formulario_fut_linear_layout_dia_da_semana);
        diaDaSemanaRadioGroup = findViewById(R.id.activity_formulario_fut_radio_group_dia_da_semana);
        horarioTextView = findViewById(R.id.activity_formulario_fut_textview_horario);
        horarioButton = findViewById(R.id.activity_formulario_fut_imagebutton_horario);
        domingo = findViewById(R.id.activity_formulario_fut_radio_button_domingo);
        segunda = findViewById(R.id.activity_formulario_fut_radio_button_segunda);
        terca = findViewById(R.id.activity_formulario_fut_radio_button_terca);
        quarta = findViewById(R.id.activity_formulario_fut_radio_button_quarta);
        quinta = findViewById(R.id.activity_formulario_fut_radio_button_quinta);
        sexta = findViewById(R.id.activity_formulario_fut_radio_button_sexta);
        sabado = findViewById(R.id.activity_formulario_fut_radio_button_sabado);
        configuraClickClear();
        configuraBotaoClearVisibility();
        configuraMensalCheckedChange();
    }

    private void verificaIntent() {
        Intent intent = getIntent();
        usuario = intent.getParcelableExtra(EXTRA_USUARIO);
        if (intent.hasExtra(ABRE_FORMULARIO_DO_FUT_EM_MODO_EDICAO_EXTRA_FUT)) {
            ativaModoEditaFut(intent);
        } else {
            ativaModoCriaFut();
        }
        formularioFutView.configuraTimePicker();
    }

    private void networkCheck() {
        new NetworkCheck().check(context, isConnected -> {});
    }

    private void inicializaAds() {
        MobileAds.initialize(this);
        adRequest = new AdRequest.Builder().build();

        AdView bannerTopo = findViewById(R.id.activity_formulario_fut_banner_topo);
        bannerTopo.loadAd(adRequest);

        AdView bannerBase = findViewById(R.id.activity_formulario_fut_banner_base);
        bannerBase.loadAd(adRequest);
    }

//Level 2 ------------------------------------------------------------------------------------------
    private void configuraClickClear() {
        clear.setOnClickListener(v -> local.setText(""));
    }

    private void configuraBotaoClearVisibility() {
        local.setOnFocusChangeListener((v, hasFocus) -> {
            if (!local.getText().toString().isEmpty() && hasFocus) {
                clear.setVisibility(View.VISIBLE);
            } else {
                clear.setVisibility(View.GONE);
            }
        });

        local.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                clear.setVisibility(View.GONE);
                if (!local.getText().toString().isEmpty()) {
                    clear.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clear.setVisibility(View.GONE);
                if (!local.getText().toString().isEmpty()) {
                    clear.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                clear.setVisibility(View.GONE);
                if (!local.getText().toString().isEmpty()) {
                    clear.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void configuraMensalCheckedChange() {
        mensal.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                visibilityCamposParaFutMensal(View.VISIBLE);
            } else {
                visibilityCamposParaFutMensal(View.GONE);
            }
        });
    }

    private void ativaModoCriaFut() {
        modoEditaFut = false;
        fut = new Fut();
        inicializaFormFutView();
        appbarTitle = "Novo Fut";
    }

    private void ativaModoEditaFut(Intent intent) {
        modoEditaFut = true;
        fut = (Fut) intent.getSerializableExtra(ABRE_FORMULARIO_DO_FUT_EM_MODO_EDICAO_EXTRA_FUT);
        inicializaFormFutView();
        linearLayoutMensal.setVisibility(View.GONE);
        formularioFutView.preencheCamposComDadosDoFut();
        appbarTitle = "Editar Fut em " + fut.getLocal();
    }

//Level 3 ------------------------------------------------------------------------------------------
    private void visibilityCamposParaFutMensal(int visibility) {
        diaDaSemanaLayout.setVisibility(visibility);
        valorMensalLinear.setVisibility(visibility);
    }

    private void inicializaFormFutView() {
        formularioFutView = new FormularioFutView(
                context,
                fut,
                local,
                valorAvulso,
                valorMensal,
                aluguelDaQuadra,
                mensal,
                diaDaSemanaLayout,
                diaDaSemanaRadioGroup,
                domingo, segunda, terca, quarta, quinta, sexta, sabado,
                horarioTextView,
                horarioButton
        );
    }

//Level 4 ------------------------------------------------------------------------------------------
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

    private void aoClicarEmSalvar() {
        if (formularioFutView.camposEstaoPreenchidos()) {
            formularioFutView.capitalizarPrimeiraLetraDeCadaPalavraNoCampoLocal();
            formularioFutView.finalizaFormulario(
                    modoEditaFut,
                    new FormularioFutView.FinalizaFormularioListener() {
                        @Override
                        public void aposSalvar() {
                            mostraIntersticialAd();
                        }
                    });
        }
    }

//Level 5 ------------------------------------------------------------------------------------------
    private void mostraIntersticialAd() {
        InterstitialAd.load(this, "ca-app-pub-7406067620829259/5564789191", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        configuraFullScreenCallback();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        diminuiSaldoSeModoEdita();
                        finish();
                        mInterstitialAd = null;
                    }
                });
    }

//Level 6 ------------------------------------------------------------------------------------------
    private void configuraFullScreenCallback() {
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
            @Override
            public void onAdDismissedFullScreenContent() {
                diminuiSaldoSeModoEdita();
                finish();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                diminuiSaldoSeModoEdita();
                finish();
            }

            @Override
            public void onAdShowedFullScreenContent() {
                mInterstitialAd = null;
            }
        });

        if (mInterstitialAd != null) {
            mInterstitialAd.show(FormularioFutActivity.this);
        }
    }

    private void diminuiSaldoSeModoEdita() {
        if (!modoEditaFut) {
            formularioFutView.diminuiSaldoNovoFut(usuario);
        }
    }
}