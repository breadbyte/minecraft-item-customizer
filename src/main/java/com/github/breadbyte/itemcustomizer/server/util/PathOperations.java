package com.github.breadbyte.itemcustomizer.server.util;

public class PathOperations {
    public static String getCategoryFromPath(String path) {
        if (path.contains("/"))  {
            return path.split("/")[0];
        }
        else return null;
    }
}
