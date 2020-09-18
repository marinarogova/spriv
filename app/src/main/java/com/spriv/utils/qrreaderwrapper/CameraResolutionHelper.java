package com.spriv.utils.qrreaderwrapper;

import android.hardware.Camera;
import android.util.Log;

import java.util.List;

public class CameraResolutionHelper {

    public final static short CALCULATE_PREVIEW_SIZE = 1;
    public final static short CALCULATE_PICTURE_SIZE = 2;

    private Camera.Size calculatePreviewSize(int w, int h, short calculateType) {

        Camera.Size optimalSize = null;
        Camera camera;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int camIdx = 0; camIdx < Camera.getNumberOfCameras(); camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                try {
                    camera = Camera.open(camIdx);
                    switch (calculateType) {
                        case CALCULATE_PICTURE_SIZE:
                            optimalSize = getOptimalPreviewSize(camera.getParameters().getSupportedPictureSizes(), w, h);
                            break;
                        case CALCULATE_PREVIEW_SIZE:
                        default:
                            optimalSize = getOptimalPreviewSize(camera.getParameters().getSupportedPreviewSizes(), w, h);
                            break;
                    }
                    camera.release();

                } catch (Exception e) {
                    Log.e("Camera", "Camera failed to open: " + e.getLocalizedMessage());
                }
                break;
            }
        }

        return optimalSize;
    }

    public Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public static int getCameraOrientation() {
        return new Camera.CameraInfo().orientation;
    }
}
