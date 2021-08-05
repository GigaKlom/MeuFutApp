package br.com.gigatron.futbusiness.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.gigatron.futbusiness.R;
import br.com.gigatron.futbusiness.asynctask.paginafut.GeraCopiaListaJogadorDaoTask;
import br.com.gigatron.futbusiness.database.FutBusinessDatabase;
import br.com.gigatron.futbusiness.database.dao.JogadorDao;
import br.com.gigatron.futbusiness.model.Evento;
import br.com.gigatron.futbusiness.model.Fut;
import br.com.gigatron.futbusiness.model.Jogador;
import br.com.gigatron.futbusiness.model.JogadorEvento;
import br.com.gigatron.futbusiness.model.JogadorFut;
import br.com.gigatron.futbusiness.ui.adapter.meufut.baseadapter.MeuFutAdapter;

public class PaginaFutAdapter extends MeuFutAdapter {

    private final Fut fut;
    private Evento eventoCorrespondente;
    private MenuItem opcaoCopiarParaOutroFut;
    private MenuItem opcaoAdicionarAUmEvento;
    private MenuItem opcaoMudarStatus;
    private final CheckBox checkBoxSelecionarTodos;
    private final List<CheckBox> checkboxes = new ArrayList<>();
    private boolean primeiroJogadorChecavel;

    public PaginaFutAdapter(
            Context context,
            Fut fut,
            boolean modoSelecao,
            CheckBox checkBoxSelecionarTodos
    ) {
        super(context, modoSelecao);
        this.fut = fut;
        this.checkBoxSelecionarTodos = checkBoxSelecionarTodos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View viewCriada = createView(parent, R.layout.item_jogadorfut);
        JogadorFut jogadorFut = (JogadorFut) objects.get(position);
        vincula(viewCriada, jogadorFut);
        configuraActionMode(viewCriada, jogadorFut);
        return viewCriada;
    }

//Public Methods -----------------------------------------------------------------------------------
//Level 1 ------------------------------------------------------------------------------------------
    public List<JogadorFut> getJogadoresFutSelecionados() {
        List<JogadorFut> listObjectToJogadorFut = new ArrayList<>();
        for (Object object: objectsSelecionados) {
            listObjectToJogadorFut.add((JogadorFut) object);
        }
        return listObjectToJogadorFut;
    }

    public void atualiza(List<JogadorFut> jogadoresFut) {
        this.objects.clear();
        this.objects.addAll(jogadoresFut);
        notifyDataSetChanged();
    }

//Level 2 ------------------------------------------------------------------------------------------
    public void setOpcaoCopiarParaOutroFut(MenuItem opcaoCopiarParaOutroFut) {
        this.opcaoCopiarParaOutroFut = opcaoCopiarParaOutroFut;
    }

    public void setOpcaoAdicionarAUmEvento(MenuItem opcaoAdicionarAUmEvento) {
        this.opcaoAdicionarAUmEvento = opcaoAdicionarAUmEvento;
    }

    public void setOpcaoMudarStatus(MenuItem opcaoMudarStatus) {
        this.opcaoMudarStatus = opcaoMudarStatus;
    }

    public void setPrimeiroJogador(Jogador primeiroJogadorSelecionado) {
        primeiroObjectSelecionado = primeiroJogadorSelecionado;
    }

    public void setEventoCorrespondente(Evento eventoCorrespondente) {
        this.eventoCorrespondente = eventoCorrespondente;
    }

    public void setPrimeiroJogadorChecavel(boolean primeiroJogadorChecavel) {
        this.primeiroJogadorChecavel = primeiroJogadorChecavel;
    }

    //Private methods ----------------------------------------------------------------------------------
//Level 1 ------------------------------------------------------------------------------------------
    private void vincula(View view, JogadorFut jogadorFut) {
        configuraTextViewNome(view, jogadorFut);
        configuraTextViewStatus(view, jogadorFut);
    }

