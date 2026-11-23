package br.com.dasein.meusremedios;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.webkit.JavascriptInterface;

import java.util.Locale;

/** Ponte entre a tela (HTML) e os recursos do Android: alarmes, voz, compartilhar e ajustes. */
public class WebBridge {

    private final Activity activity;
    private TextToSpeech tts;
    private boolean ttsReady = false;

    WebBridge(Activity activity) {
        this.activity = activity;
        tts = new TextToSpeech(activity.getApplicationContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(new Locale("pt", "BR"));
                tts.setSpeechRate(0.85f);
                ttsReady = true;
            }
        });
    }

    @JavascriptInterface
    public boolean isApp() {
        return true;
    }

    @JavascriptInterface
    public void scheduleDoses(String json) {
        AlarmScheduler.saveAndSchedule(activity.getApplicationContext(), json);
    }

    @JavascriptInterface
    public boolean speak(String text) {
        if (!ttsReady || text == null) return false;
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "fala");
        return true;
    }

    @JavascriptInterface
    public void stopSpeak() {
        if (tts != null) tts.stop();
    }

    @JavascriptInterface
    public void share(String text) {
        activity.runOnUiThread(() -> {
            Intent send = new Intent(Intent.ACTION_SEND);
            send.setType("text/plain");
            send.putExtra(Intent.EXTRA_TEXT, text);
            activity.startActivity(Intent.createChooser(send, "Enviar resumo"));
        });
    }

    @JavascriptInterface
    public void dial(String number) {
        activity.runOnUiThread(() -> {
            Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
            activity.startActivity(i);
        });
    }

    @JavascriptInterface
    public void openBatterySettings() {
        activity.runOnUiThread(() -> {
            try {
                activity.startActivity(new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS));
            } catch (Exception e) {
                openAppSettings();
            }
        });
    }

    @JavascriptInterface
    public void openNotificationSettings() {
        activity.runOnUiThread(() -> {
            try {
                Intent i = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                i.putExtra(Settings.EXTRA_APP_PACKAGE, activity.getPackageName());
                activity.startActivity(i);
            } catch (Exception e) {
                openAppSettings();
            }
        });
    }

    private void openAppSettings() {
        Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + activity.getPackageName()));
        activity.startActivity(i);
    }

    void shutdown() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}
