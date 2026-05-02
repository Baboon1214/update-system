package com.example.update.util;

public class VersionComparator {

    public static boolean isNewer(String latest, String current) {
        String[] latestParts = latest.split("\\.");
        String[] currentParts = current.split("\\.");

        int length = Math.max(latestParts.length, currentParts.length);

        for (int i = 0; i < length; i++) {
            int l = i < latestParts.length ? Integer.parseInt(latestParts[i]) : 0;
            int c = i < currentParts.length ? Integer.parseInt(currentParts[i]) : 0;

            if (l > c) return true;
            if (l < c) return false;
        }

        return false;
    }
}