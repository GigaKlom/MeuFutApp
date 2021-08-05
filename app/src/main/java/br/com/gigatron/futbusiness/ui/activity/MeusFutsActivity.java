package br.com.gigatron.futbusiness.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
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
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Objects;

import br.com.gigatron.futbusiness.R;
import br.com.gigatron.futbusiness.asynctask.DefineUsuarioTask;
import br.com.gigatron.futbusiness.database.FutBusinessDatabase;
import br.com.gigatron.futbusiness.database.dao.FutDao;
import br.com.gigatron.futbusiness.database.dao.JogadorDao;
import br.com.gigatron.futbusiness.database.dao.UsuarioDao;
import br.com.gigatron.futbusiness.model.Fut;
import br.com.gigatron.futbusiness.model.JogadorFut;
import br.com.gigatron.futbusiness.model.Usuario;
import br.com.gigatron.futbusiness.ui.adapter.MeusFutsAdapter;
import br.com.gigatron.futbusiness.ui.view.MeusFutsView;
import br.com.gigatron.futbusiness.util.NetworkCheck;

import static br.com.gigatron.futbusiness.Keys.ABRE_FORMULARIO_DO_FUT_EM_MODO_EDICAO_EXTRA_FUT;
import static br.com.gigatron.futbusiness.Keys.ABRE_JOGADORES_FUT_EXTRA_FUT;
import static br.com.gigatron.futbusiness.Keys.ABRE_LISTA_FUT_PARA_ADICIONAR_COPIAS_EXTRA_FUT;
import static br.com.gigatron.futbusiness.Keys.ABRE_LISTA_FUT_PARA_ADICIONAR_COPIAS_EXTRA_LIST_JOGADOR;
import static br.com.gigatron.futbusiness.Keys.EXTRA_FUT;
import static br.com.gigatron.futbusiness.Keys.EXTRA_USUARIO;

public class MeusFutsActivity extends AppCompatActivity {

    private  String appbarTitle;
    private final Context context = this;
    private MeusFutsAdapter adapter;
    private MeusFutsView meusFutsView;
    private FutDao futDao;
    private Usuario usuario;
    private boolean modoSelecao;
    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;
    private RewardedAd mRewardedAd;
    private UsuarioDao usuarioDao;
    private CheckBox checkBoxSelecionarTodos;
//    private AdView bannerBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_futs);
        defineUsuario();
        inicializaNovosFutsDisponiveis();
        verificaIntent();
        configuraCheckBoxSelecionarTodos();
        configuraListViewFut();
        configuraFabAdd();
        networkCheck();
        setTitle(appbarTitle);
        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color=\"#6ed616\">" + appbarTitle + "</font>"));
        inicializaAds();
    }

    @Override
    protected void onResume() {
        super.onResume();
        defineUsuario();
        meusFutsView.atualiza();
        networkCheck();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        networkCheck();
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        if(!modoSelecao) {
            mostraAlertaSair();
        } else {
            super.onBackPressed();
        }
    }

    private void mostraAlertaSair() {
        new AlertDialog.Builder(context)
                .setTitle("Sair")
                .setMessage("Deseja sair para a página inicial?")
                .setNegativeButton("Não", null)
                .setPositiveButton("Sim", (dialog, which) -> finish()).show();
    }

//Level 1 ------------------------------------------------------------------------------------------
    private void defineUsuario() {
        usuarioDao = FutBusinessDatabase.getInstance(this).getUsuarioDao();
        new DefineUsuarioTask(usuarioDao, usuario -> MeusFutsActivity.this.usuario = usuario)
                .execute();
    }

    private void inicializaNovosFutsDisponiveis() {
        ImageButton assistirRewardedAd =
                findViewById(R.id.activity_meus_futs_imagebutton_assistir_rewardedAd);
        assistirRewardedAd.setOnClickListener(v -> mostraRewardedAd());
    }

    private void verificaIntent() {
        if (getIntent().hasExtra(ABRE_LISTA_FUT_PARA_ADICIONAR_COPIAS_EXTRA_LIST_JOGADOR)) {
            modoSelecao = true;
            appbarTitle = "Selecione os futs";
        } else {
            modoSelecao = false;
            appbarTitle = "Meus futs";
        }
    }

    private void configuraListViewFut() {
        inicializaDao();
        ListView listViewFuts = findViewById(R.id.activity_meus_futs_listview);
        configuraAdapter(listViewFuts);
        inicializaListaFutView();
        configuraListViewItemClick(listViewFuts);
        configuraMultiChoiceMode(listViewFuts);
        meusFutsView.atualiza();
    }

    private void configuraFabAdd() {
        FloatingActionButton add = findViewById(R.id.activity_meus_futs_fab_add);
        if (modoSelecao) {
            add.setVisibility(View.GONE);
        }
        configuraFabClickListener(add);
    }

    private void configuraCheckBoxSelecionarTodos() {
        checkBoxSelecionarTodos = findViewById(R.id.activity_meus_futs_checkbox_selecionar_todos);
        if (modoSelecao) {
            visibilityCheckBoxSelecionarTodos(View.VISIBLE);
        } else {
            visibilityCheckBoxSelecionarTodos(View.GONE);
        }
    }

    private void networkCheck() {
        new NetworkCheck().check(context, isConnected -> {});
    }

    private void inicializaAds() {
        MobileAds.initialize(this);
        adRequest = new AdRequest.Builder().build();

        AdView bannerTopo = findViewById(R.id.activity_meus_futs_banner_topo);
        bannerTopo.loadAd(adRequest);
    }

