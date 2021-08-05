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
import android.widget.ImageButton;
import android.widget.ListView;
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
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;
import java.util.Objects;

import br.com.gigatron.futbusiness.R;
import br.com.gigatron.futbusiness.asynctask.DefineUsuarioTask;
import br.com.gigatron.futbusiness.database.FutBusinessDatabase;
import br.com.gigatron.futbusiness.database.dao.EventoDao;
import br.com.gigatron.futbusiness.database.dao.UsuarioDao;
import br.com.gigatron.futbusiness.model.Evento;
import br.com.gigatron.futbusiness.model.Fut;
import br.com.gigatron.futbusiness.model.JogadorFut;
import br.com.gigatron.futbusiness.model.Usuario;
import br.com.gigatron.futbusiness.ui.adapter.MeusEventosAdapter;
import br.com.gigatron.futbusiness.ui.view.MeusEventosView;
import br.com.gigatron.futbusiness.util.NetworkCheck;

import static br.com.gigatron.futbusiness.Keys.ABRE_EVENTOS_EXTRA_FUT;
import static br.com.gigatron.futbusiness.Keys.ABRE_EVENTOS_EXTRA_JOGADOR;
import static br.com.gigatron.futbusiness.Keys.ABRE_JOGADORES_EVENTO_EXTRA_EVENTO;

public class MeusEventosActivity extends AppCompatActivity {

    private String appbarTitle;
    private final Context context = this;
    private MeusEventosAdapter adapter;
    private MeusEventosView meusEventosView;
    private EventoDao eventoDao;
    private Fut fut;
    private List<JogadorFut> jogadoresFutSelecionados;
    private SwitchMaterial mostrarArquivados;
    private boolean modoSelecao;
    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;
    private UsuarioDao usuarioDao;
    private Usuario usuario;
    private RewardedAd mRewardedAd;
    private CheckBox checkBoxSelecionarTodos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_eventos);
        defineUsuario();
        verificaIntent();
        configuraBotoesRewardedAd();
        configuraCheckBoxSelecionarTodos();
        configuraListViewEventos();
        configuraFabAdd();
        configuraSwitchArquivados();
        networkCheck();
        setTitle(appbarTitle);
        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color=\"#6ed616\">" + appbarTitle + "</font>"));
        inicializaAds();
    }

    @Override
    protected void onResume() {
        super.onResume();
        defineUsuario();
        meusEventosView.atualiza(false);
        mostrarArquivados.setChecked(false);
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
        new DefineUsuarioTask(usuarioDao, usuario -> MeusEventosActivity.this.usuario = usuario)
                .execute();
    }

    private void verificaIntent() {
        Intent intent = getIntent();
        fut = (Fut) intent.getSerializableExtra(ABRE_EVENTOS_EXTRA_FUT);
        if (intent.hasExtra(ABRE_EVENTOS_EXTRA_JOGADOR)) {
            Bundle bundle = intent.getExtras();
            jogadoresFutSelecionados = bundle.getParcelableArrayList(ABRE_EVENTOS_EXTRA_JOGADOR);
            modoSelecao = true;
            appbarTitle = "Selecione os eventos";
        } else {
            modoSelecao = false;
            appbarTitle = "Meus Eventos";
        }
    }

    private void configuraBotoesRewardedAd() {
        ImageButton assistirRewardedAdEventos =
                findViewById(R.id.activity_meus_eventos_imagebutton_assistir_rewardedAd_eventos);
        ImageButton assistirRewardedAdArquivamentos =
                findViewById(R.id.activity_meus_eventos_imagebutton_assistir_rewardedAd_arquivamentos);
        Group novosArquivamentos =
                findViewById(R.id.activity_meus_eventos_group_novos_arquivamentos_disponiveis);
        configuraClickListenerBotoesReward(assistirRewardedAdEventos, assistirRewardedAdArquivamentos);
        configuraVisibilityGroupNovosArquivamentos(novosArquivamentos);
    }

    private void configuraCheckBoxSelecionarTodos() {
        checkBoxSelecionarTodos = findViewById(R.id.activity_meus_eventos_checkbox_selecionar_todos);
        if (modoSelecao) {
            visibilityCheckBoxSelecionarTodos(View.VISIBLE);
        } else {
            visibilityCheckBoxSelecionarTodos(View.GONE);
        }
    }

    private void configuraListViewEventos() {
        inicializaDao();
        ListView listViewEventos = findViewById(R.id.activity_meus_eventos_listview);
        configuraAdapter(listViewEventos);
        inicializaListaEventoView();
        configuraListViewItemClick(listViewEventos);
        configuraMultiChoiceMode(listViewEventos);
        meusEventosView.atualiza(false);
    }

    private void configuraFabAdd() {
        FloatingActionButton fabAdd = findViewById(R.id.activity_meus_eventos_fab_add);
        if (modoSelecao) {
            fabAdd.setVisibility(View.GONE);
        } else {
            fabAdd.setVisibility(View.VISIBLE);
        }
        configuraFabClickListener(fabAdd);
    }

    private void configuraSwitchArquivados() {
        mostrarArquivados = findViewById(R.id.activity_meus_eventos_switch_mostrar_arquivados);
        configuraVisibilitySwitchMostrarArquivados();
        configuraCheckedChangeSwitchMostrarArquivados();
        configuraEnabledSwitchMostrarArquivados();
    }

    private void networkCheck() {
        new NetworkCheck().check(context, isConnected -> {});
    }

    private void inicializaAds() {
        MobileAds.initialize(this);
        adRequest = new AdRequest.Builder().build();

        AdView bannerTopo = findViewById(R.id.activity_meus_eventos_banner_topo);
        bannerTopo.loadAd(adRequest);

//        AdView bannerBase = findViewById(R.id.activity_meus_eventos_banner_base);
//        bannerBase.loadAd(adRequest);
    }

