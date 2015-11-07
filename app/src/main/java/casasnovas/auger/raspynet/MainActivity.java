package casasnovas.auger.raspynet;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        EditText et = (EditText) findViewById(R.id.etEmail);
        et.setText("provahash8@gmail.com");
        et = (EditText) findViewById(R.id.etPass);
        et.setText("12345");
//        Intent i = new Intent(getApplicationContext(), NewUserActivity.class);
//        startActivity(i);

    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void button(View v) throws ExecutionException, InterruptedException {
        backgroundLogin bl = new backgroundLogin();
        String user, pass;
        EditText et = (EditText) findViewById(R.id.etEmail);
        user = et.getText().toString();
        et = (EditText) findViewById(R.id.etPass);
        pass = et.getText().toString();
        bl.execute(user, pass);
        Button b = (Button) findViewById(R.id.button);
        b.setText(" CONNECTING... ");
        /*Snackbar.make(b, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();*/
        b.setEnabled(false);
            /*TODO: La funcion isUserFromDb deberá tambien proporcionar el nombre del usuario y tal, todo esto se le tiene que pasar al intent para que la activity que lo recibe lo pueda mostrar*/
    }

    public void ftpBypass(View v) {
        Intent i = new Intent(getApplicationContext(), FTPActivity.class);
        startActivity(i);
    }

    private class backgroundLogin extends AsyncTask<String, Void, Void> {

        int result;

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Button b = (Button) findViewById(R.id.button);
            b.setText(R.string.connect);
            b.setEnabled(true);
            if (result == 1) {
                Intent i = new Intent(getApplicationContext(), correctActivity.class);
                startActivity(i);
            } else if (result == 2) {
                notRegisteredDialog nrd = notRegisteredDialog.newInstance("backgroundLogin");
                nrd.show(getFragmentManager(), "10");
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            URLconnect(params[0], params[1]);
            return null;
        }

        private void URLconnect(String email, String pass) {
            try {
                String urlParameters = createJsonObject(pass, email);
                String bufferLectura = serverInterface.doPost("https://raspynet.herokuapp.com/login", urlParameters);
                result = isUserFromDB(bufferLectura);

            } catch (IOException e) {
                Log.d("raspynet", "Excep: " + e.getMessage());
                result = 3;
            }
        }

        private int isUserFromDB(String response) {
            //Comprueba si el string de respuesta del server es un si o un no
            if (response.contains("success")) return 1;
            else if (response.contains("Wrong")) return 2;
            else return 3;
        }

        private String createJsonObject(String pass, String mail) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("email", mail);
                jsonObject.put("password", pass);
                return jsonObject.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}