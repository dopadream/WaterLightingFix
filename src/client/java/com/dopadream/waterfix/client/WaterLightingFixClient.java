package com.dopadream.waterfix.client;

import com.dopadream.waterfix.client.config.WaterfixConfig;
import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WaterLightingFixClient implements ClientModInitializer {
	public static final Logger LOG = LogManager.getLogger("waterlightingfix");

	public static final WaterfixConfig CONFIG = new WaterfixConfig();

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
	}
}