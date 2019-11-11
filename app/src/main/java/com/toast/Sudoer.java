package com.toast;

import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by jaka on 2/2/16.
 */
public class Sudoer {
    private static final String TAG = Sudoer.class.getName();

    private static final boolean USE_NUSU = true;

    private Sudoer() {}

    public static class ProcessWrapper {
        Process p;

        public ProcessWrapper(Process p) {
            this.p = p;
        }

        public int waitFor() throws Exception {
            if(p == null) {
                return -1;
            } else {
                return p.waitFor();
            }
        }

        public Process get() {
            return p;
        }

        public InputStream getStdout() {
            return get().getInputStream();
        }

        public InputStream getStderr() {
            return get().getErrorStream();
        }

        public OutputStream getStdin() {
            return get().getOutputStream();
        }
    }

    private static ProcessWrapper suClassic(String args) {
        String[] cmdline = new String[3];
        cmdline[0] = "su";
        cmdline[1] = "-c";
        cmdline[2] = args;

        try {
            Process p = Runtime.getRuntime().exec(cmdline);
            return new ProcessWrapper(p);
        } catch (Exception e) {
            Log.w(TAG, "Failed to execute su process.", e);
        }

        return new ProcessWrapper(null);
    }

    private static ProcessWrapper suNusu(String args) {
        String[] cmdline = new String[3];
        cmdline[0] = "/system/xbin/nusu";
        cmdline[1] = "-c";
        cmdline[2] = args;

        try {
            Process p = Runtime.getRuntime().exec(cmdline);
            return new ProcessWrapper(p);
        } catch (Exception e) {
            Log.w(TAG, "Failed to execute su process.", e);
        }

        return new ProcessWrapper(null);
    }

    public static ProcessWrapper su(String args) {
        if(USE_NUSU) {
            return suNusu(args);
        } else {
            return suClassic(args);
        }
    }

}

