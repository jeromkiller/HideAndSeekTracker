package com.github.jeromkiller.HideAndSeekTracker;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import joptsimple.internal.Strings;
import net.runelite.client.config.ConfigManager;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class HideAndSeekSettings {

    public static final String CONFIG_GROUP = "hideAndSeekTracker";
    public static final String CAPTURE_AREA_KEY = "captureAreas";
    public static final String TICK_LENIENCY_KEY = "HaS_TickLeniency";
    public static final String PLAYER_NAMES_KEY = "HaS_PlayerNames";
    public static final String SHOW_RENDER_DIST = "HaS_ShowRenderDist";

    @Inject
    private ConfigManager configManager;
    @Inject
    private Gson gson;

    private void setValue(String group, String key, Object value)
    {
        boolean isEmpty = false;
        if(value instanceof Collection)
        {
            isEmpty = ((Collection<?>) value).isEmpty();
        }
        else if (value instanceof Map)
        {
            isEmpty = ((Map<?, ?>) value).isEmpty();
        }

        if(isEmpty)
        {
            configManager.unsetConfiguration(group, key);
            return;
        }

        final String json = gson.toJson(value);
        configManager.setConfiguration(group, key, json);
    }

    public int getTickLenience() {
        final String json = configManager.getConfiguration(CONFIG_GROUP, TICK_LENIENCY_KEY);
        if(Strings.isNullOrEmpty(json)){
            return 2;
        }
        return gson.fromJson(json, new TypeToken<Integer>(){}.getType());
    }

    public void setTickLenience(int ticks) {
        setValue(CONFIG_GROUP, TICK_LENIENCY_KEY, ticks);
    }

    public List<String> getPlayerNames() {
        final String json = configManager.getConfiguration(CONFIG_GROUP, PLAYER_NAMES_KEY);
        if(Strings.isNullOrEmpty(json)){
            return new ArrayList<>();
        }
        return gson.fromJson(json, new TypeToken<ArrayList<String>>(){}.getType());
    }

    public void setPlayerNames(List<String> playerNames) {
        setValue(CONFIG_GROUP, PLAYER_NAMES_KEY, playerNames);
    }

    public List<CaptureArea> getCaptureAreas() {
        final String json = configManager.getConfiguration(CONFIG_GROUP, CAPTURE_AREA_KEY);
        if(Strings.isNullOrEmpty(json)){
            return new ArrayList<>();
        }
        return gson.fromJson(json, new TypeToken<ArrayList<CaptureArea>>(){}.getType());
    }

    public void setCaptureAreas(List<CaptureArea> captureAreas) {
        setValue(CONFIG_GROUP, CAPTURE_AREA_KEY, captureAreas);
    }

    public boolean getShowRenderDist() {
        final String json = configManager.getConfiguration(CONFIG_GROUP, SHOW_RENDER_DIST);
        if(Strings.isNullOrEmpty(json)){
            return false;
        }
        return gson.fromJson(json, new TypeToken<Boolean>(){}.getType());
    }

    public void setShowRenderDist(boolean show) {
        setValue(CONFIG_GROUP, SHOW_RENDER_DIST, show);
    }

}
