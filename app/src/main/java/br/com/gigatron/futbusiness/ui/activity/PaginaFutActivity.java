package br.com.gigatron.futbusiness.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;

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

import java.io.Serializable;
import java.util.Objects;

import br.com.gigatron.futbusiness.R;
import br.com.gigatron.futbusiness.asynctask.DefineUsuarioTask;
import br.com.gigatron.futbusiness.asynctask.evento.ProcuraFutCorrespondenteAPartirDeEventoTask;
import br.com.gigatron.futbusiness.database.FutBusinessDatabase;
import br.com.gigatron.futbusiness.database.dao.FutDao;
import br.com.gigatron.futbusiness.database.dao.JogadorDao;
import br.com.gigatron.futbusiness.database.dao.UsuarioDao;
import br.com.gigatron.futbusiness.model.Evento;
import br.com.gigatron.futbusiness.model.Fut;
import br.com.gigatron.futbusiness.model.JogadorFut;
import br.com.gigatron.futbusiness.model.Usuario;
import br.com.gigatron.futbusiness.ui.adapter.PaginaFutAdapter;
import br.com.gigatron.futbusiness.ui.view.PaginaFutView;
import br.com.gigatron.futbusiness.util.NetworkCheck;

import static br.com.gigatron.futbusiness.Keys.ABRE_EVENTOS_EXTRA_FUT;
import static br.com.gigatron.futbusiness.Keys.ABRE_EVENTOS_EXTRA_JOGADOR;
import static br.com.gigatron.futbusiness.Keys.ABRE_FORMULARIO_DO_FUT_EM_MODO_EDICAO_EXTRA_FUT;
import static br.com.gigatron.futbusiness.Keys.ABRE_FORMULARIO_JOGADOR_MODO_CRIA_EXTRA_FUT;
import static br.com.gigatron.futbusiness.Keys.ABRE_FORMULARIO_JOGADOR_MODO_EDITA_EXTRA_JOGADOR;
import static br.com.gigatron.futbusiness.Keys.ABRE_JOGADORES_FUT_EXTRA_FUT;
import static br.com.gigatron.futbusiness.Keys.ABRE_LISTA_FUT_PARA_ADICIONAR_COPIAS_EXTRA_FUT;
import static br.com.gigatron.futbusiness.Keys.ABRE_LISTA_FUT_PARA_ADICIONAR_COPIAS_EXTRA_LIST_JOGADOR;
import static br.com.gigatron.futbusiness.Keys.ABRE_LISTA_JOGADORES_PARA_SEREM_ADICIONADOS_NO_EVENTO_EXTRA_EVENTO;
import static br.com.gigatron.futbusiness.Keys.EXTRA_FUT;
import static br.com.gigatron.futbusiness.Keys.EXTRA_USUARIO;

public class PaginaFutActivity extends AppCompatActivity {

    private String appbarTitle;
    private final Context context = PaginaFutActivity.this;
    private PaginaFutAdapter adapter;
    private PaginaFutView paginaFutView;
    private Intent intent;
    private Fut fut;
    private Evento evento;
    private boolean modoSelecao;
    private TextView local;
    private TextView tipo;
    private TextView diaSemana;
    private TextView numeroJogadores;
    private TextView numeroMensalistas;
    private TextView valorQuadra;
    private TextView ganhoEsperadoMensalistas;
    private TextView ganhoEsperadoAvulsos;
    private TextView lucroPrejuizo;
    private TextView padraoAvulso;
    private TextView padraoMensalista;
    private TextView textoPadraoMensalista;
    private TextView textoMensalistas;
    private Group groupGanhoMensalistas;
    private Group groupGanhoAvulsos;
    private Group groupLucroPrejuizo;
    private SwitchCompat switchMostrarDados;
    private Group groupPadraoAvulso;
    private Group groupPadraoMensalista;
    private Group groupNumeroJogadores;
    private Group groupNumeroMensalistas;
    private Group groupPrecoQuadra;
    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;
    private RewardedAd mRewardedAd;
//    private AdView bannerBase;
    private UsuarioDao usuarioDao;
    private Usuario usuario;
    private CheckBox checkBoxSelecionarTodos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina_fut);
        defineUsuario();
        verificaIntent();
        inicializaCampos();
        configuraCheckBoxSelecionarTodos();
        determinaFutCorrespondente(this::configuraListViewJogadoresFut);
        configuraFabCriarJogador();
        configuraImageButtonAssistirRewardAd();
        networkCheck();
        setTitle(appbarTitle);
        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color=\"#6ed616\">" + appbarTitle + "</font>"));
        inicializaAds();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (fut != null) {
            paginaFutView.atualiza();
            inicializaCampos();
        }
        defineUsuario();
        networkCheck();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        networkCheck();
        return super.onTouchEvent(event);
    }

