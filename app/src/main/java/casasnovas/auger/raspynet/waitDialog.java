package casasnovas.auger.raspynet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;


public class waitDialog extends DialogFragment {
    static waitDialog newInstance (){
        waitDialog nrd = new waitDialog();
        Bundle b = new Bundle();
        b.putString("error", "Sync in progress........");
        nrd.setArguments(b);
        return nrd;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new ProgressDialog.Builder(getActivity())
                .setMessage("sfdlaskjf")
                .create();
    }
}
