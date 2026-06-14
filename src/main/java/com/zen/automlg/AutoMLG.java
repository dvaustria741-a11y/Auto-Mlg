package com.zen.automlg;

import net.fabricmc.api.ModInitializer;

public class AutoMLG implements ModInitializer {

    @Override
    public void onInitialize() {
        AutoMLGConfig.load();
    }
}