//Level 2 ------------------------------------------------------------------------------------------
    private void configuraClickListenerBotoesReward(
            ImageButton assistirRewardedAdEventos,
            ImageButton assistirRewardedAdArquivamentos
    ) {
        assistirRewardedAdEventos.setOnClickListener(v -> mostraRewardedAd(() -> {
            Toast.makeText(context, "+1 Evento liberado!!", Toast.LENGTH_SHORT).show();
            meusEventosView.aumentaSaldoNovoEvento(usuarioDao);
        }));

        assistirRewardedAdArquivamentos.setOnClickListener(v -> mostraRewardedAd(() -> {
            Toast.makeText(
                    context, "+1 espaço de arquivamento liberado!!", Toast.LENGTH_SHORT
            ).show();
            meusEventosView.aumentaSaldoNovoArquivamento(usuarioDao);
        }));
    }

    private void configuraVisibilityGroupNovosArquivamentos(Group novosArquivamentos) {
        if (modoSelecao) {
            novosArquivamentos.setVisibility(View.GONE);
        } else {
            novosArquivamentos.setVisibility(View.VISIBLE);
        }
    }

    private void visibilityCheckBoxSelecionarTodos(int visibility) {
        checkBoxSelecionarTodos.setVisibility(visibility);
        checkBoxSelecionarTodos.setChecked(false);
    }

    private void inicializaDao() {
        eventoDao = FutBusinessDatabase.getInstance(context).getEventoDao();
    }

    private void configuraAdapter(ListView listViewEventos) {
        adapter = new MeusEventosAdapter(context, fut, modoSelecao, checkBoxSelecionarTodos);
        listViewEventos.setAdapter(adapter);
    }

    private void inicializaListaEventoView() {
        TextView novosEventosDisponiveis =
                findViewById(R.id.activity_meus_eventos_textview_novos_eventos_disponiveis);
        TextView novosArquivamentosDisponiveis =
                findViewById(R.id.activity_meus_eventos_textview_novos_arquivamentos_disponiveis);

        meusEventosView = new MeusEventosView(
                context,
                adapter,
                eventoDao,
                fut,
                novosEventosDisponiveis,
                novosArquivamentosDisponiveis,
                usuarioDao);
    }

    private void configuraListViewItemClick(ListView eventos) {
        eventos.setOnItemClickListener((parent, view, position, id) -> {
            Evento evento = adapter.getItem(position);

            if (!modoSelecao) { abreListaJogadorEvento(evento); }

        });
    }

    private void configuraMultiChoiceMode(ListView listViewEventos) {
        if (!modoSelecao) {
            listViewEventos.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
            configuraMultiChoiceListener(listViewEventos);
        }
    }

    private void configuraFabClickListener(FloatingActionButton fabAdd) {
        fabAdd.setOnClickListener(v -> {
            criaEventoOuOfereceRewardedAd();
            meusEventosView.atualiza(mostrarArquivados.isChecked());
        });
    }

    private void configuraCheckedChangeSwitchMostrarArquivados() {
        mostrarArquivados.setOnCheckedChangeListener((buttonView, isChecked) -> {
            meusEventosView.atualiza(isChecked);
            meusEventosView.mostrarArquivadosChecked(isChecked);
        });
    }

    private void configuraEnabledSwitchMostrarArquivados() {
        meusEventosView.existemEventosArquivados(existem -> {
            if (!existem) {
                mostrarArquivados.setChecked(false);
                mostrarArquivados.setEnabled(false);
            } else {
                mostrarArquivados.setEnabled(true);
            }
        });
    }

    private void configuraVisibilitySwitchMostrarArquivados() {
        if (modoSelecao) {
            mostrarArquivados.setVisibility(View.GONE);
        } else {
            mostrarArquivados.setVisibility(View.VISIBLE);
        }
    }

