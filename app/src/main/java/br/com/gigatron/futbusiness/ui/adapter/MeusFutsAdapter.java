package br.com.gigatron.futbusiness.ui.adapter;

import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.gigatron.futbusiness.R;
import br.com.gigatron.futbusiness.model.Fut;
import br.com.gigatron.futbusiness.ui.adapter.meufut.baseadapter.MeuFutAdapter;

public class MeusFutsAdapter extends MeuFutAdapter {

    private MenuItem opcaoEditar;
    private Fut futOrigemDosJogadoresCopiados;
    private final CheckBox checkBoxSelecionarTodos;
    private final List<CheckBox> checkboxes = new ArrayList<>();
    private boolean primeiroFutChecavel;

    public MeusFutsAdapter(
            Context context,
            boolean modoCopiaJogadoresParaFut,
            CheckBox checkBoxSelecionarTodos
    ) {
        super(context, modoCopiaJogadoresParaFut);
        this.checkBoxSelecionarTodos = checkBoxSelecionarTodos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View viewCriada = createView(parent, R.layout.item_fut);
        Fut fut = (Fut) objects.get(position);
        configuraActionMode(viewCriada, fut);
        vincula(viewCriada, fut);
        return viewCriada;
    }

//Public methods -----------------------------------------------------------------------------------
//Level 1 ------------------------------------------------------------------------------------------
    public void atualiza(List<Fut> futs) {
        this.objects.clear();
        this.objects.addAll(futs);
        notifyDataSetChanged();
    }

//Level 2 ------------------------------------------------------------------------------------------
    public List<Fut> getFutsSelecionados() {
        List<Fut> listObjectToListFut = new ArrayList<>();
        for (Object fut: objectsSelecionados) {
            listObjectToListFut.add((Fut) fut);
        }
        return listObjectToListFut;
    }
    public void setOpcaoEditar(MenuItem opcaoEditar) {
        this.opcaoEditar = opcaoEditar;
    }

    public void setFutOrigemDosJogadoresCopiados(Fut futOrigemDosJogadoresCopiados) {
        this.futOrigemDosJogadoresCopiados = futOrigemDosJogadoresCopiados;
    }

    public void setPrimeiroFutChecavel(boolean primeiroFutChecavel) {
        this.primeiroFutChecavel = primeiroFutChecavel;
    }

//Private methods ----------------------------------------------------------------------------------
//Level 1 ------------------------------------------------------------------------------------------
    private void vincula(View view, Fut fut) {
        configuraTextViewLocal(view, fut);
        configuraTextViewAluguel(view, fut);
        configuraTextViewTipo(view, fut);
    }

    private void configuraActionMode(View viewCriada, Fut fut) {
        super.configuraActionMode(viewCriada, fut, R.id.item_fut_checkbox,
                new ConfiguraActionModeListener() {
                    @Override
                    public void setMethodsModoSelecao(CheckBox checkBox) {
                        configuraComportamentoCheckBoxModoSelecao(fut, checkBox);
                    }

                    @Override
                    public void setMethodsActionMode(CheckBox checkBox) {
                        configuraPrimeiroFutSelecionado(fut, checkBox);
                    }

                    @Override
                    public void aposSetarComportamentos(CheckBox checkBox) {
                        configuraCheckedChange(fut, checkBox);
                        configuraCheckBoxSelecionarTodos();
                    }
                });
    }

//Level 2 ------------------------------------------------------------------------------------------
    private void configuraTextViewLocal(View view, Fut fut) {
        TextView local = view.findViewById(R.id.item_fut_local);
        local.setText(fut.getLocal());
    }

    private void configuraTextViewAluguel(View view, Fut fut) {
        TextView aluguel = view.findViewById(R.id.item_fut_aluguel_quadra);
        String textAlguel = "Aluguel: R$ " +
                String.valueOf(fut.getAluguelDaQuadra()).replace(".", ",") + "0";
        aluguel.setText(textAlguel);
    }

    private void configuraTextViewTipo(View view, Fut fut) {
        TextView tipo = view.findViewById(R.id.item_fut_tipo);
        String textTipo;
        if (fut.isMensal()) {
            textTipo = "Mensal";
        } else {
            textTipo = "Avulso";
        }
        tipo.setText(textTipo);
    }

    private void configuraComportamentoCheckBoxModoSelecao(Fut fut, CheckBox checkBox) {
        checkBox.setVisibility(View.VISIBLE);
//        checkBox.setChecked(fut.equals(futOrigemDosJogadoresCopiados));
        checkBox.setEnabled(!fut.equals(futOrigemDosJogadoresCopiados));
    }

    private void configuraPrimeiroFutSelecionado(Fut fut, CheckBox checkBox) {
        if (fut == primeiroObjectSelecionado && primeiroFutChecavel) {
            checkBox.setChecked(true);
            primeiroFutChecavel = false;
            if (!objectsSelecionados.contains(fut)) {
                objectsSelecionados.add(fut);
            }
        }
    }

    private void configuraCheckedChange(Fut fut, CheckBox checkBox) {
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!objectsSelecionados.contains(fut)) {
                    objectsSelecionados.add(fut);
                }
            } else {
                objectsSelecionados.remove(fut);
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


//Level 3 ------------------------------------------------------------------------------------------
    private void configuraChoiceModeOptions() {
        mode.setTitle(objectsSelecionados.size() + " fut(s) selecionado(s)");
        opcaoEditar.setVisible(objectsSelecionados.size() == 1);
        opcaoRemover.setVisible(objectsSelecionados.size() > 0);
    }

//Obrigatórios -------------------------------------------------------------------------------------
    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Fut getItem(int position) {
        return (Fut) objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        Fut fut = (Fut) objects.get(position);
        return fut.getFutId();
    }
}
