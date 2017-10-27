package com.hearst.fbia.app.market;

import java.util.LinkedHashMap;
import java.util.Map;

public class MarketRegistry {

	private static Map<String, Market> marketRegistry = new LinkedHashMap<String, Market>();

	public static void registerMarket(String marketerName, Market market) {
		marketRegistry.put(marketerName.toLowerCase(), market);
	}

	public static Market getRegisteredMarket(String marketerName) {
		return marketRegistry.get(marketerName.toLowerCase());
	}

}