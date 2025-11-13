package com.meuapp.webview;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import androidx.core.app.NotificationCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class WebViewService extends Service {

    public static final String ACTION_PLAY = "com.meuapp.webview.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.meuapp.webview.ACTION_PAUSE";
    public static final String ACTION_CLOSE = "com.meuapp.webview.ACTION_CLOSE";
    public static final String CHANNEL_ID = "WebViewServiceChannel";

    private WindowManager windowManager;
    private View serviceView;
    private WebView webView;
    private SwipeRefreshLayout swipeLayout;
    private ProgressBar progressBar;
    private boolean isPlaying = true;

    private final BroadcastReceiver mediaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) return;

            switch (action) {
                case ACTION_PLAY:
                    isPlaying = true;
                    webView.onResume();
                    updateNotification();
                    break;
                case ACTION_PAUSE:
                    isPlaying = false;
                    webView.onPause();
                    updateNotification();
                    break;
                case ACTION_CLOSE:
                    stopSelf();
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        serviceView = LayoutInflater.from(this).inflate(R.layout.service_webview, null);

        swipeLayout = serviceView.findViewById(R.id.swipe_container);
        webView = serviceView.findViewById(R.id.webview);
        progressBar = serviceView.findViewById(R.id.progress_bar);

        setupWebView();
        setupSwipeRefresh();

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_PLAY);
        filter.addAction(ACTION_PAUSE);
        filter.addAction(ACTION_CLOSE);
        registerReceiver(mediaReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateNotification();

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.START;

        if (serviceView.getWindowToken() == null) {
            windowManager.addView(serviceView, params);
        }
        return START_STICKY;
    }

    private void setupWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress < 100) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
                if (swipeLayout.isRefreshing()) {
                    swipeLayout.setRefreshing(false);
                }
            }
        });
        webView.loadUrl("https://x9quiz.stunnelpro.shop/");
    }

    private void setupSwipeRefresh() {
        swipeLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_purple));
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
            }
        });
    }

    private void updateNotification() {
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent closeIntent = new Intent(ACTION_CLOSE);
        PendingIntent pCloseIntent = PendingIntent.getBroadcast(this, 0, closeIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("QuiZoeira no Ar!")
                .setContentText("A rádio está tocando. Clique para voltar.")
                .setSmallIcon(R.drawable.ic_radio_white)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_close_white, "Fechar", pCloseIntent);

        if (isPlaying) {
            Intent pauseIntent = new Intent(ACTION_PAUSE);
            PendingIntent pPauseIntent = PendingIntent.getBroadcast(this, 0, pauseIntent, PendingIntent.FLAG_IMMUTABLE);
            notificationBuilder.addAction(R.drawable.ic_pause_white, "Pausar", pPauseIntent);
        } else {
            Intent playIntent = new Intent(ACTION_PLAY);
            PendingIntent pPlayIntent = PendingIntent.getBroadcast(this, 0, playIntent, PendingIntent.FLAG_IMMUTABLE);
            notificationBuilder.addAction(R.drawable.ic_play_arrow_white, "Tocar", pPlayIntent);
        }

        Notification notification = notificationBuilder.build();
        startForeground(1, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID, "Canal da Rádio QuiZoeira", NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mediaReceiver);
        if (serviceView != null && serviceView.getWindowToken() != null) {
            windowManager.removeView(serviceView);
        }
        if (webView != null) {
            webView.destroy();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
