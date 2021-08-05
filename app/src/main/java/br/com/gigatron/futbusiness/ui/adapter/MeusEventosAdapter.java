package br.com.gigatron.futbusiness.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.gigatron.futbusiness.R;
import br.com.gigatron.futbusiness.model.Evento;
import br.com.gigatron.futbusiness.model.Fut;
import br.com.gigatron.futbusiness.ui.adapter.meufut.baseadapter.MeuFutAdapter;

public class MeusEventosAdapter extends MeuFutAdapter {

    private final Fut fut;
    private MenuItem opcaoEditar;
    private MenuItem opcaoArquivar;
    private MenuItem opcaoDesarquivar;
    private MenuItem opcaoFinalizar;
    private MenuItem opcaoFinalizarEArquivar;
    private final CheckBox checkBoxSelecionarTodos;
    private final List<CheckBox> checkboxes = new ArrayList<>();
    private boolean primeiroEventoChecavel;

    public MeusEventosAdapter(
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
        View viewCriada = createView(parent, R.layout.item_evento);
        Evento evento = (Evento) objects.get(position);
        configuraActionMode(viewCriada, evento);
        vincula(viewCriada, evento);
        return viewCriada;
    }

//Public methods -----------------------------------------------------------------------------------
//Level 1 ------------------------------------------------------------------------------------------
    public void atualiza(List<Evento> eventos) {
        this.objects.clear();
        this.objects.addAll(eventos);
        notifyDataSetChanged();
    }

//Level 2 ------------------------------------------------------------------------------------------
    public void setOpcaoEditar(MenuItem opcaoEditar) {
        this.opcaoEditar = opcaoEditar;
    }

    public void setOpcaoArquivar(MenuItem opcaoArquivar) {
        this.opcaoArquivar = opcaoArquivar;
    }

    public void setOpcaoDesarquivar(MenuItem opcaoDesarquivar) {
        this.opcaoDesarquivar = opcaoDesarquivar;
    }

    public void setOpcaoFinalizar(MenuItem opcaoFinalizar) {
        this.opcaoFinalizar = opcaoFinalizar;
    }

    public void setOpcaoFinalizarEArquivar(MenuItem opcaoFinalizarEArquivar) {
        this.opcaoFinalizarEArquivar = opcaoFinalizarEArquivar;
    }

    public void setPrimeiroEventoSelecionado(Evento primeiroEventoSelecionado) {
        this.primeiroObjectSelecionado = primeiroEventoSelecionado;
    }

    public void setPrimeiroEventoChecavel(boolean primeiroEventoChecavel) {
        this.primeiroEventoChecavel = primeiroEventoChecavel;
    }

    //Private methods ----------------------------------------------------------------------------------
//Level 1 ------------------------------------------------------------------------------------------
    private void configuraActionMode(View viewCriada, Evento evento) {
        super.configuraActionMode(viewCriada, evento, R.id.item_evento_checkbox,
                new ConfiguraActionModeListener() {
                    @Override
                    public void setMethodsModoSelecao(CheckBox checkBox) {
                        configuraComportamentoCheckBoxModoSelecao(evento, checkBox);
                    }

                    @Override
                    public void setMethodsActionMode(CheckBox checkBox) {
                        configuraCheckPrimeiroEventoSelecionado(evento, checkBox, () -> {
                            verificaSeOpcaoEditarSeraHabilitada();
                            verificaSeOpcaoArquivarDesarquivarSeraHabilitada();
                        });
                    }

                    @Override
                    public void aposSetarComportamentos(CheckBox checkBox) {
                        configuraCheckedChange(evento, checkBox);
                        configuraCheckBoxSelecionarTodos();
                    }
                });
    }

    private void vincula(View view, Evento evento) {
        configuraTextViewData(view, evento);
        configuraTextViewHorario(view, evento);
        configuraTextViewStatus(view, evento);
        configuraImageViewArquivado(view, evento);
        vinculaReferencia(view, evento);
    }

//Level 2 ------------------------------------------------------------------------------------------
    private void configuraComportamentoCheckBoxModoSelecao(Evento evento, CheckBox checkBox) {
        checkBox.setVisibility(View.VISIBLE);
        checkBox.setEnabled(evento.isAtivo());
    }

    private void configuraCheckPrimeiroEventoSelecionado(
            Evento evento,
            CheckBox checkBox,
            ConfiguraCheckPrimeiroEventoSelecionadoListener listener
    ) {
        if (evento == primeiroObjectSelecionado && primeiroEventoChecavel) {
            checkBox.setChecked(true);
            primeiroEventoChecavel = false;
            if (!objectsSelecionados.contains(evento)) {
                objectsSelecionados.add(evento);
                listener.aposChecarPrimeiroEventoSelecionado();
            }
        }
    }

