package com.zen.automlg;

public class AutoMLGConfig {

    public static boolean boatEnabled = true;
    public static boolean waterEnabled = true;
    public static boolean waterPickupEnabled = true;
    public static boolean snowEnabled = true;
    public static boolean snowPickupEnabled = true;

    public static void set(String feature, boolean value) {
        switch (feature) {
            case "boat" -> boatEnabled = value;
            case "water" -> waterEnabled = value;
            case "waterpickup" -> waterPickupEnabled = value;
            case "snow" -> snowEnabled = value;
            case "snowpickup" -> snowPickupEnabled = value;
            default -> {
            }
        }
    }
}
