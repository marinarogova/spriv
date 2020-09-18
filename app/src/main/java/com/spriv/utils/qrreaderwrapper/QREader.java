/*
 * Copyright (C) 2016 Nishant Srivastava
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.spriv.utils.qrreaderwrapper;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

/**
 * QREader Singleton.
 */
public class QREader {
    /**
     * The constant FRONT_CAM.
     */
    public static final int FRONT_CAM = CameraSource.CAMERA_FACING_FRONT;
    /**
     * The constant BACK_CAM.
     */
    public static final int BACK_CAM = CameraSource.CAMERA_FACING_BACK;
    private final String LOGTAG = "usm_qr_" + getClass().getSimpleName();
    private final int width;
    private final int height;
    private final int facing;
    private final QRDataListener qrDataListener;
    private final Context context;
    private final SurfaceView surfaceView;
    private CameraSource cameraSource = null;
    private BarcodeDetector barcodeDetector = null;
    private boolean autoFocusEnabled;

    private boolean cameraRunning = false;

    private boolean surfaceCreated = false;
    private final SurfaceHolder.Callback surfaceHolderCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            Log.d("usm_qr_reader_0", "surfaceCreated is called");
            //we can start barcode after after creating
            surfaceCreated = true;
            startCameraView(context, cameraSource, surfaceView);
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            Log.d("usm_qr_reader_1", "surfaceChanged is called");
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            Log.d("usm_qr_reader_2", "surfaceDestroyed is called");
            surfaceCreated = false;
            stop();
            surfaceHolder.removeCallback(this);
        }
    };

    /**
     * Instantiates a new QREader.
     *
     * @param builder the builder
     */
    /*
     * Instantiates a new QREader
     *
     * @param builder the builder
     */
    private QREader(final Builder builder) {
        this.autoFocusEnabled = builder.autofocusEnabled;
        this.width = builder.width;
        this.height = builder.height;
        this.facing = builder.facing;
        this.qrDataListener = builder.qrDataListener;
        this.context = builder.context;
        this.surfaceView = builder.surfaceView;
        //for better performance we should use one detector for all Reader, if builder not specify it
        if (builder.barcodeDetector == null) {
            this.barcodeDetector = BarcodeDetectorHolder.getBarcodeDetector(context);
        } else {
            this.barcodeDetector = builder.barcodeDetector;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private static void removeOnGlobalLayoutListener(View v,
                                                     ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }

    public void initAndStart(final CodeScannerView scannerView, final boolean shouldStart) {
        Log.d("usm_qr_reader", "initAndStart: shouldStart= " + shouldStart);

        scannerView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        init();
                        if (shouldStart)
                            start();
                        removeOnGlobalLayoutListener(scannerView, this);
                    }
                });
    }

    public void initAndStartWithoutObserver(boolean shouldStart) {
        init();
        if (shouldStart)
            start();
    }

    /**
     * onSurfaceCreated callback is not calling first time due to some conflict with layoutObserver used here. So, need to set surfaceCreated=true ourselves initially
     *
     * @param surfaceCreated boolean
     */
    public void setSurfaceCreated(boolean surfaceCreated) {
        this.surfaceCreated = surfaceView != null && surfaceCreated;
    }

    public Size getPreviewSize() {
        return cameraSource != null ? cameraSource.getPreviewSize() : new Size(width, height);
    }

    /**
     * Init.
     */
    public void init() {
        Log.d("usm_qr_reader", "init is called: barcodeDetector= " + barcodeDetector.isOperational());

        if (!hasAutofocus(context)) {
            Log.e(LOGTAG, "Do not have autofocus feature, disabling autofocus feature in the library!");
            autoFocusEnabled = false;
        }

        if (!hasCameraHardware(context)) {
            Log.e(LOGTAG, "Does not have camera hardware!");
            return;
        }
        if (!checkCameraPermission(context)) {
            Log.e(LOGTAG, "Do not have camera permission!");
            return;
        }

        if (barcodeDetector.isOperational()) {
            barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
                @Override
                public void release() {
                    // Handled via public method
                }

                @Override
                public void receiveDetections(Detector.Detections<Barcode> detections) {
                    final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                    if (barcodes.size() != 0 && qrDataListener != null) {
                        qrDataListener.onDetected(barcodes.valueAt(0).displayValue);
                    }
                }
            });

            cameraSource =
                    new CameraSource.Builder(context, barcodeDetector).setAutoFocusEnabled(true)
                            .setFacing(facing)
                            .setRequestedPreviewSize(width, height)
                            .build();

        } else {
            Log.e(LOGTAG, "Barcode recognition libs are not downloaded and are not operational");
        }
    }

    public BarcodeDetector getBarcodeDetector() {
        return barcodeDetector;
    }

    /**
     * Start scanning qr codes.
     */
    public void start() {
        Log.d("usm_qr_reader", "start: surfaceCreated= " + surfaceCreated + " ,cameraRunning= " + cameraRunning);
        if (surfaceView != null) {
            //if surface already created, we can start camera
            if (surfaceCreated) {
                startCameraView(context, cameraSource, surfaceView);
            } else {
                //startCameraView will be invoke in void surfaceCreated
                surfaceView.getHolder().addCallback(surfaceHolderCallback);
            }
        }
    }

    private boolean hasAutofocus(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS);
    }

    private boolean hasCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private boolean checkCameraPermission(Context context) {
        String permission = Manifest.permission.CAMERA;
        int res = context.checkCallingOrSelfPermission(permission);
        return res == PackageManager.PERMISSION_GRANTED;
    }

    private void startCameraView(Context context, CameraSource cameraSource,
                                 SurfaceView surfaceView) {

        Log.d("usm_qr_reader_0", "startCameraView is called: cameraRunning= " + cameraRunning);
        if (cameraRunning) {
            Log.d("usm_qr_camera_return", "Camera already started!");
            return;
            // throw new IllegalStateException("Camera already started!");
        }
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.e(LOGTAG, "Permission not granted!");
            } else if (!cameraRunning && cameraSource != null && surfaceView != null) {
                Log.d("usm_qr_reader_1", "startCameraView is called");
                cameraSource.start(surfaceView.getHolder());
                cameraRunning = true;
            }
        } catch (IOException ie) {
            Log.e(LOGTAG, ie.getMessage());
            ie.printStackTrace();
        }
    }

    /**
     * Is camera running boolean.
     *
     * @return the boolean
     */
    public boolean isCameraRunning() {
        return cameraRunning;
    }

    /**
     * Release and cleanup QREader.
     */
    public void releaseAndCleanup() {
        stop();
        if (cameraSource != null) {
            Log.d("usm_qr_reader", "release is calling");
            //release camera and barcode detector(will invoke inside) resources
            cameraSource.release();
            cameraSource = null;
        }
    }

    /**
     * Stop camera
     */
    public void stop() {
        Log.d("usm_qr_reader", "stop is called: cameraRunning= " + cameraRunning);
        try {
            if (cameraRunning && cameraSource != null) {
                cameraSource.stop();
                cameraRunning = false;
            }
        } catch (Exception ie) {
            Log.e(LOGTAG, ie.getMessage());
            ie.printStackTrace();
        }
    }

    /**
     * The type Builder.
     */
    public static class Builder {
        private final QRDataListener qrDataListener;
        private final Context context;
        private final SurfaceView surfaceView;
        private boolean autofocusEnabled;
        private int width;
        private int height;
        private int facing;
        private BarcodeDetector barcodeDetector;

        /**
         * Instantiates a new Builder.
         *
         * @param context        the context
         * @param scannerView    the code scanner view
         * @param qrDataListener the qr data listener
         */
        public Builder(Context context, CodeScannerView scannerView, QRDataListener qrDataListener) {
            this.autofocusEnabled = true;
            this.width = 800;
            this.height = 800;
            this.facing = BACK_CAM;
            this.qrDataListener = qrDataListener;
            this.context = context;
            this.surfaceView = scannerView.getPreviewView();
        }

        /**
         * Enable autofocus builder.
         *
         * @param autofocusEnabled the autofocus enabled
         * @return the builder
         */
        public Builder enableAutofocus(boolean autofocusEnabled) {
            this.autofocusEnabled = autofocusEnabled;
            return this;
        }

        /**
         * Width builder.
         *
         * @param width the width
         * @return the builder
         */
        public Builder width(int width) {
            if (width != 0) {
                this.width = width;
            }
            return this;
        }

        /**
         * Height builder.
         *
         * @param height the height
         * @return the builder
         */
        public Builder height(int height) {
            if (height != 0) {
                this.height = height;
            }
            return this;
        }

        /**
         * Facing builder.
         *
         * @param facing the facing
         * @return the builder
         */
        public Builder facing(int facing) {
            this.facing = facing;
            return this;
        }

        /**
         * Build QREader
         *
         * @return the QREader
         */
        public QREader build() {
            return new QREader(this);
        }

        /**
         * Barcode detector.
         *
         * @param barcodeDetector the barcode detector
         */
        public void barcodeDetector(BarcodeDetector barcodeDetector) {
            this.barcodeDetector = barcodeDetector;
        }
    }
}