//Level 1 ------------------------------------------------------------------------------------------
    private void defineUsuario() {
        usuarioDao = FutBusinessDatabase.getInstance(this).getUsuarioDao();
        new DefineUsuarioTask(usuarioDao, usuario -> PaginaFutActivity.this.usuario = usuario)
                .execute();
    }

    private void verificaIntent() {
        intent = getIntent();
        modoSelecao =
                intent.hasExtra(ABRE_LISTA_JOGADORES_PARA_SEREM_ADICIONADOS_NO_EVENTO_EXTRA_EVENTO);
    }

    private void inicializaCampos() {
        local =
                findViewById(R.id.activity_pagina_fut_textview_local);
        tipo =
                findViewById(R.id.activity_pagina_fut_tipo);
        diaSemana =
                findViewById(R.id.activity_pagina_fut_dia_semana);
        numeroJogadores =
                findViewById(R.id.activity_pagina_fut_numero_jogadores);
        numeroMensalistas =
                findViewById(R.id.activity_pagina_fut_numero_mensalistas);
        valorQuadra =
                findViewById(R.id.activity_pagina_fut_preco_da_quadra);
        ganhoEsperadoMensalistas =
                findViewById(R.id.activity_pagina_fut_ganho_esperado_mensalistas);
        ganhoEsperadoAvulsos =
                findViewById(R.id.activity_pagina_fut_ganho_esperado_avulsos);
        lucroPrejuizo =
                findViewById(R.id.activity_pagina_fut_lucro_prejuizo);
        padraoAvulso =
                findViewById(R.id.activity_pagina_fut_padrao_avulso);
        padraoMensalista =
                findViewById(R.id.activity_pagina_fut_padrao_mensalista);
        textoPadraoMensalista =
                findViewById(R.id.activity_pagina_fut_texto_padrao_mensalista);
        textoMensalistas =
                findViewById(R.id.activity_pagina_fut_texto_mensalistas);
        groupGanhoMensalistas =
                findViewById(R.id.activity_pagina_fut_group_ganho_mensalistas);
        groupGanhoAvulsos =
                findViewById(R.id.activity_pagina_fut_group_ganho_avulsos);
        groupLucroPrejuizo =
                findViewById(R.id.activity_pagina_fut_group_lucro_prejuizo);
        switchMostrarDados =
                findViewById(R.id.activity_pagina_fut_switch);
        groupPadraoAvulso =
                findViewById(R.id.activity_pagina_fut_group_padrao_avulso);
        groupPadraoMensalista =
                findViewById(R.id.activity_pagina_fut_group_padrao_mensalista);
        groupNumeroJogadores =
                findViewById(R.id.activity_pagina_fut_group_numero_jogadores);
        groupNumeroMensalistas =
                findViewById(R.id.activity_pagina_fut_group_numero_mensalistas);
        groupPrecoQuadra =
                findViewById(R.id.activity_pagina_fut_group_preco_quadra);

        if (modoSelecao) {
            escondeBarraDeInformacoesDoFut();
        }
    }

    private void configuraCheckBoxSelecionarTodos() {
        checkBoxSelecionarTodos = findViewById(R.id.activity_pagina_fut_checkbox_selecionar_todos);
        if (modoSelecao) {
            visibilityCheckBoxSelecionarTodos(View.VISIBLE);
        } else {
            visibilityCheckBoxSelecionarTodos(View.GONE);
        }
    }

    private void determinaFutCorrespondente(DeterminaFutCorrespondenteListener listener) {
        if (modoSelecao) {
            determinaFutPeloEvento(listener);
        } else {
            determinaFutPelaIntent(listener);
        }
    }

    private void configuraListViewJogadoresFut() {
        ListView listViewJogadores = findViewById(R.id.activity_pagina_fut_listview);
        criaAdapter(listViewJogadores);
        inicializaListaJogadorFutView();
        configuraListViewItemClick(listViewJogadores);
        configuraChoiceMode(listViewJogadores);
        paginaFutView.atualiza();
    }

    private void configuraFabCriarJogador() {
        FloatingActionButton criarJogador = findViewById(R.id.activity_pagina_fut_fab_add);
        if (modoSelecao) {
            criarJogador.setVisibility(View.GONE);
        } else {
            aoClicarNoFabAdd(criarJogador);
        }
    }

    private void configuraImageButtonAssistirRewardAd() {
        ImageView assistirRewardedAd =
                findViewById(R.id.activity_pagina_fut_imagebutton_assistir_rewardedAd);
        assistirRewardedAd.setOnClickListener(v -> mostraRewardedAd());
    }

    private void networkCheck() {
        new NetworkCheck().check(context, isConnected -> {});
    }

    private void inicializaAds() {
        MobileAds.initialize(this);
        adRequest = new AdRequest.Builder().build();

        AdView bannerTopo = findViewById(R.id.activity_pagina_fut_banner_topo);
        bannerTopo.loadAd(adRequest);

//        bannerBase = findViewById(R.id.activity_pagina_fut_banner_base);
//        bannerBase.loadAd(adRequest);

//        if (modoSelecao) {
//            visibilityBannerBase(View.GONE);
//        } else {
//            visibilityBannerBase(View.VISIBLE);
//        }
    }