//Level 2 ------------------------------------------------------------------------------------------
    private void inicializaDao() {
        futDao = FutBusinessDatabase.getInstance(MeusFutsActivity.this).getFutDao();
    }

    private void configuraAdapter(ListView futs) {
        adapter = new MeusFutsAdapter(context, modoSelecao, checkBoxSelecionarTodos);
        futs.setAdapter(adapter);
        if (modoSelecao) { enviaFutOrigemDosJogadoresCopiados(); }
    }

    private void inicializaListaFutView() {
        TextView novosFutsDisponiveis =
                findViewById(R.id.activity_meus_futs_textview_novos_futs_disponiveis);
        meusFutsView = new MeusFutsView(
                context,
                adapter,
                futDao,
                novosFutsDisponiveis,
                usuarioDao
        );
    }

    private void configuraListViewItemClick(ListView futs) {
        futs.setOnItemClickListener((parent, view, position, id) -> {
            Fut fut = adapter.getItem(position);
            if (!modoSelecao) { abreListaJogadores(fut); }
        });
    }

    private void configuraMultiChoiceMode(ListView listViewFuts) {
        if (!modoSelecao) {
            listViewFuts.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
            configuraChoiceModeListener(listViewFuts);
        }
    }

    private void configuraFabClickListener(FloatingActionButton add) {
        add.setOnClickListener(v -> {
            abreFormularioOuOfereceRewardedAd();
        });
    }

    private void abreFormularioOuOfereceRewardedAd() {
        if (usuario.getSaldoNovoFut() > 0) {
            abreFormularioEmModoCriaFut();
        } else {
            new AlertDialog.Builder(context)
                    .setTitle("Sem novos futs disponíveis")
                    .setMessage(
"Você atingiu o número máximo de futs criados.\n\nAssista à um vídeo e ganhe mais um espaço!"
                    )
                    .setPositiveButton(
                            "Assitir agora", (dialog, which) -> mostraRewardedAd()
                    )
                    .setNegativeButton("Depois", null)
                    .show();
        }
    }

//Level 3 ------------------------------------------------------------------------------------------
    private void enviaFutOrigemDosJogadoresCopiados() {
        Fut fut = (Fut) getIntent()
                .getSerializableExtra(ABRE_LISTA_FUT_PARA_ADICIONAR_COPIAS_EXTRA_FUT);

        adapter.setFutOrigemDosJogadoresCopiados(fut);
    }

    private void configuraChoiceModeListener(ListView listViewFuts) {
        listViewFuts.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode,
                                                  int position,
                                                  long id,
                                                  boolean checked) {
                adapter.setPrimeiroObjectSelecionado(adapter.getItem(position));
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.activity_meus_futs_actionmode_menu, menu);
                escondeFabAdd(true);
                mode.setTitle("1 fut(s) selecionado(s)");
                adapter.setActionMode(true);
                adapter.setPrimeiroFutChecavel(true);
                enviaOptionMenuItemsParaOAdapter(mode, menu);
                visibilityCheckBoxSelecionarTodos(View.VISIBLE);
//                visibilityBannerBase(View.GONE);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.activity_lista_fut_actionmode_menu_editar) {
                    if (adapter.getFutsSelecionados().size() == 1) {
                        abreFormularioEmModoEditaFut(adapter.getFutsSelecionados().get(0));
                    }
                    mode.finish();
                    return true;

                } else if (itemId == R.id.activity_lista_fut_actionmode_menu_remover) {
                    meusFutsView.confirmaRemove(mode::finish);
                    return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                escondeFabAdd(false);
                visibilityCheckBoxSelecionarTodos(View.GONE);
//                visibilityBannerBase(View.VISIBLE);
                adapter.setActionMode(false);
                adapter.setPrimeiroFutChecavel(true);
            }
        });
    }

    private void abreListaJogadores(Fut fut) {
        Intent abreListaJogadores = new Intent(MeusFutsActivity.this,
                PaginaFutActivity.class);

        abreListaJogadores.putExtra(ABRE_JOGADORES_FUT_EXTRA_FUT, fut);
        startActivity(abreListaJogadores);
    }

    private void abreFormularioEmModoCriaFut() {
        Intent abreFormularioEmModoCriaFut =
                new Intent(MeusFutsActivity.this, FormularioFutActivity.class);
        abreFormularioEmModoCriaFut.putExtra(EXTRA_USUARIO, usuario);
        startActivity(abreFormularioEmModoCriaFut);
    }