//Level 3 ------------------------------------------------------------------------------------------
    private void ofereceRewardedAd(String tipo, OfereceRewardedAdListener listener) {
        new AlertDialog.Builder(context)
                .setTitle("Sem novos " + tipo + "s disponíveis")
                .setMessage(
                        "Você atingiu o número máximo de " + tipo + "s criados.\n\n" +
                                "Assista à um vídeo e ganhe mais um espaço!"
                )
                .setPositiveButton(
                        "Assitir agora", (dialog, which) -> mostraRewardedAd(() -> {
                            Toast.makeText(
                                    context,
                                    "+1 "+ tipo + " liberado!!",
                                    Toast.LENGTH_SHORT
                            ).show();
                            listener.userReward();
                        })
                )
                .setNegativeButton("Depois", (dialog, which) -> {
                    listener.denyRewardedAd();
                })
                .show();
    }

    private void configuraMultiChoiceListener(ListView listViewEventos) {
        listViewEventos.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode,
                                                  int position,
                                                  long id,
                                                  boolean checked
            ) {
                adapter.setPrimeiroEventoSelecionado(adapter.getItem(position));
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.activity_meus_eventos_actionmode_menu, menu);
                escondeFabAddESwitchMostrarArquivados(true);
                adapter.setActionMode(true);
                adapter.setPrimeiroEventoChecavel(true);
                mode.setTitle("1 evento(s) selecionado(s)");
                enviaOptionsMenuItemsParaOAdapter(mode, menu);
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
                if (itemId == R.id.activity_lista_evento_actionmode_menu_editar) {
                    if (adapter.getEventosSelecionados().size() == 1) {
                        abreDialogEditaEvento(adapter.getEventosSelecionados().get(0));
                    }
                    mode.finish();
                    return true;

                } else if (itemId == R.id.activity_lista_evento_actionmode_menu_remover) {
                    meusEventosView.confirmaRemove(mode::finish);
                    return true;

                } else if (itemId == R.id.activity_lista_evento_actionmode_menu_finaliza_evento) {
                    meusEventosView.confirmaFinalizaEvento(
                            isConfirmado -> {
                                if (isConfirmado) { mostraIntersticialAd(mode::finish); }
                            });
                    return true;

                } else if (itemId == R.id.activity_lista_evento_actionmode_menu_arquiva_evento) {
                    if (usuario.getSaldoArquivaEvento() >= adapter.getEventosSelecionados().size()) {
                        meusEventosView.arquivaDesarquivaEvento(true, isConfirmado -> mode.finish());
                    } else {
                        ofereceRewardedAd(
                                "arquivamento",
                                new OfereceRewardedAdListener() {
                                    @Override
                                    public void userReward() {
                                        meusEventosView.aumentaSaldoNovoArquivamento(usuarioDao);
                                    }

                                    @Override
                                    public void denyRewardedAd() {
                                        mode.finish();
                                    }
                                }
                        );
                    }
                    return true;

                } else if (itemId == R.id.activity_lista_evento_actionmode_menu_desarquiva_evento) {
                    meusEventosView.arquivaDesarquivaEvento(false,
                            isConfirmado -> mode.finish());
                    return true;

                } else if (itemId == R.id.activity_lista_evento_actionmode_menu_finaliza_e_arquiva_evento) {
                    meusEventosView.confirmaFinalizaEvento(isConfirmado -> {
                        if (usuario.getSaldoArquivaEvento() >= adapter.getEventosSelecionados().size()) {
                            if (isConfirmado) {
                                meusEventosView.arquivaDesarquivaEvento
                                        (true, isConfirmado1 -> mode.finish());
                            }
                        } else {
                            ofereceRewardedAd(
                                    "arquivamento",
                                    new OfereceRewardedAdListener() {
                                        @Override
                                        public void userReward() {
                                            meusEventosView.aumentaSaldoNovoArquivamento(usuarioDao);
                                            mode.finish();
                                        }

                                        @Override
                                        public void denyRewardedAd() {
                                            mode.finish();
                                        }
                                    }
                            );
                        }
                    });
                    return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                adapter.setActionMode(false);
                adapter.setPrimeiroEventoChecavel(true);
                escondeFabAddESwitchMostrarArquivados(false);
                configuraEnabledSwitchMostrarArquivados();
                meusEventosView.atualiza(mostrarArquivados.isChecked());
                defineUsuario();
                visibilityCheckBoxSelecionarTodos(View.GONE);
            }
        });
    }

    private void criaEventoOuOfereceRewardedAd() {
        if (usuario.getSaldoNovoEvento() > 0) {
            if (fut.isMensal()) {
                abreDialogCriaEventoMensal();
            } else {
                abreDialogCriaEventoAvulso();
            }
        } else {
            ofereceRewardedAd(
                    "evento",
                    new OfereceRewardedAdListener() {
                        @Override
                        public void userReward() {
                            meusEventosView.aumentaSaldoNovoEvento(usuarioDao);
                        }

                        @Override
                        public void denyRewardedAd() {

                        }
                    }
            );
        }
    }

