package com.spriv.utils;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

public class InstallationManager {
    private static String sID = null;

    public synchronized static String id(Context context) {
        if (sID == null) {
            File internalInstallationFile = new File(context.getFilesDir(), FileNames.INSTALLATION_FILE_NAME);

            try {
                if (!internalInstallationFile.exists()) {
                    //Check if we can restore registration id from external files (in case user clear data)
                    if (StorageHelper.isExternalStorageReadable()) {
                        File externalInstallationFile = new File(context.getExternalFilesDir(null), FileNames.INSTALLATION_FILE_NAME);
                        //Id found in external
                        if (externalInstallationFile.exists()) {
                            sID = readInstallationFile(externalInstallationFile);
                            writeInstallationFile(internalInstallationFile, sID);
                        }
                        //Id not found in external - create it once!
                        else {

                            writeInstallationFile(internalInstallationFile);
                            sID = readInstallationFile(internalInstallationFile);
                            if (StorageHelper.isExternalStorageWritable()) {
                                writeInstallationFile(externalInstallationFile, sID);
                            }
                        }
                    } else {
                        writeInstallationFile(internalInstallationFile);
                        sID = readInstallationFile(internalInstallationFile);
                    }
                } else {
                    sID = readInstallationFile(internalInstallationFile);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return sID;
    }


    public synchronized static boolean isRegistered(Context context) {
        File registrationFile = new File(context.getFilesDir(), FileNames.REGISTRATION_FILE_NAME);
        if (registrationFile.exists()) {
            return true;
        } else {
            //Check if we can restore registration id from external files (in case user clear data)
            File externalInstallationFile = new File(context.getExternalFilesDir(null), FileNames.INSTALLATION_FILE_NAME);
            //Id found in external - restore internal registration file and return true.
            if (externalInstallationFile.exists()) {
                setRegistered(context);
                return true;
            }
        }
        return false;
    }

    public synchronized static void setRegistered(Context context) {
        File registrationFile = new File(context.getFilesDir(), FileNames.REGISTRATION_FILE_NAME);
        try {
            if (!registrationFile.exists()) {
                writeRegistrationFile(registrationFile);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String readInstallationFile(File file) throws IOException {
        try {
            if (file.exists()) {
                RandomAccessFile f = new RandomAccessFile(file, "r");
                byte[] bytes = new byte[(int) f.length()];
                f.readFully(bytes);
                f.close();
                new String(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static void writeInstallationFile(File file) throws IOException {
        try {
            if (file.exists()) {
                FileOutputStream out = new FileOutputStream(file);
                String id = UUID.randomUUID().toString();
                out.write(id.getBytes());
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeInstallationFile(File file, String id) throws IOException {
        try {
            if (file.exists()) {
                FileOutputStream out = new FileOutputStream(file);
                out.write(id.getBytes());
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeRegistrationFile(File file) throws IOException {
        try {
            if (file.exists()) {
                FileOutputStream out = new FileOutputStream(file);
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}