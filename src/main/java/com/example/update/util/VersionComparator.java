package com.example.update.util;

public class VersionComparator {

    public static boolean isNewer(String latest, String current) {
        String[] l = latest.split("\\.");
        String[] c = current.split("\\.");

        for (int i = 0; i < Math.max(l.length, c.length); i++) {
            int lv = i < l.length ? Integer.parseInt(l[i]) : 0;
            int cv = i < c.length ? Integer.parseInt(c[i]) : 0;

            if (lv > cv) return true;
            if (lv < cv) return false;
        }
        return false;
    }
}