//Level 2 ------------------------------------------------------------------------------------------
    private void escondeBarraDeInformacoesDoFut() {
        ConstraintLayout barraDeInformacoesDoFut =
                findViewById(R.id.activity_pagina_fut_dados_fut);
        barraDeInformacoesDoFut.setVisibility(View.GONE);
    }

    private void visibilityCheckBoxSelecionarTodos(int visibility) {
        checkBoxSelecionarTodos.setVisibility(visibility);
        checkBoxSelecionarTodos.setChecked(false);
    }

    private void determinaFutPelaIntent(DeterminaFutCorrespondenteListener listener) {
        fut = (Fut) intent.getSerializableExtra(ABRE_JOGADORES_FUT_EXTRA_FUT);
        appbarTitle = "Página do Fut";
        listener.aposDeterminarFut();
    }

    private void determinaFutPeloEvento(DeterminaFutCorrespondenteListener listener) {
        evento = (Evento) intent.getSerializableExtra
                (ABRE_LISTA_JOGADORES_PARA_SEREM_ADICIONADOS_NO_EVENTO_EXTRA_EVENTO);

        FutDao futDao = FutBusinessDatabase.getInstance(this).getFutDao();

        new ProcuraFutCorrespondenteAPartirDeEventoTask(futDao, evento, futEncontrado -> {
            fut = futEncontrado;
            listener.aposDeterminarFut();
        }).execute();

        appbarTitle = "Selecione os jogadores";
    }

    private void criaAdapter(ListView listViewJogadores) {
        adapter = new PaginaFutAdapter(
                PaginaFutActivity.this,
                fut,
                modoSelecao,
                checkBoxSelecionarTodos);
        listViewJogadores.setAdapter(adapter);
    }

    private void inicializaListaJogadorFutView() {
        JogadorDao jogadorDao = FutBusinessDatabase.getInstance(context).getJogadorDao();
        TextView novosJogadoresDisponiveis =
                findViewById(R.id.activity_pagina_fut_textview_novos_jogadores_disponiveis);
        paginaFutView = new PaginaFutView(
                context,
                adapter,
                jogadorDao,
                fut,
                local,
                tipo,
                diaSemana,
                numeroJogadores,
                numeroMensalistas,
                valorQuadra,
                ganhoEsperadoMensalistas,
                ganhoEsperadoAvulsos,
                lucroPrejuizo,
                padraoAvulso,
                padraoMensalista,
                textoPadraoMensalista,
                textoMensalistas,
                groupGanhoMensalistas,
                groupGanhoAvulsos,
                groupLucroPrejuizo,
                switchMostrarDados,
                groupPadraoAvulso,
                groupPadraoMensalista,
                groupNumeroJogadores,
                groupNumeroMensalistas,
                groupPrecoQuadra,
                usuarioDao,
                novosJogadoresDisponiveis
        );
    }

    private void configuraListViewItemClick(ListView jogadores) {
        if (!modoSelecao) {
            jogadores.setOnItemClickListener((parent, view, position, id) ->
                    abreFormularioEmModoEditaJogadorFut(position));
        }
    }

    private void configuraChoiceMode(ListView listViewJogadores) {
        if (modoSelecao) {
            adapter.setEventoCorrespondente(evento);
        } else {
            listViewJogadores.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
            configuraMultiChoiceModeListener(listViewJogadores);
        }
    }

    private void aoClicarNoFabAdd(FloatingActionButton add) {
        add.setOnClickListener(v -> {
            if (usuario.getSaldoNovoJogador() > 0) {
                abreFormularioEmModoCriaJogadorFut();
            } else {
                new AlertDialog.Builder(context)
                        .setTitle("Sem novos jogadores disponíveis")
                        .setMessage(
    "Você atingiu o número máximo de jogadores criados.\n\nAssista à um vídeo e ganhe mais um espaço!"
                        )
                        .setPositiveButton(
                                "Assitir agora", (dialog, which) -> mostraRewardedAd()
                        )
                        .setNegativeButton("Depois", null)
                        .show();
            }
        });
    }

    private void mostraRewardedAd() {
        RewardedAd.load(
                PaginaFutActivity.this,
                "ca-app-pub-7406067620829259/3498148610",
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
                            public void onAdShowedFullScreenContent() {}

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {}

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
            mRewardedAd.show(PaginaFutActivity.this, rewardItem -> {
                paginaFutView.aumentaSaldoNovoJogador(usuarioDao);
            });
        } else {
            paginaFutView.aumentaSaldoNovoJogador(usuarioDao);
            paginaFutView.atualiza();
        }
    }

