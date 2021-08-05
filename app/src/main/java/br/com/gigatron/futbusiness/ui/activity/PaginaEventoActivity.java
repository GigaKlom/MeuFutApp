package br.com.gigatron.futbusiness.ui.activity;

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
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.Group;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import br.com.gigatron.futbusiness.R;
import br.com.gigatron.futbusiness.database.FutBusinessDatabase;
import br.com.gigatron.futbusiness.database.dao.EventoDao;
import br.com.gigatron.futbusiness.database.dao.JogadorDao;
import br.com.gigatron.futbusiness.model.Evento;
import br.com.gigatron.futbusiness.model.JogadorEvento;
import br.com.gigatron.futbusiness.ui.adapter.PaginaEventoAdapter;
import br.com.gigatron.futbusiness.ui.view.PaginaEventoView;
import br.com.gigatron.futbusiness.util.NetworkCheck;

import static br.com.gigatron.futbusiness.Keys.ABRE_JOGADORES_EVENTO_EXTRA_EVENTO;
import static br.com.gigatron.futbusiness.Keys.ABRE_LISTA_JOGADORES_PARA_SEREM_ADICIONADOS_NO_EVENTO_EXTRA_EVENTO;

public class PaginaEventoActivity extends AppCompatActivity {

    private static final String APPBAR_TITLE = "Página do Evento";
    private final Context context = this;
    private PaginaEventoAdapter adapter;
    private PaginaEventoView paginaEventoView;
    private Evento evento;

    private TextView txtData;
    private TextView txtHorario;
    private TextView tituloPrecoAvulsoQuadra;
    private TextView precoAvulsoQuadra;
    private TextView txtGanhoPrevisto;
    private TextView txtGanhoObtido;
    private TextView txtNumeroJogadores;
    private TextView txtJogadoresPagos;
    private TextView txtStatusEvento;
    private TextView referenciaMensal;
    private TextView precoMensalQuadra;
    private TextView totalObtidoAvulsos;
    private TextView totalObtidoMensalistas;
    private TextView lucroPrejuizoMensal;
    private TextView nJogadoresDiferentes;
    private TextView calotesRecebidos;
    private TextView vezesQueFuraram;
    private SwitchCompat switchMostraDadosMensal;
    private SwitchCompat switchMostraDados;
    private Group groupReferenciaMensal;
    private Group groupDadosMensal;
    private Group groupDados;
    private int alteracoes = 0;
    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;
    private CheckBox checkBoxSelecionarTodos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina_evento);
        verificaIntent();
        inicializaCampos();
        configuraCheckBoxSelecionarTodos();
        configuraListViewJogadoresEvento();
        configuraFabAdicionarJogador();
        networkCheck();
        setTitle(APPBAR_TITLE);
        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color=\"#6ed616\">" + APPBAR_TITLE + "</font>"));
        inicializaAds();
    }

    @Override
    protected void onResume() {
        super.onResume();
        paginaEventoView.atualizaLista();
        networkCheck();
        inicializaAds();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        networkCheck();
        return super.onTouchEvent(event);
    }

