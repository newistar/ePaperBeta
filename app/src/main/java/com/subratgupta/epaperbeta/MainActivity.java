package com.subratgupta.epaperbeta;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "ItemViewActivity";
    private String date;
    private ImageView mPageView;
    private String page = "01";
    private Boolean[] download = new Boolean[5];
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    Boolean READ = false, WRITE = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        Glide.with(getApplicationContext()).load("https://epaper.amarujala.com/2018/10/05/dl/01/hdimage.jpg").into((ImageView) findViewById(R.id.e_paper));
        Glide.with(getApplicationContext()).load("https://epaper.amarujala.com/2018/10/04/dl/01/hdimage.jpg").into((ImageView) findViewById(R.id.d_paper));
        Glide.with(getApplicationContext()).load("https://epaper.amarujala.com/2018/10/03/dl/01/hdimage.jpg").into((ImageView) findViewById(R.id.c_paper));
        Glide.with(getApplicationContext()).load("https://epaper.amarujala.com/2018/10/02/dl/01/hdimage.jpg").into((ImageView) findViewById(R.id.b_paper));
        Glide.with(getApplicationContext()).load("https://epaper.amarujala.com/2018/10/01/dl/01/hdimage.jpg").into((ImageView) findViewById(R.id.a_paper));
    }

    public void onClick(View view){
        switch (view.getId()) {
            case R.id.e_paper:
            case R.id.e_button:
                date = "05";
                view();
                break;
            case R.id.d_paper:
            case R.id.d_button:
                date = "04";
                view();
                break;
            case R.id.c_paper:
            case R.id.c_button:
                date = "03";
                view();
                break;
            case R.id.b_paper:
            case R.id.b_button:
                date = "02";
                view();
                break;
            case R.id.a_paper:
            case R.id.a_button:
                date = "01";
                view();
                break;
            case R.id.e_download:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Log.v(TAG,"Permission is granted");
                        try {
                            date = "05";
                            new DownloadFile().execute("https://firebasestorage.googleapis.com/v0/b/epaperdemo-3fc5c.appspot.com/o/hdimage5.pdf?alt=media&token=4398b877-35df-40e0-9ef7-ff48f79b1e54", "05",".pdf");
                            Toast.makeText(getApplicationContext(), "Paper is Downloading...\nIt may take few minutes.", Toast.LENGTH_LONG).show();
//                    download[4] = true;
                            sharedPref("D05","true",WRITE);
                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(),"Sorry, Unable to download.",Toast.LENGTH_SHORT).show();
                            Log.e(TAG,e.getMessage());
                        }
                    } else {
                        Log.v(TAG,"Permission is revoked");
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                }
                break;
            case R.id.d_download:
                Toast.makeText(getApplicationContext(), "This is Demo App, Only one paper available (05 Oct 2018).", Toast.LENGTH_LONG).show();
                break;
            case R.id.c_download:
                Toast.makeText(getApplicationContext(), "This is Demo App, Only one paper available (05 Oct 2018).", Toast.LENGTH_LONG).show();
                break;
            case R.id.b_download:
                Toast.makeText(getApplicationContext(), "This is Demo App, Only one paper available (05 Oct 2018).", Toast.LENGTH_LONG).show();
                break;
            case R.id.a_download:
                Toast.makeText(getApplicationContext(), "This is Demo App, Only one paper available (05 Oct 2018).", Toast.LENGTH_LONG).show();
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
            onClick(findViewById(R.id.e_download));
        }
    }

    private void view(){

        if (sharedPref("D"+date,"default",READ).equals("true")){
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/.ePB/"+date+".pdf");
            Intent target = new Intent(Intent.ACTION_VIEW);
            target.setDataAndType(Uri.fromFile(file),"application/pdf");
            target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            Intent intent = Intent.createChooser(target, "Open File");
            try {
                startActivity(intent);
            } catch (Exception e) {
                // Instruct the user to install a PDF reader here, or something
                Log.e("MyErrorCode001",e.getMessage());
            }
        }else {
            findViewById(R.id.list_item).setVisibility(View.GONE);
            findViewById(R.id.read_mode).setVisibility(View.VISIBLE);
            mPageView = findViewById(R.id.page_view);
            loadPaper(date, page);
            getSupportActionBar().hide();
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

    }

    private void loadPaper(String date, String page) {
        Glide.with(getApplicationContext()).load("https://epaper.amarujala.com/2018/10/"+date+"/dl/"+page+"/hdimage.jpg").into(mPageView);
    }

    public void changePage(View view){
        if (view.getId() == R.id.previous && Integer.parseInt(page)>1){
            page = String.format("%02d", Integer.parseInt(page)-1);
        }else if (view.getId() == R.id.next && Integer.parseInt(page)<=25){
            page = String.format("%02d", Integer.parseInt(page)+1);

        }
        loadPaper(date,page);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.check_for_update:
                Toast.makeText(getApplicationContext(), "You are using Latest Version.", Toast.LENGTH_LONG).show();
                return true;
            case R.id.about:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class DownloadFile extends AsyncTask<String, Void, Void> {


        @Override
        protected Void doInBackground(String... strings) {
//            download = new Boolean[5];
            sharedPref("D"+Integer.parseInt(strings[1]),"true",WRITE);
            String fileUrl = strings[0];   // -> http://maven.apache.org/maven-1.x/maven.pdf
            String fileName = strings[1]+strings[2];  // -> maven.pdf
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, ".ePB");
            folder.mkdir();

            File pdfFile = new File(folder, fileName);

            try {
                pdfFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileDownloader.downloadFile(fileUrl, pdfFile);
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        if ((findViewById(R.id.read_mode)).getVisibility() == View.VISIBLE){
            findViewById(R.id.list_item).setVisibility(View.VISIBLE);
            findViewById(R.id.read_mode).setVisibility(View.GONE);
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getSupportActionBar().show();
        } else {
            super.onBackPressed();
        }

    }

    private String sharedPref(String key, String value, Boolean mode){
        if (mode){
            editor.putString(key, value);
            editor.commit();
            return "Success";
        } else {
            String result = sharedPref.getString(key, value);
            return result;
        }

    }
}
