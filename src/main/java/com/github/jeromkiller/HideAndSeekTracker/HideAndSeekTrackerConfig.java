package com.github.jeromkiller.HideAndSeekTracker;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("hideandseektracker")
public interface HideAndSeekTrackerConfig extends Config
{
	@ConfigSection(
			name = "Area",
			description = "Seeking Area Settings",
			position = 0,
			closedByDefault = false
	)
	String AreaSection = "Area";

	@Range(max = 15)
	@ConfigItem(
			keyName = "HaS_North",
			name = "North Offset",
			description = "Customize the North offset of the find radius",
			section = "Area",
			position = 0

	)
	default int northOffset()
	{
		return 1;
	}

	@Range(max = 15)
	@ConfigItem(
			keyName = "HaS_East",
			name = "East Offset",
			description = "Customize the East offset of the find radius",
			section = "Area",
			position = 1
	)
	default int eastOffset()
	{
		return 1;
	}

	@Range(max = 15)
	@ConfigItem(
			keyName = "HaS_South",
			name = "South Offset",
			description = "Customize the South offset of the find radius",
			section = "Area",
			position = 2
	)
	default int southOffset()
	{
		return 1;
	}

	@Range(max = 15)
	@ConfigItem(
			keyName = "HaS_West",
			name = "West Offset",
			description = "Customize the West offset of the find radius",
			section = "Area",
			position = 3
	)
	default int westOffset()
	{
		return 1;
	}

	@Alpha
	@ConfigItem(
			keyName = "HaS_AreaColor",
			name = "Area Color",
			description = "Customize the color of the area boundary",
			section = "Area",
			position = 4
	)
	default Color areaColor() {
		return Color.GREEN;
	}

	@ConfigItem(
			keyName = "HaS_AreaBorderWidth",
			name = "Border Width",
			description = "Customize the width of the area boundary",
			section = "Area",
			position = 5
	)
	default double areaBorderWidth() {
		return 2.0;
	}

	@Alpha
	@ConfigItem(
			keyName = "HaS_FillColor",
			name = "Fill Color",
			description = "Customize the color of the area boundary",
			section = "Area",
			position = 6
	)
	default Color fillColor() {
		return new Color(0, 255, 0, 50);
	}

	@Units(Units.TICKS)
	@Range(max = 100)
	@ConfigItem(
			keyName = "HaS_TickLeniency",
			name = "Placement Leniency",
			description = "the amount of gameticks leniency for people to share a spot",
			position = 7
	)
	default int tickLeniency()
	{
		return 4;
	}

	@ConfigItem(
			keyName = "HaS_PlayerNames",
			name = "Participant Names",
			description = "Names of participating players, separated by a new line",
			position = 8
	)
	default String participantNames()
	{
		return "";
	}
}