//Level 1 ------------------------------------------------------------------------------------------
    private void inicializaCampos() {
        txtData =
                findViewById(R.id.activity_pagina_evento_evento_data);
        txtHorario =
                findViewById(R.id.activity_pagina_evento_horario);
        tituloPrecoAvulsoQuadra =
                findViewById(R.id.activity_pagina_evento_titulo_preco_avulso_quadra);
        precoAvulsoQuadra =
                findViewById(R.id.activity_pagina_evento_preco_avulso_quadra);
        txtGanhoPrevisto =
                findViewById(R.id.activity_pagina_evento_previsao);
        txtGanhoObtido =
                findViewById(R.id.activity_pagina_evento_textview_obtido);
        txtNumeroJogadores =
                findViewById(R.id.activity_pagina_evento_textview_total_jogadores);
        txtJogadoresPagos =
                findViewById(R.id.activity_pagina_evento_textview_pagos);
        txtStatusEvento =
                findViewById(R.id.activity_pagina_evento_status_do_evento);
        groupReferenciaMensal =
                findViewById(R.id.activity_pagina_evento_group_referencia_mensal);
        referenciaMensal =
                findViewById(R.id.activity_pagina_evento_referencia_mensal);
        groupDadosMensal =
                findViewById(R.id.activity_pagina_evento_group_dados_mensal);
        groupDados =
                findViewById(R.id.activity_pagina_evento_group_dados);
        switchMostraDadosMensal =
                findViewById(R.id.activity_pagina_evento_switch_mostra_dados_mensal);
        switchMostraDadosMensal =
                findViewById(R.id.activity_pagina_evento_switch_mostra_dados_mensal);
        switchMostraDados =
                findViewById(R.id.activity_pagina_evento_switch_mostra_dados);
        precoMensalQuadra =
                findViewById(R.id.activity_pagina_evento_preco_mensal_quadra);
        totalObtidoAvulsos =
                findViewById(R.id.activity_pagina_evento_total_obtido_avulsos);
        totalObtidoMensalistas =
                findViewById(R.id.activity_pagina_evento_total_obtido_mensalistas);
        lucroPrejuizoMensal =
                findViewById(R.id.activity_pagina_evento_lucro_prejuizo_mensal);
        nJogadoresDiferentes =
                findViewById(R.id.activity_pagina_evento_n_jogadores_diferentes);
        calotesRecebidos =
                findViewById(R.id.activity_pagina_evento_calotes_recebidos);
        vezesQueFuraram =
                findViewById(R.id.activity_pagina_evento_vezes_que_furaram);
    }

    private void verificaIntent() {
        Intent intent = getIntent();
        evento = (Evento) intent.getSerializableExtra(ABRE_JOGADORES_EVENTO_EXTRA_EVENTO);
    }

    private void configuraCheckBoxSelecionarTodos() {
        checkBoxSelecionarTodos = findViewById(R.id.activity_pagina_evento_checkbox_selecionar_todos);
    }

    private void configuraListViewJogadoresEvento() {
        ListView listViewJogadorEvento = findViewById(R.id.activity_pagina_evento_listview);
        criaAdapter(listViewJogadorEvento);
        criaListaJogadorEventoView();
        configuraListViewItemClick(listViewJogadorEvento);
        configuraMultiChoiceMode(listViewJogadorEvento);
        configuraListaEBarraDeInformacoes();
    }

    private void configuraFabAdicionarJogador() {
        FloatingActionButton adicionarJogador =
                findViewById(R.id.activity_pagina_evento_fab_adicionar_jogador);

        if (evento.isAtivo()) {
            adicionarJogador.setOnClickListener(v -> {
                Intent abreListaDeJogadoresParaAdicionarAoEvento =
                        new Intent(context, PaginaFutActivity.class);

                abreListaDeJogadoresParaAdicionarAoEvento.putExtra(
                        ABRE_LISTA_JOGADORES_PARA_SEREM_ADICIONADOS_NO_EVENTO_EXTRA_EVENTO, evento);

                startActivity(abreListaDeJogadoresParaAdicionarAoEvento);
            });
        } else {
            adicionarJogador.setVisibility(View.GONE);
        }
    }

    private void networkCheck() {
        new NetworkCheck().check(context, isConnected -> {});
    }

    private void inicializaAds() {
        MobileAds.initialize(this);
        adRequest = new AdRequest.Builder().build();
        AdView bannerTopo = findViewById(R.id.activity_pagina_evento_banner_topo);
        bannerTopo.loadAd(adRequest);
    }

//Level 2 ------------------------------------------------------------------------------------------
    private void visibilityCheckBoxSelecionarTodos(int visibility) {
        checkBoxSelecionarTodos.setVisibility(visibility);
        checkBoxSelecionarTodos.setChecked(false);
    }

    private void criaAdapter(ListView listViewJogadorEvento) {
        adapter = new PaginaEventoAdapter(context, checkBoxSelecionarTodos);
        listViewJogadorEvento.setAdapter(adapter);
    }

    private void criaListaJogadorEventoView() {
        FutBusinessDatabase database = FutBusinessDatabase.getInstance(context);
        JogadorDao jogadorDao = database.getJogadorDao();
        EventoDao eventoDao = database.getEventoDao();
        paginaEventoView = new PaginaEventoView(
                context,
                adapter,
                evento,
                jogadorDao,
                eventoDao,
                tituloPrecoAvulsoQuadra,
                precoAvulsoQuadra,
                txtGanhoPrevisto,
                txtGanhoObtido,
                txtNumeroJogadores,
                txtJogadoresPagos,
                txtStatusEvento,
                groupReferenciaMensal,
                referenciaMensal,
                groupDadosMensal,
                groupDados, switchMostraDadosMensal,
                switchMostraDados, precoMensalQuadra,
                totalObtidoAvulsos,
                totalObtidoMensalistas,
                lucroPrejuizoMensal,
                nJogadoresDiferentes,
                calotesRecebidos,
                vezesQueFuraram);
    }

    private void configuraListViewItemClick(ListView listViewEvento) {
        listViewEvento.setOnItemClickListener((parent, view, position, id) -> {
            JogadorEvento jogadorEvento = adapter.getItem(position);
            if (evento.isAtivo()) {
                if (
                        !jogadorEvento.isFuro() &&
                        ((jogadorEvento.getValorAvulso() > 0 && !jogadorEvento.isMensalista()) ||
                        (jogadorEvento.getValorMensal() > 0 && jogadorEvento.isMensalista()))
                ) {
                    paginaEventoView.alteraStatusDoPagamento(jogadorEvento);
                    contaAlteracoes();
                }
            }
        });
    }

    private void configuraMultiChoiceMode(ListView listViewJogadorEvento) {
        if (evento.isAtivo()) {
            listViewJogadorEvento.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
            configuraMultiChoiceModeListener(listViewJogadorEvento);
        }
    }

    private void configuraListaEBarraDeInformacoes() {
        paginaEventoView.atualizaLista();
        paginaEventoView.atribuiInformacoesDoEventoAoTitulo(txtData, txtHorario);
    }

