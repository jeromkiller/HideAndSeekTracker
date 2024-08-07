package com.github.jeromkiller.HideAndSeekTracker;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class HideAndSeekTrackerPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(HideAndSeekTrackerPlugin.class);
		RuneLite.main(args);
	}
}