package casasnovas.auger.raspynet;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FTPActivity extends AppCompatActivity {

    FTPFile[] filesystem;
    String location, itemSelectedName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftp);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton b = (ImageButton) findViewById(R.id.bFTP);
        b.setEnabled(true);

        ImageButton b1 = (ImageButton) findViewById(R.id.bFTPupload);
        b1.setEnabled(true);

        b1 = (ImageButton) findViewById(R.id.bDelete);
        b1.setEnabled(true);

        b1 = (ImageButton) findViewById(R.id.bRefresh);
        b1.setEnabled(true);

        refresh();
        location = "dir";
        itemSelectedName = null;
    }
    public void button (View v){
        final backgroundFTP bf;
        bf = new backgroundFTP();
        switch (v.getId()){
            case R.id.bFTP:
                bf.execute("download");
                ImageButton b = (ImageButton) findViewById(R.id.bFTP);
                b.setEnabled(false);
                break;
            case R.id.bFTPupload:
                fileChooser fc = new fileChooser(this);
                fc.setFileListener(new fileChooser.FileSelectedListener() {
                    @Override
                    public void fileSelected(File file) {
                        Log.d("raspynet", "File to upload: " + file.getPath());
                        bf.execute("upload", file.getPath(), file.getName());
                        ImageButton b1 = (ImageButton) findViewById(R.id.bFTPupload);
                        b1.setEnabled(false);

                    }
                }).showDialog();
                break;
            case R.id.bRefresh:
                //Refresh function
                refresh();
                if (filesystem != null) {
                    final ListView listView = (ListView) findViewById(R.id.lvLS);
                    String array[] = new String[filesystem.length+1];
                    array[0] = "/"+location;
                    for (int i = 0; i < filesystem.length; ++i) array[i+1] = filesystem[i].getName();
                    listView.setAdapter(new ArrayAdapter<>(getApplication().getApplicationContext(), R.layout.listviewitem, R.id.listviewitemtext, array));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        /*TODO: para hacer que se mantenga seleccionado un item, hay que crear un layout nuevo*/
                            if (position > 0) {
                                TextView selectedItem = (TextView) findViewById(R.id.tvSelectedItem);
                                selectedItem.setText(filesystem[position - 1].getName());
                                itemSelectedName = filesystem[position - 1].getName();
                            }
                        }
                    });
                }
                break;
            case R.id.bDelete:
                bf.execute("delete");
        }

    }

    private void refresh (){
        backgroundFTP bf;
        bf = new backgroundFTP();
        bf.execute("ls");
        try {
            bf.get(1000, TimeUnit.MILLISECONDS);
            Log.d("raspynet", "SIZE: " + filesystem.length);
        } catch (InterruptedException e) {
            Log.d("raspynet", "InterruptedEXC: " + e.getMessage());
        } catch (ExecutionException e) {
            Log.d("raspynet", "ExecutionEXC: " + e.getMessage());
        } catch (TimeoutException e) {
            Log.d("raspynet", "TimeoutEXC: " + e.getMessage());
        }
    }
    private class backgroundFTP extends AsyncTask <String, Void, Boolean>{
        String param;
        Boolean success;
        @Override
        protected Boolean doInBackground(String... params) {
            param = params[0];
            success = false;
            switch (params[0]) {
                case "download":
                    try {
                        if (itemSelectedName != null) {
                            success = serverInterface.downloadFTP("anonymous", "mailinventat", location + "/" + itemSelectedName, Environment.getExternalStorageDirectory().getPath() + "/download/"+itemSelectedName);
                        }

                    } catch (IOException e) {
                        Log.d("raspynet", "Exception: " + e.getMessage());
                    }
                    break;
                case "upload":
                    try {
                        success = serverInterface.uploadFTP("anonymous", "mailinventat", params[1], "dir/" + params[2]);
                    } catch (IOException e) {
                        Log.d("raspynet", "Exception: " + e.getMessage());
                    }
                    break;
                case "ls":
                    try {
                        filesystem = serverInterface.lsFTP(location, "anonymous", "patata");
                    } catch (IOException e) {
                        Log.d("raspynet", "Exception: " + e.getMessage());
                    }
                    break;
                case "delete":
                    try {
                        if (itemSelectedName != null) {
                            /*TODO: crear un dialog de "estas seguro de querer eliminar?" */
                            success = serverInterface.deletefile(location + "/" + itemSelectedName, "anonymous", "patata");
                        }
                    } catch (IOException e) {
                        Log.d("raspynet", "Exception: " +e.getMessage());
                    }
                    break;
                case "mkdir":
                    try {
                        success = serverInterface.mkdir(location, "anonymous", "patata");
                    } catch (IOException e) {
                        Log.d("raspynet", "Exception: " + e.getMessage());
                    }
                    break;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            ImageButton b = (ImageButton) findViewById(R.id.bFTP);
            b.setEnabled(true);
            b = (ImageButton) findViewById(R.id.bFTPupload);
            b.setEnabled(true);

            if (success) Toast.makeText(getApplicationContext(), "Acció realitzada amb éxit", Toast.LENGTH_SHORT).show();
            else Toast.makeText(getApplicationContext(), "Wops, hi ha hagut un error", Toast.LENGTH_SHORT).show();
        }
    }


}