//Level 3 ------------------------------------------------------------------------------------------
    private void contaAlteracoes() {
        alteracoes += 1;
        if (alteracoes == 10) {
            mostraIntersticialAd();
            alteracoes = 0;
        }
    }

    private void configuraMultiChoiceModeListener(ListView listViewJogadorEvento) {
        listViewJogadorEvento.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(
                    ActionMode mode,
                    int position,
                    long id,
                    boolean checked
            ) {
                adapter.setPrimeiroJogadorSelecionado(adapter.getItem(position));
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.activity_pagina_evento_actionmode_menu, menu);
                visibilityFabAdd(View.GONE);
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

                if (itemId == R.id.activity_pagina_evento_actionmode_menu_remover) {
                    paginaEventoView.confirmaRemoveJogadores(mode::finish);
                    return true;

                } else if (itemId == R.id.activity_pagina_evento_actionmode_menu_marcar_furo) {
                    paginaEventoView.alteraStatusDeFaltanteJogador(mode::finish);
                    return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                adapter.setActionMode(false);
                adapter.setPrimeiroJogadorChecavel(true);
                visibilityFabAdd(View.VISIBLE);
                visibilityCheckBoxSelecionarTodos(View.GONE);
            }
        });
    }

//Level 4 ------------------------------------------------------------------------------------------
    private void visibilityFabAdd(int visibility) {
        FloatingActionButton fabAdd =
                findViewById(R.id.activity_pagina_evento_fab_adicionar_jogador);

        fabAdd.setVisibility(visibility);
    }

    private void enviaOptionsMenuItemsParaAdapter(ActionMode mode, Menu menu) {
        adapter.setActionModeReference(mode);
        adapter.setOpcaoRemover(menu.findItem(R.id.activity_pagina_evento_actionmode_menu_remover));

        adapter.setOpcaoMarcarFaltante(
                menu.findItem(R.id.activity_pagina_evento_actionmode_menu_marcar_furo)
        );
    }

//OptionsMenu --------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_pagina_evento_menu, menu);
        MenuItem finalizaEvento = menu.findItem(R.id.activity_pagina_evento_menu_finaliza_evento);
        if (evento.isAtivo()) {
            finalizaEvento.setIcon(R.drawable.ic_action_lockopen);
            finalizaEvento.setEnabled(true);
        } else {
            finalizaEvento.setIcon(R.drawable.ic_action_lock);
            finalizaEvento.setEnabled(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.activity_pagina_evento_menu_finaliza_evento) {
            paginaEventoView.confirmaFinalizaEvento(isConfirmado -> {
                if (isConfirmado) {
                    mostraIntersticialAd();
                    finish();
                }
            });
        } else if (itemId == R.id.activity_pagina_evento_menu_deleta_evento) {
            paginaEventoView.confirmaRemoveEvento(this::finish);
        }
        return super.onOptionsItemSelected(item);
    }

//Level 2 ------------------------------------------------------------------------------------------
    private void mostraIntersticialAd() {
        InterstitialAd.load(this, "ca-app-pub-7406067620829259/3003324597", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        configuraFullScreenCallback();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mInterstitialAd = null;
                    }
                });
    }

//Level 3 ------------------------------------------------------------------------------------------
    private void configuraFullScreenCallback() {
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {}

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {

            }

            @Override
            public void onAdShowedFullScreenContent() {
                mInterstitialAd = null;
            }
        });

        if (mInterstitialAd != null) {
            mInterstitialAd.show(PaginaEventoActivity.this);
        }
    }
}