package com.dopadream.waterfix.client.config;

import com.dopadream.waterfix.client.WaterLightingFixClient;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class WaterfixConfig {

    // Borrowed from Animatica

    public static String AO_KEY = "water_ao";
    private static final Component GRAPHICS_TOOLTIP_AO = Component.translatable("options.waterlightingfix.water_ao.tooltip");

    public static final String FILE_NAME = "waterlightingfix.properties";

    private final OptionInstance<Boolean> waterAOOption;
    public boolean waterAO;

    public WaterfixConfig() {
        try {
            load();
        } catch (IOException e) {
            WaterLightingFixClient.LOG.error("Error loading config during initialization!", e);
        }

        this.waterAOOption = OptionInstance.createBoolean(
                "option.waterlightingfix.water_ao",
                OptionInstance.cachedConstantTooltip(GRAPHICS_TOOLTIP_AO),
                this.waterAO,
                value -> {
                    this.waterAO = value;
                    try {
                        this.save();
                    } catch (IOException e) { WaterLightingFixClient.LOG.error("Error saving config while changing in game!", e); }
                    Minecraft.getInstance().reloadResourcePacks();
                }
        );
    }

    public void writeTo(Properties properties) {
        properties.put(AO_KEY, Boolean.toString(waterAO));
    }

    public void readFrom(Properties properties) {
        this.waterAO = boolFrom(properties.getProperty(AO_KEY), false);
    }

    public Path getFile() throws IOException {
        var file = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
        if (!Files.exists(file)) {
            Files.createFile(file);
        }

        return file;
    }

    public OptionInstance<Boolean> getWaterAOOption() {
        return waterAOOption;
    }

    public void save() throws IOException {
        var file = getFile();
        var properties = new Properties();

        writeTo(properties);

        try (var out = Files.newOutputStream(file)) {
            properties.store(out, "Configuration file for WaterLightingFix");
        }
    }

    public void load() throws IOException {
        var file = getFile();
        var properties = new Properties();

        try (var in = Files.newInputStream(file)) {
            properties.load(in);
        }

        readFrom(properties);
    }

    private static boolean boolFrom(String s, boolean defaultVal) {
        return s == null ? defaultVal : "true".equals(s);
    }
}
