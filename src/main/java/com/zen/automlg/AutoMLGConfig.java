package com.zen.automlg;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class AutoMLGConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("automlg.json");

    public static boolean boatEnabled = true;
    public static boolean horseEnabled = true;
    public static boolean waterEnabled = true;
    public static boolean waterPickupEnabled = true;
    public static boolean snowEnabled = true;
    public static boolean snowPickupEnabled = true;
    public static boolean hayBaleEnabled = true;
    public static boolean hayBalePickupEnabled = true;
    public static boolean cobwebEnabled = true;
    public static boolean cobwebPickupEnabled = true;
    public static boolean sweetBerryEnabled = true;
    public static boolean sweetBerryPickupEnabled = true;

    private static class Data {
        boolean boatEnabled = true;
        boolean horseEnabled = true;
        boolean waterEnabled = true;
        boolean waterPickupEnabled = true;
        boolean snowEnabled = true;
        boolean snowPickupEnabled = true;
        boolean hayBaleEnabled = true;
        boolean hayBalePickupEnabled = true;
        boolean cobwebEnabled = true;
        boolean cobwebPickupEnabled = true;
        boolean sweetBerryEnabled = true;
        boolean sweetBerryPickupEnabled = true;
    }

    public static void load() {
        if (!Files.exists(PATH)) {
            save();
            return;
        }
        try (Reader reader = Files.newBufferedReader(PATH)) {
            Data data = GSON.fromJson(reader, Data.class);
            if (data != null) {
                boatEnabled = data.boatEnabled;
                horseEnabled = data.horseEnabled;
                waterEnabled = data.waterEnabled;
                waterPickupEnabled = data.waterPickupEnabled;
                snowEnabled = data.snowEnabled;
                snowPickupEnabled = data.snowPickupEnabled;
                hayBaleEnabled = data.hayBaleEnabled;
                hayBalePickupEnabled = data.hayBalePickupEnabled;
                cobwebEnabled = data.cobwebEnabled;
                cobwebPickupEnabled = data.cobwebPickupEnabled;
                sweetBerryEnabled = data.sweetBerryEnabled;
                sweetBerryPickupEnabled = data.sweetBerryPickupEnabled;
            }
        } catch (IOException ignored) {
        }
    }

    public static void save() {
        Data data = new Data();
        data.boatEnabled = boatEnabled;
        data.horseEnabled = horseEnabled;
        data.waterEnabled = waterEnabled;
        data.waterPickupEnabled = waterPickupEnabled;
        data.snowEnabled = snowEnabled;
        data.snowPickupEnabled = snowPickupEnabled;
        data.hayBaleEnabled = hayBaleEnabled;
        data.hayBalePickupEnabled = hayBalePickupEnabled;
        data.cobwebEnabled = cobwebEnabled;
        data.cobwebPickupEnabled = cobwebPickupEnabled;
        data.sweetBerryEnabled = sweetBerryEnabled;
        data.sweetBerryPickupEnabled = sweetBerryPickupEnabled;

        try (Writer writer = Files.newBufferedWriter(PATH)) {
            GSON.toJson(data, writer);
        } catch (IOException ignored) {
        }
    }
}