//Level 3 ------------------------------------------------------------------------------------------
    private void abreFormularioEmModoEditaJogadorFut(int position) {
        JogadorFut jogadorFut = adapter.getItem(position);
        Intent abreFormularioModoEdita =
                new Intent(PaginaFutActivity.this, FormularioJogadorActivity.class);
        abreFormularioModoEdita.putExtra(EXTRA_FUT, fut);
        abreFormularioModoEdita.putExtra(ABRE_FORMULARIO_JOGADOR_MODO_EDITA_EXTRA_JOGADOR, jogadorFut);
        abreFormularioModoEdita.putExtra(EXTRA_USUARIO, usuario);
        startActivity(abreFormularioModoEdita);
    }

    private void configuraMultiChoiceModeListener(ListView listViewJogadores) {
        listViewJogadores.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode,
                                                  int position,
                                                  long id,
                                                  boolean checked) {
                adapter.setPrimeiroJogador(adapter.getItem(position));
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.activity_pagina_fut_actionmode_menu, menu);
                visibilityFabAdd(View.GONE);
//                visibilityBannerBase(View.GONE);
                adapter.setActionMode(true);
                adapter.setPrimeiroJogadorChecavel(true);
                mode.setTitle("1 jogador(es) selecionado(s)");
                enviaOptionsMenuItemsParaAdapter(mode, menu);
                visibilityCheckBoxSelecionarTodos(View.VISIBLE);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.activity_pagina_fut_actionmode_menu_remover) {
                    paginaFutView.confirmaRemoveJogador(mode::finish);
                    return true;

                } else if (itemId == R.id.activity_pagina_fut_actionmode_menu_mudar_status) {
                    paginaFutView.mudaStatus(adapter.getJogadoresFutSelecionados());
                    mode.finish();
                    return true;

                } else if (itemId == R.id.activity_pagina_fut_actionmode_menu_adicionar_evento) {
                    abreListaEventosParaAdicionarJogadores();
                    mode.finish();
                    return true;

                } else if (itemId == R.id.activity_pagina_fut_actionmode_menu_copiar_para_outro_fut) {
                    abreListaDeFutsParaCopiarJogadores();
                    mode.finish();
                    return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                adapter.setActionMode(false);
                adapter.setPrimeiroJogadorChecavel(true);
                visibilityFabAdd(View.VISIBLE);
//                visibilityBannerBase(View.VISIBLE);
                visibilityCheckBoxSelecionarTodos(View.GONE);
            }
        });
    }

    private void abreFormularioEmModoCriaJogadorFut() {
        Intent abreFormularioEmModoCriaJogadorFut = new Intent(PaginaFutActivity.this,
                FormularioJogadorActivity.class);
        abreFormularioEmModoCriaJogadorFut.putExtra(ABRE_FORMULARIO_JOGADOR_MODO_CRIA_EXTRA_FUT, fut);
        abreFormularioEmModoCriaJogadorFut.putExtra(EXTRA_USUARIO, usuario);
        startActivity(abreFormularioEmModoCriaJogadorFut);
    }

//Level 4 ------------------------------------------------------------------------------------------
    private void visibilityFabAdd(int visibility) {
        FloatingActionButton fabAdd = findViewById(R.id.activity_pagina_fut_fab_add);
        fabAdd.setVisibility(visibility);
    }

