package casasnovas.auger.raspynet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class correctActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correct);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView iv = (ImageView) findViewById(R.id.ivUser);
        iv.setImageResource(R.drawable.ic_no_user);

    }
    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), ":P", Toast.LENGTH_SHORT).show();
    }
    public void buttonSelection (View v) {
        switch (v.getId()){
            case R.id.bCloud:
                Intent i = new Intent(getApplicationContext(), FTPActivity.class);
                startActivity(i);
                break;
            case R.id.bSettings:
                Toast.makeText(getApplicationContext(), "No esta implementado", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bLogout:
                Intent logout = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(logout);
        }
    }

}