//Level 4 ------------------------------------------------------------------------------------------
    private void abreDialogCriaEventoAvulso() {
        CriaEventoAvulsoDialog criaEventoAvulsoDialog = new CriaEventoAvulsoDialog(
                context,
                eventoDao,
                fut,
                meusEventosView,
                false,
                null,
                mostrarArquivados.isChecked(),
                usuario,
                usuarioDao
        );
        criaEventoAvulsoDialog.show(getSupportFragmentManager(), "evento dialog");
    }

    private void abreDialogCriaEventoMensal() {
        CriaEventoMensalDialog criaEventoMensalDialog = new CriaEventoMensalDialog(
                fut,
                eventoDao,
                context,
                meusEventosView,
                mostrarArquivados.isChecked(),
                usuario,
                usuarioDao
        );
        criaEventoMensalDialog.show(getSupportFragmentManager(), "evento mensal dialog");
    }

//Level 5 ------------------------------------------------------------------------------------------
    private void enviaOptionsMenuItemsParaOAdapter(ActionMode mode, Menu menu) {
        adapter.setActionModeReference
                (mode);
        adapter.setOpcaoEditar
                (menu.findItem(R.id.activity_lista_evento_actionmode_menu_editar));
        adapter.setOpcaoRemover
                (menu.findItem(R.id.activity_lista_evento_actionmode_menu_remover));
        adapter.setOpcaoArquivar
                (menu.findItem(R.id.activity_lista_evento_actionmode_menu_arquiva_evento));
        adapter.setOpcaoDesarquivar
                (menu.findItem(R.id.activity_lista_evento_actionmode_menu_desarquiva_evento));
        adapter.setOpcaoFinalizar
                (menu.findItem(R.id.activity_lista_evento_actionmode_menu_finaliza_evento));
        adapter.setOpcaoFinalizarEArquivar
                (menu.findItem(R.id.activity_lista_evento_actionmode_menu_finaliza_e_arquiva_evento));
    }

    private void escondeFabAddESwitchMostrarArquivados(boolean esconde) {
        FloatingActionButton fabAdd = findViewById(R.id.activity_meus_eventos_fab_add);
        if (esconde) {
            fabAdd.setVisibility(View.GONE);
            mostrarArquivados.setVisibility(View.GONE);
        } else {
            fabAdd.setVisibility(View.VISIBLE);
            mostrarArquivados.setVisibility(View.VISIBLE);
        }
    }

    private void abreListaJogadorEvento(Evento evento) {
        Intent abreListaJogadorEvento = new Intent(context, PaginaEventoActivity.class);
        abreListaJogadorEvento.putExtra(ABRE_JOGADORES_EVENTO_EXTRA_EVENTO, evento);
        startActivity(abreListaJogadorEvento);
    }

    private void abreDialogEditaEvento(Evento evento) {
        CriaEventoAvulsoDialog criaEventoAvulsoDialog = new CriaEventoAvulsoDialog(
                context,
                eventoDao,
                fut,
                meusEventosView,
                true,
                evento,
                mostrarArquivados.isChecked(),
                usuario, usuarioDao);
        criaEventoAvulsoDialog.show(getSupportFragmentManager(), "evento dialog");
    }

    private void mostraRewardedAd(MostraRewardedAdListener listener) {
        RewardedAd.load(
                MeusEventosActivity.this,
                "ca-app-pub-7406067620829259/9951876328",
                adRequest,
                new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        aumentaSaldo(listener);
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
                                defineUsuario();
                            }
                        });
                        aumentaSaldo(listener);
                    }
                });
    }

    private void aumentaSaldo(MostraRewardedAdListener listener) {
        if (mRewardedAd != null) {
            mRewardedAd.show(MeusEventosActivity.this, rewardItem -> {
                listener.userReward();
            });
        } else {
            listener.userReward();
            meusEventosView.atualiza(mostrarArquivados.isChecked());
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
            List<Evento> eventosSelecionados = adapter.getEventosSelecionados();
            mostraIntersticialAd(() -> meusEventosView.adicionaJogadoresNoEvento(
                    eventosSelecionados,
                    jogadoresFutSelecionados,
                    MeusEventosActivity.this::finish
            ));
        } else if (itemId == R.id.done_menu_add) {
            criaEventoOuOfereceRewardedAd();
        }
        return super.onOptionsItemSelected(item);
    }

//Level 2 ------------------------------------------------------------------------------------------
    private void mostraIntersticialAd(OnAdDismissedFullScreenContentListener listener) {
        InterstitialAd.load(this, "ca-app-pub-7406067620829259/2292878155", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        configuraFullScreenCallback(listener);
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        listener.onAdDismissedFullScreenContent();
                        mInterstitialAd = null;
                    }
                });
    }

//Level 3 ------------------------------------------------------------------------------------------
    private void configuraFullScreenCallback(OnAdDismissedFullScreenContentListener listener) {
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                listener.onAdDismissedFullScreenContent();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                listener.onAdDismissedFullScreenContent();
            }

            @Override
            public void onAdShowedFullScreenContent() {
                mInterstitialAd = null;
            }
        });

        if (mInterstitialAd != null) {
            mInterstitialAd.show(MeusEventosActivity.this);
        }
    }

    private interface OnAdDismissedFullScreenContentListener {
        void onAdDismissedFullScreenContent();
    }

//Interfaces ---------------------------------------------------------------------------------------
    public interface MostraRewardedAdListener {
        void userReward();
    }

    public interface OfereceRewardedAdListener {
        void userReward();
        void denyRewardedAd();
    }
}