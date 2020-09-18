package com.spriv.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.images.Size;
import com.spriv.R;
import com.spriv.utils.qrreaderwrapper.CameraResolutionHelper;
import com.spriv.utils.qrreaderwrapper.CodeScannerView;
import com.spriv.utils.qrreaderwrapper.DialogAlert;
import com.spriv.utils.qrreaderwrapper.QRDataListener;
import com.spriv.utils.qrreaderwrapper.QREader;


public class QRScannerActivity extends AppCompatActivity {
    public static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    public static final short REQUEST_CODE_CAMERA_PERMISSION = 1;
    // QREader
    private CodeScannerView cameraPreview;
    private QREader qrEader;
    private boolean haltScan;
    private boolean isResumedState;
    private boolean isShowingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);

        cameraPreview = findViewById(R.id.camera_view);


        observePreviewSize();
    }

    private void observePreviewSize() {
//        cameraPreview.setPreviewSize(new Point(1280, 960));
        cameraPreview.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {

                    @Override
                    public boolean onPreDraw() {
                        cameraPreview.getViewTreeObserver().removeOnPreDrawListener(this);
                        initQReader();

                        return false;
                    }
                }
        );

    }

    private void initQReader() {

        if (isPermissionGranted(PERMISSION_CAMERA)) {
            Size previewSize;

            // swapped width and height values as camera angle is rotated and it will effect aspect ration of preview
            if (CameraResolutionHelper.getCameraOrientation() == 0)
                previewSize = new Size(cameraPreview.getMeasuredHeight(), cameraPreview.getMeasuredWidth());
            else
                previewSize = new Size(cameraPreview.getMeasuredWidth(), cameraPreview.getMeasuredHeight());

            // Init QREader
            // ------------
            qrEader = new QREader.Builder(this, cameraPreview, new QRDataListener() {
                @Override
                public void onDetected(final String data) {
                    Log.d("usm_qr_code_result", "QRResult= " + data);
                    QRScannerActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            QRScannerActivity.this.onScanResultReceived(data);
                        }
                    });
                }
            }).facing(QREader.BACK_CAM)
                    .enableAutofocus(true)
                    .height(previewSize.getHeight())
                    .width(previewSize.getWidth())
                    .build();
//                            qrEader.init();
            qrEader.initAndStart(cameraPreview, true);
            qrEader.setSurfaceCreated(true);  // onSurfaceCreated callback is not calling first time due to some conflict with layoutObserver used here. So, need to set surfaceCreated=true ourselves initially


        } else {
            requestPermission(PERMISSION_CAMERA);
        }
    }

    private boolean isCameraRunning() {
        return qrEader != null && qrEader.isCameraRunning();
    }

    @Override
    public void onResume() {
        isResumedState = true;
        Log.d("usm_qr_resume", "onResume: haltScanner= " + haltScan + " ,isShowingDialog= " + isShowingDialog
                + " ,isCameraRunning= " + isCameraRunning() + " ,isResumedState= " + isResumedState
        );
        super.onResume();

        if (qrEader != null && !haltScan && !isCameraRunning() && isPermissionGranted(PERMISSION_CAMERA)) {
            cameraPreview.post(new Runnable() {
                @Override
                public void run() {
                    Log.d("usm_qr_resume_post", "cameraPreview Post is called: isResumedState= " + isResumedState);
                    if (isResumedState) {
                        //qrEader.initAndStart(mFragmentQrscannerBinding.cameraPreview, getUserVisibleHint());
                        qrEader.initAndStartWithoutObserver(true);
                    }
                }
            });
        }
        if (!isShowingDialog)
            haltScan = false;
    }

    @Override
    public void onPause() {
        Log.d("usm_qr_pause", "onPause: haltScanner= " + haltScan + " ,isShowingDialog= " + isShowingDialog
                + " ,isCameraRunning= " + isCameraRunning()
        );
        super.onPause();
        isResumedState = false;
        if (isCameraRunning()) {
//            qrEader.stop();
            qrEader.releaseAndCleanup();
        }
        if (!isShowingDialog)
            haltScan = false;
    }

    private void onScanResultReceived(String result) {

        if (haltScan) {
            Log.d("usm_qr_scanner_2", "Ignore Scan Result. Already processing.");
            return;
        }
        haltScan = true;
        if (qrEader != null)
            qrEader.stop();
        try {
            Uri uri = Uri.parse(result);
            String pairCode = uri.getQueryParameter("ID");
            Intent intent = new Intent();
            intent.putExtra(AddAccountActivity.PAIRING_CODE_PARAM, pairCode);
            setResult(Activity.RESULT_OK, intent);
            finish();
        } catch (Exception e) {
            Toast.makeText(QRScannerActivity.this, "Failed to recognize QR Code", Toast.LENGTH_SHORT).show();

            isShowingDialog = true;
            DialogAlert dialogAlert = DialogAlert.getInstance(this);
            dialogAlert.setMessage("Failed to recognize QR Code");
            dialogAlert.setCancelable(false);
            dialogAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    haltScan = isShowingDialog = false;
                    if (qrEader != null)
                        qrEader.start();
                    else {
                        QRScannerActivity.this.onResume();
                    }

                    dialog.dismiss();
                }
            });
            dialogAlert.show();
        }

    }

    public void requestPermission(String permission) {
        ActivityCompat.requestPermissions(
                this,
                new String[]{permission},
                REQUEST_CODE_CAMERA_PERMISSION);
    }

    public boolean isPermissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
                if (isPermissionGranted(permissions[0]))
                    initQReader();
                else
                    Toast.makeText(this, "Can't proceed without permission", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