//    private void visibilityBannerBase(int visibility) {
//        bannerBase.setVisibility(visibility);
//    }

    private void enviaOptionsMenuItemsParaAdapter(ActionMode mode, Menu menu) {
        adapter.setActionModeReference(mode);

        adapter.setOpcaoRemover(menu.findItem(
                R.id.activity_pagina_fut_actionmode_menu_remover));

        adapter.setOpcaoCopiarParaOutroFut(menu.findItem(
                R.id.activity_pagina_fut_actionmode_menu_copiar_para_outro_fut));

        adapter.setOpcaoAdicionarAUmEvento(menu.findItem(
                R.id.activity_pagina_fut_actionmode_menu_adicionar_evento));

        adapter.setOpcaoMudarStatus(menu.findItem(
                R.id.activity_pagina_fut_actionmode_menu_mudar_status));
    }

    private void abreListaEventosParaAdicionarJogadores() {
        Intent abreListaEventos =
                new Intent(PaginaFutActivity.this, MeusEventosActivity.class);

        abreListaEventos.putExtra(ABRE_EVENTOS_EXTRA_JOGADOR,
                (Serializable) adapter.getJogadoresFutSelecionados());

        abreListaEventos.putExtra(ABRE_EVENTOS_EXTRA_FUT, fut);
        startActivity(abreListaEventos);
    }

    private void abreListaDeFutsParaCopiarJogadores() {
        Intent abreListaFuts =
                new Intent(PaginaFutActivity.this, MeusFutsActivity.class);

        abreListaFuts.putExtra(ABRE_LISTA_FUT_PARA_ADICIONAR_COPIAS_EXTRA_LIST_JOGADOR,
                (Serializable) adapter.getJogadoresFutSelecionados());

        abreListaFuts.putExtra(ABRE_LISTA_FUT_PARA_ADICIONAR_COPIAS_EXTRA_FUT, fut);
        startActivity(abreListaFuts);
    }

//OptionsMenu --------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_pagina_fut_menu, menu);

        if (modoSelecao) {

            menu.findItem(R.id.activity_pagina_fut_menu_adicionar_ao_evento_solicitado).
                    setVisible(true);

            menu.findItem(R.id.activity_pagina_fut_menu_abre_eventos).
                    setVisible(false);

            menu.findItem(R.id.activity_pagina_fut_menu_deleta_fut).
                    setVisible(false);

            menu.findItem(R.id.activity_pagina_fut_menu_edita_fut).
                    setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.activity_pagina_fut_menu_adicionar_ao_evento_solicitado) {
            mostraIntersticialAd();

        } else if (itemId == R.id.activity_pagina_fut_menu_abre_eventos) {
            abreListaEventosParaEntrarNoEvento();

        } else if (itemId == R.id.activity_pagina_fut_menu_deleta_fut) {
            removeFut();

        } else if (itemId == R.id.activity_pagina_fut_menu_edita_fut) {
            abreFormularioEmModoEditaFut();
        }
        return super.onOptionsItemSelected(item);
    }

    private void abreListaEventosParaEntrarNoEvento() {
        Intent abreListaEventos = new Intent(context, MeusEventosActivity.class);
        abreListaEventos.putExtra(ABRE_EVENTOS_EXTRA_FUT, fut);
        startActivity(abreListaEventos);
    }

    private void removeFut() {
        FutDao futDao = FutBusinessDatabase.getInstance(context).getFutDao();
        paginaFutView.confirmaRemoveFut(futDao, this::finish);
    }

    private void abreFormularioEmModoEditaFut() {
        Intent abreFormularioEmModoEditaFut = new Intent(context, FormularioFutActivity.class);
        abreFormularioEmModoEditaFut.putExtra(ABRE_FORMULARIO_DO_FUT_EM_MODO_EDICAO_EXTRA_FUT, fut);
        startActivity(abreFormularioEmModoEditaFut);
        finish();
    }

//Level 2 ------------------------------------------------------------------------------------------
    private void mostraIntersticialAd() {
        InterstitialAd.load(this, "ca-app-pub-7406067620829259/8558903606", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        configuraFullScreenCallback();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        adicionaJogadoresNoEvento();
                        mInterstitialAd = null;
                    }
                });
    }

//Level 3 ------------------------------------------------------------------------------------------
    private void configuraFullScreenCallback() {
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                adicionaJogadoresNoEvento();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                adicionaJogadoresNoEvento();
            }

            @Override
            public void onAdShowedFullScreenContent() {
                mInterstitialAd = null;
            }
        });

        if (mInterstitialAd != null) {
            mInterstitialAd.show(PaginaFutActivity.this);
        }
    }

    private void adicionaJogadoresNoEvento() {
        paginaFutView.adicionaJogadoresNoEvento(
                evento,
                adapter.getJogadoresFutSelecionados(),
                PaginaFutActivity.this::finish);
    }

    //Interface
    public interface DeterminaFutCorrespondenteListener {
        void aposDeterminarFut();
    }
}