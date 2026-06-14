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
    public static boolean waterEnabled = true;
    public static boolean waterPickupEnabled = true;
    public static boolean snowEnabled = true;
    public static boolean snowPickupEnabled = true;

    private static class Data {
        boolean boatEnabled = true;
        boolean waterEnabled = true;
        boolean waterPickupEnabled = true;
        boolean snowEnabled = true;
        boolean snowPickupEnabled = true;
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
                waterEnabled = data.waterEnabled;
                waterPickupEnabled = data.waterPickupEnabled;
                snowEnabled = data.snowEnabled;
                snowPickupEnabled = data.snowPickupEnabled;
            }
        } catch (IOException ignored) {
        }
    }

    public static void save() {
        Data data = new Data();
        data.boatEnabled = boatEnabled;
        data.waterEnabled = waterEnabled;
        data.waterPickupEnabled = waterPickupEnabled;
        data.snowEnabled = snowEnabled;
        data.snowPickupEnabled = snowPickupEnabled;

        try (Writer writer = Files.newBufferedWriter(PATH)) {
            GSON.toJson(data, writer);
        } catch (IOException ignored) {
        }
    }
}