    private void configuraActionMode(View viewCriada, JogadorFut jogadorFut) {
        super.configuraActionMode(viewCriada, jogadorFut, R.id.item_jogadorfut_checkbox,
                new ConfiguraActionModeListener() {
                    @Override
                    public void setMethodsModoSelecao(CheckBox checkBox) {
                        configuraComportamentoCheckBoxModoSelecao(jogadorFut, checkBox);
                    }

                    @Override
                    public void setMethodsActionMode(CheckBox checkBox) {
                        configuraOpcoesMenu();
                        checkBox.setVisibility(View.VISIBLE);
                        configuraCheckPrimeiroJogadorSelecionado(jogadorFut, checkBox);
                    }

                    @Override
                    public void aposSetarComportamentos(CheckBox checkBox) {
                        configuraCheckedChange(jogadorFut, checkBox);
                        configuraCheckBoxSelecionarTodos();
                    }
                });
    }

//Level 2 ------------------------------------------------------------------------------------------
    private void configuraTextViewNome(View view, Jogador jogador) {
        TextView nome = view.findViewById(R.id.item_jogadorfut_nome);
        nome.setText(jogador.getNome());
    }
    private void configuraTextViewStatus(View view, Jogador jogador) {
        TextView status = view.findViewById(R.id.item_jogadorfut_status);
        String textStatus;
        if (jogador.isMensalista()) {
            textStatus = "Mensalista - R$ " +
                    String.valueOf(jogador.getValorMensal()).replace(".",",") + "0";
        } else {
            textStatus = "Avulso - R$ " +
                    String.valueOf(jogador.getValorAvulso()).replace(".",",") + "0";
        }
        status.setText(textStatus);
    }

    private void configuraComportamentoCheckBoxModoSelecao(JogadorFut jogadorFut, CheckBox checkBox) {
        checkBox.setVisibility(View.VISIBLE);
        checaJogadoresJaExistentesNoEvento(checkBox, jogadorFut);
    }

    private void configuraOpcoesMenu() {
        opcaoMudarStatus.setVisible(fut.isMensal());
    }

    private void configuraCheckPrimeiroJogadorSelecionado(JogadorFut jogadorFut, CheckBox checkBox) {
        if (jogadorFut == primeiroObjectSelecionado && primeiroJogadorChecavel) {
            checkBox.setChecked(true);
            primeiroJogadorChecavel = false;
            if (!objectsSelecionados.contains(jogadorFut)) {
                objectsSelecionados.add(jogadorFut);
            }
        }
    }

    private void checaJogadoresJaExistentesNoEvento(CheckBox checkBox, JogadorFut jogadorFut) {
        JogadorDao jogadorDao = FutBusinessDatabase.getInstance(context).getJogadorDao();
        new GeraCopiaListaJogadorDaoTask(jogadorDao, eventoCorrespondente,
                jogadoresDoEvento -> {
                    for (JogadorEvento jogadorEvento: jogadoresDoEvento) {
                        if (jogadorFut.getJogadorFutId() == jogadorEvento.getJogadorFutId()) {
                            checkBox.setChecked(true);
                            checkBox.setEnabled(false);
                        }
                    }
                }).execute();
    }

    private void configuraCheckedChange(JogadorFut jogadorFut, CheckBox checkBox) {
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!objectsSelecionados.contains(jogadorFut)) {
                    objectsSelecionados.add(jogadorFut);
                }
            } else {
                objectsSelecionados.remove(jogadorFut);
                Log.i("CUDOLEO", "configuraCheckedChange: " + objectsSelecionados);
                checkBoxSelecionarTodos.setChecked(false);
            }
            if (!modoSelecao) {
                if (isActionMode) {
                    configuraChoiceModeOptions();
                }
            }
        });
        if (checkBoxSelecionarTodos.isChecked()) {
            checkBox.setChecked(true);
        }
        checkboxes.add(checkBox);
    }

    private void configuraCheckBoxSelecionarTodos() {
        checkBoxSelecionarTodos.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                for (CheckBox checkBox1: checkboxes) {
                    if (!checkBox1.isChecked()){
                        checkBox1.setChecked(true);
                    }
                }
            }
        });
    }

//Level 3 ------------------------------------------------------------------------------------------
    private void configuraChoiceModeOptions() {
        mode.setTitle(objectsSelecionados.size() + " jogador(es) selecionad(os)");
        opcaoRemover.setVisible(objectsSelecionados.size() > 0);
        opcaoCopiarParaOutroFut.setVisible(objectsSelecionados.size() > 0);
        opcaoAdicionarAUmEvento.setVisible(objectsSelecionados.size() > 0);
        opcaoMudarStatus.setVisible(objectsSelecionados.size() > 0 && fut.isMensal());
    }


//Métodos obrigatórios -----------------------------------------------------------------------------
    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public JogadorFut getItem(int position) {
        return (JogadorFut) objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        JogadorFut jogadorFut = (JogadorFut) objects.get(position);
        return jogadorFut.getJogadorFutId();
    }
}
