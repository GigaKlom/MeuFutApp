package br.com.gigatron.futbusiness.ui.adapter.meufut.baseadapter;

import android.content.Context;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;

import java.util.ArrayList;
import java.util.List;

public abstract class MeuFutAdapter extends BaseAdapter {

    protected final Context context;
    protected final List<Object> objects = new ArrayList<>();
    protected final List<Object> objectsSelecionados = new ArrayList<>();
    protected Object primeiroObjectSelecionado;
    protected ActionMode mode;
    protected boolean isActionMode;
    protected MenuItem opcaoRemover;
    protected final boolean modoSelecao;

    public MeuFutAdapter(Context context, boolean modoSelecao) {
        this.context = context;
        this.modoSelecao = modoSelecao;
    }

    protected void configuraActionMode(
            View viewCriada,
            Object object,
            @IdRes int idCheckBox,
            ConfiguraActionModeListener listener
    ) {
        CheckBox checkBox = viewCriada.findViewById(idCheckBox);
        checkBox.setChecked(objectsSelecionados.contains(object));
        if (modoSelecao) {
            listener.setMethodsModoSelecao(checkBox);
        } else {
            if (isActionMode) {
                checkBox.setVisibility(View.VISIBLE);
                listener.setMethodsActionMode(checkBox);
            } else {
                this.objectsSelecionados.clear();
                checkBox.setVisibility(View.GONE);
            }
        }
        listener.aposSetarComportamentos(checkBox);
    }

//Public methods -----------------------------------------------------------------------------------
    public void setActionMode(boolean isActionMode) {
        this.isActionMode = isActionMode;
    }

    public void setActionModeReference(ActionMode mode) {
        this.mode = mode;
    }

    public void setOpcaoRemover(MenuItem opcaoRemover) {
        this.opcaoRemover = opcaoRemover;
    }

    public void setPrimeiroObjectSelecionado(Object primeiroObjectSelecionado) {
        this.primeiroObjectSelecionado = primeiroObjectSelecionado;
    }

//Base adapter methods -----------------------------------------------------------------------------
    @Override//Override in children
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override//Override in children
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override//Override in children
    public long getItemId(int position) {
        return 0;
    }

    protected View createView(ViewGroup parent, @LayoutRes int resource) {
        return LayoutInflater
                .from(context)
                .inflate(resource, parent, false);
    }

//Interfaces ---------------------------------------------------------------------------------------
    protected interface ConfiguraActionModeListener {
        void setMethodsModoSelecao(CheckBox checkBox);
        void setMethodsActionMode(CheckBox checkBox);
        void aposSetarComportamentos(CheckBox checkBox);
    }
}
