package br.com.gigatron.futbusiness.util;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkCheck {

    public void check(Context context, NetworkCheckListener listener) {
        if (!isConnected(context)) {
            new AlertDialog.Builder(context)
                    .setTitle("Desconectado")
                    .setMessage("Por favor, conecte-se à internet para continuar")
                    .setCancelable(false)
                    .setPositiveButton("Ok", (dialog, which) -> {
                        listener.onUserOk(isConnected(context));
                    })
                    .show();
        }
    }

    private boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return (wifiConnection != null && wifiConnection.isConnected())
                || (mobileConnection != null && mobileConnection.isConnected());
    }

    public interface NetworkCheckListener {
        void onUserOk(boolean isConnected);
    }
}
