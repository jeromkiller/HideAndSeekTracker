package com.github.jeromkiller.HideAndSeekTracker;

import com.github.jeromkiller.HideAndSeekTracker.Scoring.HintScoring;
import com.github.jeromkiller.HideAndSeekTracker.Scoring.PointSystem;
import com.github.jeromkiller.HideAndSeekTracker.Scoring.PositionScoring;
import com.github.jeromkiller.HideAndSeekTracker.Scoring.ScoreRules;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import joptsimple.internal.Strings;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.util.RuntimeTypeAdapterFactory;

import javax.inject.Inject;
import java.util.*;

public class HideAndSeekSettings {

    public static final String CONFIG_GROUP = "hideAndSeekTracker";
    public static final String CAPTURE_AREA_KEY = "captureAreas";
    public static final String TICK_LENIENCY_KEY = "HaS_TickLeniency";
    public static final String PLAYER_NAMES_KEY = "HaS_PlayerNames";
    public static final String SHOW_RENDER_DIST = "HaS_ShowRenderDist";
    public static final String SCORERULES_KEY = "HaS_ScoreRules";
    public static final String HIDE_UNFINISHED_KEY = "HaS_HideUnfinishedPlayers";
    public static final String DEV_MODE_KEY = "HaS_DevMode";

    @Inject
    private ConfigManager configManager;
    @Inject
    private Gson gson;

    private void setValue(String key, Object value)
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
            configManager.unsetConfiguration(CONFIG_GROUP, key);
            return;
        }

        final String json = gson.toJson(value);
        configManager.setConfiguration(CONFIG_GROUP, key, json);
    }

    public int getTickLenience() {
        final String json = configManager.getConfiguration(CONFIG_GROUP, TICK_LENIENCY_KEY);
        if(Strings.isNullOrEmpty(json)){
            return 2;
        }
        return gson.fromJson(json, new TypeToken<Integer>(){}.getType());
    }

    public void setTickLenience(int ticks) {
        setValue(TICK_LENIENCY_KEY, ticks);
    }

    public LinkedHashSet<String> getPlayerNames() {
        final String json = configManager.getConfiguration(CONFIG_GROUP, PLAYER_NAMES_KEY);
        if(Strings.isNullOrEmpty(json)){
            return new LinkedHashSet<>();
        }
        return gson.fromJson(json, new TypeToken<LinkedHashSet<String>>(){}.getType());
    }

    public void setPlayerNames(LinkedHashSet<String> playerNames) {
        setValue(PLAYER_NAMES_KEY, playerNames);
    }

    public List<CaptureArea> getCaptureAreas() {
        final String json = configManager.getConfiguration(CONFIG_GROUP, CAPTURE_AREA_KEY);
        if(Strings.isNullOrEmpty(json)){
            return new ArrayList<>();
        }
        return gson.fromJson(json, new TypeToken<ArrayList<CaptureArea>>(){}.getType());
    }

    public void setCaptureAreas(List<CaptureArea> captureAreas) {
        setValue(CAPTURE_AREA_KEY, captureAreas);
    }

    public boolean getShowRenderDist() {
        final String json = configManager.getConfiguration(CONFIG_GROUP, SHOW_RENDER_DIST);
        if(Strings.isNullOrEmpty(json)){
            return false;
        }
        return gson.fromJson(json, new TypeToken<Boolean>(){}.getType());
    }

    public void setShowRenderDist(boolean show) {
        setValue(SHOW_RENDER_DIST, show);
    }

    public ScoreRules getScoreRules() {
        final RuntimeTypeAdapterFactory<PointSystem> typeFactory = RuntimeTypeAdapterFactory
                .of(PointSystem.class)
                .registerSubtype(PositionScoring.class)
                .registerSubtype(HintScoring.class);

        final Gson scoreRulesGson = gson.newBuilder().registerTypeAdapterFactory(
                typeFactory).create();


        final String json = configManager.getConfiguration(CONFIG_GROUP, SCORERULES_KEY);
        if(Strings.isNullOrEmpty(json)){
            return ScoreRules.getDefaultRules();
        }

        final ScoreRules newRules = scoreRulesGson.fromJson(json, new TypeToken<ScoreRules>(){}.getType());
        return newRules;
    }

    public void setScoreRules(ScoreRules rules) {
        final RuntimeTypeAdapterFactory<PointSystem> typeFactory = RuntimeTypeAdapterFactory
                .of(PointSystem.class)
                .registerSubtype(PositionScoring.class)
                .registerSubtype(HintScoring.class);

        final Gson scoreRulesGson = gson.newBuilder().registerTypeAdapterFactory(
                typeFactory).create();

        final String json = scoreRulesGson.toJson(rules);
        configManager.setConfiguration(CONFIG_GROUP, SCORERULES_KEY, json);
    }

    public void setHideUnfinished(boolean show) {
        setValue(HIDE_UNFINISHED_KEY, show);
    }

    public boolean getHideUnfinished() {
        final String json = configManager.getConfiguration(CONFIG_GROUP, HIDE_UNFINISHED_KEY);
        if(Strings.isNullOrEmpty(json)){
            return false;
        }
        return gson.fromJson(json, new TypeToken<Boolean>(){}.getType());
    }

    public void setDevMode(boolean show) {
        setValue(DEV_MODE_KEY, show);
    }

    public boolean getDevMode() {
        final String json = configManager.getConfiguration(CONFIG_GROUP, DEV_MODE_KEY);
        if(Strings.isNullOrEmpty(json)){
            return false;
        }
        return gson.fromJson(json, new TypeToken<Boolean>(){}.getType());
    }

}
