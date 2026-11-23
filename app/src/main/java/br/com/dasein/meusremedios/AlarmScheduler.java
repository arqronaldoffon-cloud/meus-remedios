package br.com.dasein.meusremedios;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Guarda a lista de doses enviada pela tela do app e agenda um alarme
 * do sistema para a próxima vez de cada dose (toca mesmo com o celular bloqueado).
 */
public final class AlarmScheduler {

    private static final String PREFS = "meus_remedios";
    private static final String KEY_DOSES = "doses";
    private static final String KEY_COUNT = "alarm_count";

    private AlarmScheduler() {}

    public static void saveAndSchedule(Context ctx, String json) {
        prefs(ctx).edit().putString(KEY_DOSES, json == null ? "[]" : json).apply();
        scheduleAll(ctx);
    }

    public static void scheduleAll(Context ctx) {
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;
        SharedPreferences sp = prefs(ctx);

        // Cancela os alarmes anteriores
        int old = sp.getInt(KEY_COUNT, 0);
        for (int i = 0; i < old; i++) {
            am.cancel(alarmIntent(ctx, i, null));
        }

        JSONArray doses;
        try {
            doses = new JSONArray(sp.getString(KEY_DOSES, "[]"));
        } catch (Exception e) {
            doses = new JSONArray();
        }

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        long now = System.currentTimeMillis();
        int count = 0;

        for (int i = 0; i < doses.length(); i++) {
            JSONObject d = doses.optJSONObject(i);
            if (d == null) continue;
            String time = d.optString("time", "");
            String[] p = time.split(":");
            if (p.length != 2) continue;
            int h, m;
            try {
                h = Integer.parseInt(p[0]);
                m = Integer.parseInt(p[1]);
            } catch (NumberFormatException e) {
                continue;
            }

            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, h);
            c.set(Calendar.MINUTE, m);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            boolean takenToday = today.equals(d.optString("takenDate", ""));
            if (c.getTimeInMillis() <= now + 1000 || takenToday) {
                c.add(Calendar.DAY_OF_YEAR, 1);
            }

            PendingIntent pi = alarmIntent(ctx, count, d);
            long when = c.getTimeInMillis();
            if (canExact(am)) {
                PendingIntent show = PendingIntent.getActivity(ctx, 9000 + count,
                        new Intent(ctx, MainActivity.class),
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                am.setAlarmClock(new AlarmManager.AlarmClockInfo(when, show), pi);
            } else {
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, when, pi);
            }
            count++;
        }
        sp.edit().putInt(KEY_COUNT, count).apply();
    }

    static boolean canExact(AlarmManager am) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return am.canScheduleExactAlarms();
        }
        return true;
    }

    private static PendingIntent alarmIntent(Context ctx, int requestCode, JSONObject dose) {
        Intent i = new Intent(ctx, AlarmReceiver.class);
        i.setAction("br.com.dasein.meusremedios.ALARME");
        if (dose != null) {
            i.putExtra("name", dose.optString("name", "Remédio"));
            i.putExtra("dose", dose.optString("dose", ""));
            i.putExtra("note", dose.optString("note", ""));
            i.putExtra("time", dose.optString("time", ""));
        }
        return PendingIntent.getBroadcast(ctx, requestCode, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private static SharedPreferences prefs(Context ctx) {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }
}
