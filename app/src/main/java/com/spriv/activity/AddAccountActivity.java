package com.spriv.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.spriv.R;
import com.spriv.SprivApp;
import com.spriv.data.SprivAccount;
import com.spriv.model.AccountsModel;
import com.spriv.task.UpdatePairTask;
import com.spriv.task.UpdatePairTaskHandler;
import com.spriv.utils.FontsManager;
import com.spriv.utils.MySharedPreferences;

public class AddAccountActivity extends AppCompatActivity implements UpdatePairTaskHandler {

    public static final String PAIRING_CODE_PARAM = "PairingCode";
    private TextView m_qrCodeTextView;
    private boolean m_inAsynchCall = false;
    private String m_pairingCodeInput = null;

    //alert dialog for downloadDialog
    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);
        applyFonts();

        m_qrCodeTextView = (TextView) findViewById(R.id.qr_text);
            /*m_qrCodeTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					scanQR();
				}
			});*/

        boolean openedAutomatically = (AccountsModel.getInstance().getAccounts().size() == 0);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(!openedAutomatically);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SprivApp.updateActionbarText(this);
        Bundle data = getIntent().getExtras();
        if (data != null) {
            boolean hasPairingInputCode = data.containsKey(PAIRING_CODE_PARAM);
            if (hasPairingInputCode) {
                m_pairingCodeInput = data.getString(PAIRING_CODE_PARAM);
                addAccountFromLongCode(m_pairingCodeInput);
            }
        }
    }

    private void applyFonts() {
        Typeface normalFont = FontsManager.getInstance().getNormalFont();
        //((TextView)findViewById(R.id.instruction_text)).setTypeface(normalFont);
        ((TextView) findViewById(R.id.qr_text)).setTypeface(normalFont);

    }

    @Override
    protected void onPause() {
        SprivApp.InForground = false;
        super.onPause();
    }

    @Override
    protected void onResume() {
        SprivApp.InForground = true;
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_account, menu);
        return true;
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean firstAccount = !AccountsModel.getInstance().hasAccounts();
        menu.findItem(R.id.action_cancel).setVisible(!firstAccount);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_cancel) {
            boolean firstAccount = !AccountsModel.getInstance().hasAccounts();
            if (!firstAccount) {
                finish();
            }
            return true;
        } else if (id == android.R.id.home) {
            goOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //product qr code mode
    public void scanQR() {
        try {
            //start the scanning activity from the com.google.zxing.client.android.SCAN intent
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            //on catch, show the download dialog
            showDialog(AddAccountActivity.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }

    //on ActivityResult method
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                //get the extras that are returned from the intent
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                Toast toast = Toast.makeText(this, "Content:" + contents + " Format:" + format, Toast.LENGTH_LONG);
                toast.show();
                addAccountFromLongCode(contents);
            }
        }
    }

    private void addAccountFromLongCode(String longCode) {
        boolean codeIsValid = isLongCodeValid(longCode);
        Log.d("usm_pair_code", "code= " + longCode+" ,codeIsValid= "+codeIsValid);
        if (codeIsValid) {
            String phoneNumber = "0";
            //String currentRegId = GCMRegistrationManager.getCurrentRegistrationId(this);
            MySharedPreferences mySharedPreferences = new MySharedPreferences(this);
            String currentRegId = mySharedPreferences.getFCMToken();
            UpdatePairTask updatePairTask = new UpdatePairTask(longCode, phoneNumber, currentRegId, this);
            Log.d("usm_pair_code_task", "phoneNumber= " + phoneNumber+" ,currentRegId= "+currentRegId);
            enableViews(false);
            m_inAsynchCall = true;
            updatePairTask.execute();
        } else {
            showInvalidLongCodeDialog();
        }

    }

    private boolean isLongCodeValid(String qrCode) {
        return qrCode != null && qrCode.length() > 6;
    }

    private void showInvalidPairingCodeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.setTitle(R.string.invalid_code_title);
        builder.setMessage(R.string.invalid_code_message);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showInvalidLongCodeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                goOut();
            }
        });
        builder.setTitle(R.string.invalid_long_code_title);
        builder.setMessage(R.string.invalid_long_code_message);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean isCodeValid(String code) {
        if (code.trim().length() != 6) {
            return false;
        } else {
            try {
                Integer.parseInt(code);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

    @Override
    public void onUpdatePairPerformed(SprivAccount sprivAccount) {

        goOut();
        m_inAsynchCall = false;
        enableViews(true);
    }

    private void goOut() {
        boolean firstAccount = (AccountsModel.getInstance().getAccounts().size() == 1);
        boolean triggeredByLink = m_pairingCodeInput != null;
        if (firstAccount || triggeredByLink) {
            ActivityNavigator.navigateToAccountList(this);
        }
        finish();
    }

    @Override
    public void onUpdatePairException(Exception exception, String key) {
        if (isShortKey(key)) {
            showInvalidPairingCodeDialog();
        } else {
            showInvalidLongCodeDialog();
        }
        m_inAsynchCall = false;
        enableViews(true);

    }

    private boolean isShortKey(String key) {
        // TODO Auto-generated method stub
        return key.length() == 6;
    }

    private void enableViews(boolean enabled) {
        m_qrCodeTextView.setEnabled(enabled);
    }

}
