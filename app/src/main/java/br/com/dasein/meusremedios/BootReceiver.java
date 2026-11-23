package br.com.dasein.meusremedios;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** Reagenda os alarmes quando o celular reinicia, o app é atualizado ou a hora muda. */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context ctx, Intent intent) {
        AlarmScheduler.scheduleAll(ctx);
    }
}