//Level 4 ------------------------------------------------------------------------------------------
    private void enviaOptionMenuItemsParaOAdapter(ActionMode mode, Menu menu) {
        adapter.setActionModeReference(mode);
        adapter.setOpcaoEditar(menu.findItem(R.id.activity_lista_fut_actionmode_menu_editar));
        adapter.setOpcaoRemover(menu.findItem(R.id.activity_lista_fut_actionmode_menu_remover));
    }

    private void visibilityCheckBoxSelecionarTodos(int visibility) {
        checkBoxSelecionarTodos.setVisibility(visibility);
        checkBoxSelecionarTodos.setChecked(false);
    }

//    private void visibilityBannerBase(int visibility) {
//        bannerBase.setVisibility(visibility);
//    }

    private void escondeFabAdd(boolean esconde) {
        FloatingActionButton fabAdd = findViewById(R.id.activity_meus_futs_fab_add);
        if (esconde) {
            fabAdd.setVisibility(View.GONE);
        } else {
            fabAdd.setVisibility(View.VISIBLE);
        }
    }

    private void abreFormularioEmModoEditaFut(Fut fut) {
        Intent abreFormularioEmModoEditaFut = new Intent(MeusFutsActivity.this,
                FormularioFutActivity.class);
        abreFormularioEmModoEditaFut.putExtra(ABRE_FORMULARIO_DO_FUT_EM_MODO_EDICAO_EXTRA_FUT, fut);
        abreFormularioEmModoEditaFut.putExtra(EXTRA_FUT, usuario);
        startActivity(abreFormularioEmModoEditaFut);
    }

    private void mostraRewardedAd() {
        RewardedAd.load(
                MeusFutsActivity.this,
                "ca-app-pub-7406067620829259/1283048815",
                adRequest,
                new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        aumentaSaldo();
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdShowedFullScreenContent() {
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                mRewardedAd = null;
                            }
                        });
                        aumentaSaldo();
                    }
                });
    }

    private void aumentaSaldo() {
        if (mRewardedAd != null) {
            mRewardedAd.show(MeusFutsActivity.this, rewardItem -> {
                meusFutsView.aumentaSaldoNovoFut(usuarioDao);
            });
        } else {
            meusFutsView.aumentaSaldoNovoFut(usuarioDao);
            meusFutsView.atualiza();
        }
    }

//OptionsMenu --------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (modoSelecao) {
            getMenuInflater().inflate(R.menu.done_menu, menu);
            menu.findItem(R.id.done_menu_add).setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.done_menu_done) {
            List<JogadorFut> jogadoresAInserir = getIntent().getParcelableArrayListExtra
                    (ABRE_LISTA_FUT_PARA_ADICIONAR_COPIAS_EXTRA_LIST_JOGADOR);
            JogadorDao jogadorDao = FutBusinessDatabase.getInstance(context).getJogadorDao();
            List<Fut> futsSelecionados = adapter.getFutsSelecionados();
            mostraIntersticialAd(jogadoresAInserir, jogadorDao, futsSelecionados);
        } else if (itemId == R.id.done_menu_add) {
            abreFormularioOuOfereceRewardedAd();
        }
        return super.onOptionsItemSelected(item);
    }

//Level 2 ------------------------------------------------------------------------------------------
    private void mostraIntersticialAd(
            List<JogadorFut> jogadoresAInserir,
            JogadorDao jogadorDao,
            List<Fut> futsSelecionados
    ) {
        InterstitialAd.load(this, "ca-app-pub-7406067620829259/3704912619", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        configuraFullScreenCallback(
                                jogadoresAInserir,
                                jogadorDao,
                                futsSelecionados
                        );
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        insereJogadoresCopiados(jogadoresAInserir, jogadorDao, futsSelecionados);
                        mInterstitialAd = null;
                    }
                });
    }

//Level 3 ------------------------------------------------------------------------------------------
    private void configuraFullScreenCallback(
            List<JogadorFut> jogadoresAInserir,
            JogadorDao jogadorDao,
            List<Fut> futsSelecionados
    ) {
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                insereJogadoresCopiados(jogadoresAInserir, jogadorDao, futsSelecionados);
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                insereJogadoresCopiados(jogadoresAInserir, jogadorDao, futsSelecionados);
            }

            @Override
            public void onAdShowedFullScreenContent() {
                mInterstitialAd = null;
            }
        });

        if (mInterstitialAd != null) {
            mInterstitialAd.show(MeusFutsActivity.this);
        }
    }

    private void insereJogadoresCopiados(List<JogadorFut> jogadoresAInserir, JogadorDao jogadorDao, List<Fut> futsSelecionados) {
        meusFutsView.insereJogadoresCopiados(
                jogadoresAInserir,
                jogadorDao,
                futsSelecionados,
                MeusFutsActivity.this::finish
        );
    }
}