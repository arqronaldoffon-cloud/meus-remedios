package br.com.dasein.meusremedios;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;

/** Dispara quando chega a hora do remédio: mostra o alerta com som de alarme e vibração. */
public class AlarmReceiver extends BroadcastReceiver {

    static final String CHANNEL_ID = "alarme_remedio_v1";

    @Override
    public void onReceive(Context ctx, Intent intent) {
        String name = intent.getStringExtra("name");
        String dose = intent.getStringExtra("dose");
        String note = intent.getStringExtra("note");
        String time = intent.getStringExtra("time");
        if (name == null) name = "Remédio";

        ensureChannel(ctx);

        Intent open = new Intent(ctx, MainActivity.class);
        open.putExtra("fromAlarm", true);
        open.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(ctx, 7000, open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        StringBuilder text = new StringBuilder();
        if (dose != null && !dose.isEmpty()) text.append(dose);
        if (time != null && !time.isEmpty()) text.append(text.length() > 0 ? " • " : "").append(time);
        if (note != null && !note.isEmpty()) text.append("\n").append(note);
        text.append("\nToque aqui e depois aperte \"JÁ TOMEI\".");

        Notification.Builder b = new Notification.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notif)
                .setContentTitle("⏰ Hora do remédio: " + name)
                .setContentText(text.toString())
                .setStyle(new Notification.BigTextStyle().bigText(text.toString()))
                .setCategory(Notification.CATEGORY_ALARM)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setContentIntent(pi)
                .setFullScreenIntent(pi, true)
                .setAutoCancel(true);

        Notification n = b.build();
        n.flags |= Notification.FLAG_INSISTENT; // repete o som até a pessoa tocar

        NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            int id = (name + time).hashCode();
            try {
                nm.notify(id, n);
            } catch (SecurityException ignored) {
                // Sem permissão de notificação: nada a fazer aqui
            }
        }

        // Agenda o próximo dia
        AlarmScheduler.scheduleAll(ctx);
    }

    static void ensureChannel(Context ctx) {
        NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null || nm.getNotificationChannel(CHANNEL_ID) != null) return;
        NotificationChannel ch = new NotificationChannel(CHANNEL_ID,
                "Alarme dos remédios", NotificationManager.IMPORTANCE_HIGH);
        ch.setDescription("Avisa na hora de tomar cada remédio");
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (sound == null) sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        ch.setSound(sound, new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build());
        ch.enableVibration(true);
        ch.setVibrationPattern(new long[]{0, 800, 400, 800, 400, 800});
        ch.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        ch.setBypassDnd(true);
        nm.createNotificationChannel(ch);
    }
}
