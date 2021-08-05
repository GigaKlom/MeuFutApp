package br.com.gigatron.futbusiness.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.Calendar;

import br.com.gigatron.futbusiness.R;
import br.com.gigatron.futbusiness.asynctask.AlteraSaldoTask;
import br.com.gigatron.futbusiness.asynctask.evento.AlteraSaldoNovoEventoTask;
import br.com.gigatron.futbusiness.asynctask.evento.CriaEventosNoMesTask;
import br.com.gigatron.futbusiness.database.dao.EventoDao;
import br.com.gigatron.futbusiness.database.dao.UsuarioDao;
import br.com.gigatron.futbusiness.model.Fut;
import br.com.gigatron.futbusiness.model.Usuario;
import br.com.gigatron.futbusiness.ui.view.MeusEventosView;

import static java.util.Calendar.YEAR;

public class CriaEventoMensalDialog extends AppCompatDialogFragment {


    private final Context context;
    private final MeusEventosView meusEventosView;
    private final EventoDao eventoDao;
    private final Fut fut;
    private RadioGroup meses;
    private final boolean mostrarArquivados;
    private final Usuario usuario;
    private final UsuarioDao usuarioDao;

    public CriaEventoMensalDialog(Fut fut,
                                  EventoDao eventoDao,
                                  Context context,
                                  MeusEventosView meusEventosView,
                                  boolean mostrarArquivados,
                                  Usuario usuario,
                                  UsuarioDao usuarioDao
    ) {
        this.fut = fut;
        this.eventoDao = eventoDao;
        this.context = context;
        this.meusEventosView = meusEventosView;
        this.mostrarArquivados = mostrarArquivados;
        this.usuario = usuario;
        this.usuarioDao = usuarioDao;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_cria_evento_mensal_dialog, null);
        configuraViewDialog(builder, view);
        inicializaCampos(view);
        return builder.create();
    }

//Level 1 ------------------------------------------------------------------------------------------
    private void configuraViewDialog(AlertDialog.Builder builder, View view) {
        builder.setView(view)
                .setTitle("Escolha o mês do mensal")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Ok", (dialog, which) -> {
                    if (meses.getCheckedRadioButtonId() == -1) {
                        alertaErro("Nenhum mês foi selecionado!");
                    } else {
                        criaEventosNoMes();
                    }
                    meusEventosView.atualiza(mostrarArquivados);
                });
    }

    private void criaEventosNoMes() {
        int mes = getMes();
        int ano = Calendar.getInstance().get(YEAR);
        int diaDaSemana = fut.getDiaDaSemana();

        new CriaEventosNoMesTask(mes, ano, diaDaSemana, eventoDao, fut,
                sucesso -> {
                    if (!sucesso) {
                        alertaErro("Ja existem eventos no mês solicitado!");
                    } else {
                        diminuiSaldoNovoEvento();
                        feedbackAoUser("Eventos do mês foram criados!");
                    }
                    meusEventosView.atualiza(mostrarArquivados);
                }).execute();
    }

    private void inicializaCampos(View view) {
        meses = view.findViewById(R.id.fragment_cria_evento_mensal_radiogroup_meses);
    }

//Level 2 ------------------------------------------------------------------------------------------
    private int getMes() {
        int mes = 0;
        if (meses.getCheckedRadioButtonId() ==
                R.id.fragment_cria_evento_mensal_radiobutton_fevereiro) {
            mes = 1;

        } else if (meses.getCheckedRadioButtonId() ==
                R.id.fragment_cria_evento_mensal_radiobutton_marco) {
            mes = 2;

        } else if (meses.getCheckedRadioButtonId() ==
                R.id.fragment_cria_evento_mensal_radiobutton_abril) {
            mes = 3;

        } else  if (meses.getCheckedRadioButtonId() ==
                R.id.fragment_cria_evento_mensal_radiobutton_maio) {
            mes = 4;

        } else  if (meses.getCheckedRadioButtonId() ==
                R.id.fragment_cria_evento_mensal_radiobutton_junho) {
            mes = 5;

        } else  if (meses.getCheckedRadioButtonId() ==
                R.id.fragment_cria_evento_mensal_radiobutton_julho) {
            mes = 6;

        } else  if (meses.getCheckedRadioButtonId() ==
                R.id.fragment_cria_evento_mensal_radiobutton_agosto) {
            mes = 7;

        } else  if (meses.getCheckedRadioButtonId() ==
                R.id.fragment_cria_evento_mensal_radiobutton_setembro) {
            mes = 8;

        } else  if (meses.getCheckedRadioButtonId() ==
                R.id.fragment_cria_evento_mensal_radiobutton_outubro) {
            mes = 9;

        } else  if (meses.getCheckedRadioButtonId() ==
                R.id.fragment_cria_evento_mensal_radiobutton_novembro) {
            mes = 10;

        } else  if (meses.getCheckedRadioButtonId() ==
                R.id.fragment_cria_evento_mensal_radiobutton_dezembro) {
            mes = 11;

        }
        return mes;
    }

    private void alertaErro (String message) {
        new AlertDialog.Builder(context)
                .setTitle("Erro")
                .setMessage(message)
                .setNeutralButton("Ok", null)
                .show();
    }

    private void diminuiSaldoNovoEvento() {
        new AlteraSaldoNovoEventoTask(
                usuario,
                usuarioDao,
                AlteraSaldoTask.Acao.DIMINUI
        ).execute();
    }

    private void feedbackAoUser(String text) {
        new Toast(context);
        Toast.makeText(context, text, Toast.LENGTH_SHORT)
                .show();
    }
}
