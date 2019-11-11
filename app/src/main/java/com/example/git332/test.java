package com.example.git332;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import pub.devrel.easypermissions.EasyPermissions;

public class test extends AppCompatActivity {

    TableLayout tbl;
    static int flg=0;
    private static final int WRITE_REQUEST_CODE = 300;
    private static final String TAG = MainActivity.class.getSimpleName();
    Context con = this;
    ImageView img_view;
    String url_mainActivity;
    ImageButton download;
    ImageButton setwallpaper;

    private static final long GAME_LENGTH_MILLISECONDS = 30;

    private InterstitialAd interstitialAd;
    private CountDownTimer countDownTimer;
    private Button retryButton;
    private boolean gameIsInProgress;
    private long timerMilliseconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_test);

        tbl=(TableLayout)findViewById(R.id.tbl_test);
        download=(ImageButton) findViewById(R.id.down);
        setwallpaper=(ImageButton)findViewById(R.id.set);
        img_view=(ImageView)findViewById(R.id.text_id);

        url_mainActivity = getIntent().getExtras().getString("url");
        Picasso.with(getApplicationContext()).load(url_mainActivity).into(img_view);
        EasyPermissions.hasPermissions(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.ad_unit_id));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                startGame();
            }
        });
        startGame();


        img_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flg == 0) {
                    tbl.setVisibility(View.INVISIBLE);
                    flg = 1;
                }
                else {
                    tbl.setVisibility(View.VISIBLE);
                    flg = 0;
                }
            }
        });

        setwallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                new SetWallpaperTask().execute(url_mainActivity);
            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                showInterstitial();
                new DownloadFile().execute(url_mainActivity);
            }
        });
    }
    class DownloadFile extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog;
        private String fileName;
        private String folder;
        private boolean isDownloaded;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(con);
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                int lengthOfFile = connection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream());
                fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1, f_url[0].length());
                folder = Environment.getExternalStorageDirectory() + File.separator + "Download/";
                File directory = new File(folder);
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                OutputStream output = new FileOutputStream(folder + fileName);
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));

                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
                return "Downloaded at: " + folder + fileName;
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
            return "your app has not storage permission";
        }
        @Override
        protected void onProgressUpdate(String... values) {
            progressDialog.setProgress(Integer.parseInt(values[0]));
        }
        @Override
        protected void onPostExecute(String s) {
            this.progressDialog.dismiss();
            Toast.makeText(con, s, Toast.LENGTH_SHORT).show();

        }
    }
    public class SetWallpaperTask extends AsyncTask<String, Void, Bitmap> {
        private ProgressDialog progressDialog;
        @Override
        protected void onPreExecute () {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(test.this);
            this.progressDialog.setMessage("Please wait...");
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }
        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap result= null;
            try {
                result = Picasso.with(getApplicationContext())
                        .load(url_mainActivity)
                        .get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
            try {
                wallpaperManager.setBitmap(result);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return result;
        }
        protected void onPostExecute (Bitmap result) {
            super.onPostExecute(result);
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(getBaseContext());
            try {
                wallpaperManager.setBitmap(result);
                Toast.makeText(getApplicationContext(), "Set wallpaper successfully", Toast.LENGTH_SHORT).show();
                this.progressDialog.dismiss();
                download.setEnabled(true);
               showInterstitial();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    private void createTimer(final long milliseconds) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(milliseconds, 50) {
            @Override
            public void onTick(long millisUnitFinished) {
                timerMilliseconds = millisUnitFinished;
            }
            @Override
            public void onFinish() {
                gameIsInProgress = false;
             }
        };
    }
    @Override
    public void onResume() {
        super.onResume();
        if (gameIsInProgress) {
            resumeGame(timerMilliseconds);
        }
    }
    @Override
    public void onPause() {
        countDownTimer.cancel();
        super.onPause();
    }
    private void showInterstitial() {
        if (interstitialAd != null && interstitialAd.isLoaded()) {
            interstitialAd.show();
        } else {
            startGame();
        }
    }
    private void startGame() {
        if (!interstitialAd.isLoading() && !interstitialAd.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            interstitialAd.loadAd(adRequest);
        }
        resumeGame(GAME_LENGTH_MILLISECONDS);
    }
    private void resumeGame(long milliseconds) {
        gameIsInProgress = true;
        timerMilliseconds = milliseconds;
        createTimer(milliseconds);
        countDownTimer.start();
    }
}
