package com.github.jeromkiller.HideAndSeekTracker;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.util.List;

@Slf4j
@PluginDescriptor(
	name = "HideAndSeekTracker"
)
public class HideAndSeekTrackerPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private HideAndSeekTrackerConfig config;

	@Inject
	private HideAndSeekTrackerSceneOverlay sceneOverlay;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ConfigManager configManager;

	@Inject
	private ClientToolbar clientToolbar;

    private NavigationButton navButton;

	public HideAndSeekGame game;

	@Override
	protected void startUp()
	{
		overlayManager.add(sceneOverlay);

		game = new HideAndSeekGame(config);
        HideAndSeekTrackerPanel panel = new HideAndSeekTrackerPanel(this);
		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "HnS_icon.png");
		navButton = NavigationButton.builder()
				.tooltip("Hide and Seek")
				.priority(5)
				.panel(panel)
				.icon(icon)
				.build();
		clientToolbar.addNavigation(navButton);
		game.setTable(panel.getTable());

		log.info("HideAndSeekTracker started!");
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(sceneOverlay);
		clientToolbar.removeNavigation(navButton);
		log.info("HideAndSeekTracker stopped!");
	}

	@Provides
	HideAndSeekTrackerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(HideAndSeekTrackerConfig.class);
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		checkPlayersInRange();
		game.tick();
	}

	private void checkPlayersInRange()
	{
		List<? extends  Player> playersList = client.getPlayers();	// is there a non deprecated method of getting this information?
		Player localPlayer = client.getLocalPlayer();
		for(Player player : playersList)
		{
			if(player == localPlayer)
			{
				continue;
			}

			String playerName = player.getName();
			if(isInHiderRange(player))
			{
				game.playerFound(playerName);
			}
		}
	}

	private boolean isInHiderRange(Player player)
	{
		Player localPlayer = client.getLocalPlayer();
		WorldPoint localPlayerLoc = localPlayer.getWorldLocation();
		WorldPoint playerLoc = player.getWorldLocation();

		int negX = -config.westOffset();
		int negY = -config.southOffset();
		WorldPoint negCorner = localPlayerLoc.dx(negX).dy(negY);

		int posX = config.eastOffset();
		int posY = config.northOffset();
		WorldPoint posCorner = localPlayerLoc.dx(posX).dy(posY);

		boolean inRange = ((playerLoc.getX() >= negCorner.getX()) && (playerLoc.getY() >= negCorner.getY())
		&& (playerLoc.getX() <= posCorner.getX()) && (playerLoc.getY() <= posCorner.getY()));

		return inRange;
	}

	public String loadStartingPlayers()
	{
		String[] playerNames = config.participantNames().split("\n");
        return game.setPlayers(playerNames);
	}
}