    private void configuraCheckedChange(Evento evento, CheckBox checkBox) {
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!objectsSelecionados.contains(evento)) {
                    objectsSelecionados.add(evento);
                }
            } else {
                objectsSelecionados.remove(evento);
                checkBoxSelecionarTodos.setChecked(false);
            }
            if (!modoSelecao) {
                configuraChoiceModeOptions();
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

    private void configuraTextViewData(View view, Evento evento) {
        TextView data = view.findViewById(R.id.item_evento_data);
        data.setText(evento.getData());
    }

    private void configuraTextViewHorario(View view, Evento evento) {
        TextView horario = view.findViewById(R.id.item_evento_horario);
        horario.setText(evento.getHorario());
    }

    private void configuraTextViewStatus(View view, Evento evento) {
        TextView status = view.findViewById(R.id.item_evento_status_do_evento);
        String statusText;
        if (evento.isAtivo()) {
            statusText = "Ativo";
            status.setTextColor(Color.GREEN);
        } else {
            statusText = "Finalizado";
            status.setTextColor(Color.RED);
        }
        status.setText(statusText);
    }

    private void configuraImageViewArquivado(View view, Evento evento) {
        ImageView arquivado = view.findViewById(R.id.item_evento_arquivado);
        if (evento.isArquivado()) {
            arquivado.setVisibility(View.VISIBLE);
        } else {
            arquivado.setVisibility(View.GONE);
        }
    }

//Level 3 ------------------------------------------------------------------------------------------
    private void verificaSeOpcaoEditarSeraHabilitada() {
        Evento evento = (Evento) objectsSelecionados.get(0);
        if (evento.isAtivo()) {
            opcaoEditar.setIcon(R.drawable.ic_action_edit);
        } else {
            opcaoEditar.setIcon(R.drawable.ic_action_no_edit);
        }
        opcaoEditar.setEnabled(evento.isAtivo());
    }

    private void vinculaReferencia(View view, Evento evento) {
        TextView referencia = view.findViewById(R.id.item_evento_referencia_do_evento);
        TextView referenciaTitulo = view.findViewById(R.id.item_evento_titulo_referencia_do_evento);

        if (!fut.isMensal()) {
            referencia.setVisibility(View.GONE);
            referenciaTitulo.setVisibility(View.GONE);
        } else {
            String referenciaFormatada = String.valueOf(evento.getMensalId());
            String[] array = referenciaFormatada.split("");
            referenciaFormatada = array[0] + "/" + array[1] + array[2] + array[3] + array[4];
            referencia.setText(referenciaFormatada);
        }
    }

//Level 4 ------------------------------------------------------------------------------------------
    private void configuraChoiceModeOptions() {
        String title = objectsSelecionados.size() + " evento(s) selecionado(s)";
        mode.setTitle(title);
        opcaoEditar.setVisible(objectsSelecionados.size() == 1);
        if (objectsSelecionados.size() == 1) {
            verificaSeOpcaoEditarSeraHabilitada();
        }
        verificaSeOpcaoArquivarDesarquivarSeraHabilitada();
        opcaoRemover.setVisible(objectsSelecionados.size() > 0);
    }

    private void verificaSeOpcaoArquivarDesarquivarSeraHabilitada() {
        opcaoArquivar.setEnabled(objectsSelecionados.size() > 0);
        opcaoArquivar.setVisible(objectsSelecionados.size() > 0);
        opcaoDesarquivar.setVisible(objectsSelecionados.size() > 0);
        opcaoFinalizar.setVisible(objectsSelecionados.size() > 0);
        opcaoFinalizarEArquivar.setVisible(objectsSelecionados.size() > 0);
        opcaoArquivar.setIcon(R.drawable.ic_action_archive);

        for (Evento e: getEventosSelecionados()) {
            if (e.isAtivo()) {
                opcaoArquivar.setEnabled(false);
                opcaoArquivar.setIcon(R.drawable.ic_action_archive2);
            }
            if (e.isArquivado()) {
                opcaoArquivar.setVisible(false);
                opcaoFinalizarEArquivar.setVisible(false);
            }
            if (!e.isArquivado()) {
                opcaoDesarquivar.setVisible(false);
            }
            if (!e.isAtivo()) {
                opcaoFinalizar.setVisible(false);
                opcaoFinalizarEArquivar.setVisible(false);
            }
        }
    }

//Obrigatórios -------------------------------------------------------------------------------------
    public List<Evento> getEventosSelecionados() {
        List<Evento> listObjectToEvento = new ArrayList<>();
        for (Object object: objectsSelecionados) {
            listObjectToEvento.add((Evento) object);
        }
        return listObjectToEvento;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Evento getItem(int position) {
        return (Evento) objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        Evento evento = (Evento) objects.get(position);
        return evento.getEventoId();
    }

//Interfaces
    public interface ConfiguraCheckPrimeiroEventoSelecionadoListener {
        void aposChecarPrimeiroEventoSelecionado();
    }
}
