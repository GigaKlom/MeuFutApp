package br.com.gigatron.futbusiness.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.gigatron.futbusiness.R;
import br.com.gigatron.futbusiness.model.Jogador;
import br.com.gigatron.futbusiness.model.JogadorEvento;
import br.com.gigatron.futbusiness.ui.adapter.meufut.baseadapter.MeuFutAdapter;

public class PaginaEventoAdapter extends MeuFutAdapter {

    private MenuItem opcaoMarcarFaltante;
    private final CheckBox checkBoxSelecionarTodos;
    private final List<CheckBox> checkboxes = new ArrayList<>();
    private boolean primeiroJogadorChecavel;

    public PaginaEventoAdapter(Context context, CheckBox checkBoxSelecionarTodos) {
        super(context, false);
        this.checkBoxSelecionarTodos = checkBoxSelecionarTodos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View viewCriada = createView(parent, R.layout.item_jogadorevento);
        JogadorEvento jogadorEvento = (JogadorEvento) objects.get(position);
        vincula(viewCriada, jogadorEvento);
        configuraActionMode(viewCriada, jogadorEvento);
        return viewCriada;
    }


//Public methods -----------------------------------------------------------------------------------
//Level 1 ------------------------------------------------------------------------------------------
    public void atualiza(List<JogadorEvento> jogadoresEvento) {
        this.objects.clear();
        this.objects.addAll(jogadoresEvento);
        notifyDataSetChanged();
    }

//Level 2 ------------------------------------------------------------------------------------------
    public void setOpcaoMarcarFaltante(MenuItem opcaoMarcarFaltante) {
        this.opcaoMarcarFaltante = opcaoMarcarFaltante;
    }

    public List<JogadorEvento> getJogadoresEventoSelecionados() {
        List<JogadorEvento> listObjectToJogadorEvento = new ArrayList<>();
        for (Object object: objectsSelecionados) {
            listObjectToJogadorEvento.add((JogadorEvento) object);
        }
        return listObjectToJogadorEvento;
    }

    public void setPrimeiroJogadorSelecionado(Jogador primeiroJogadorSelecionado) {
        this.primeiroObjectSelecionado = primeiroJogadorSelecionado;
    }

    public void setPrimeiroJogadorChecavel(boolean primeiroJogadorChecavel) {
        this.primeiroJogadorChecavel = primeiroJogadorChecavel;
    }

    //Private methods ----------------------------------------------------------------------------------
//Level 1 ------------------------------------------------------------------------------------------
    private void vincula(View view, JogadorEvento jogadorEvento) {
        configuraTextViewNome(view, jogadorEvento);
        configuraTextViewStatus(view, jogadorEvento);
        configuraTextViewPago(view, jogadorEvento);
    }

    private void configuraActionMode(View viewCriada, JogadorEvento jogadorEvento) {
        super.configuraActionMode(viewCriada, jogadorEvento, R.id.item_jogadorevento_checkbox,
                new ConfiguraActionModeListener() {
                    @Override
                    public void setMethodsModoSelecao(CheckBox checkBox) {

                    }

                    @Override
                    public void setMethodsActionMode(CheckBox checkBox) {
                        configuraCheckNoPrimeiroJogadorSelecionado(jogadorEvento, checkBox);
                    }

                    @Override
                    public void aposSetarComportamentos(CheckBox checkBox) {
                        configuraCheckedChange(jogadorEvento, checkBox);
                        configuraCheckBoxSelecionarTodos();
                    }
                });
    }

//Level 2 ------------------------------------------------------------------------------------------
    private void configuraTextViewNome(View view, JogadorEvento jogadorEvento) {
        TextView nome = view.findViewById(R.id.item_jogadorevento_nome);
        nome.setText(jogadorEvento.getNome());
    }
    private void configuraTextViewStatus(View view, JogadorEvento jogadorEvento) {
        TextView status = view.findViewById(R.id.item_jogadorevento_status);
        String textStatus;
        if (jogadorEvento.isMensalista()) {
            textStatus = "Mensalista - R$ " +
                    String.valueOf(jogadorEvento.getValorMensal()).replace(".",",") + "0";
        } else {
            textStatus = "Avulso - R$ " +
                    String.valueOf(jogadorEvento.getValorAvulso()).replace(".",",") + "0";
        }
        status.setText(textStatus);
    }

    private void configuraTextViewPago(View view, JogadorEvento jogadorEvento) {
        TextView pago = view.findViewById(R.id.item_jogadorevento_pago);
        if (!jogadorEvento.isFuro()) {
            if (jogadorEvento.isPago()) {
                pago.setText(R.string.pago);
                pago.setTextColor(Color.GREEN);
            } else {
                pago.setText(R.string.nao_pago);
                pago.setTextColor(Color.RED);
            }
        } else {
            pago.setText(R.string.furou);
            pago.setTextColor(Color.YELLOW);
        }
    }

    private void configuraCheckNoPrimeiroJogadorSelecionado(
            JogadorEvento jogadorEvento,
            CheckBox checkBox
    ) {
        if (jogadorEvento == primeiroObjectSelecionado && primeiroJogadorChecavel) {
            checkBox.setChecked(true);
            primeiroJogadorChecavel = false;
            if (!objectsSelecionados.contains(jogadorEvento)) {
                objectsSelecionados.add(jogadorEvento);
            }
        }
    }

    private void configuraCheckedChange(JogadorEvento jogadorEvento, CheckBox checkBox) {
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!objectsSelecionados.contains(jogadorEvento)) {
                    objectsSelecionados.add(jogadorEvento);
                }
            } else {
                objectsSelecionados.remove(jogadorEvento);
                checkBoxSelecionarTodos.setChecked(false);
            }
            configuraChoiceModeOptions();
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
        mode.setTitle(objectsSelecionados.size() + " jogador(es) selecionado(s)");
        opcaoRemover.setVisible(objectsSelecionados.size() > 0);
        opcaoMarcarFaltante.setVisible(objectsSelecionados.size() > 0);
    }

//Obrigatórios -------------------------------------------------------------------------------------
    @Override
    public int getCount() {
    return objects.size();
}

    @Override
    public JogadorEvento getItem(int position) {
        return (JogadorEvento) objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        JogadorEvento evento = (JogadorEvento) objects.get(position);
        return evento.getJogadorEventoId();
    }
}