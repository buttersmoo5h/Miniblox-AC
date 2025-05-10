package me.vekster.lightanticheat;

import me.vekster.lightanticheat.check.Check;
import me.vekster.lightanticheat.check.buffer.Buffer;
import me.vekster.lightanticheat.check.checks.combat.reach.ReachA;
import me.vekster.lightanticheat.check.checks.interaction.blockplace.BlockPlaceA;
import me.vekster.lightanticheat.check.checks.movement.flight.*;
import me.vekster.lightanticheat.check.checks.movement.nofall.NoFallA;
import me.vekster.lightanticheat.check.checks.movement.nofall.NoFallB;
import me.vekster.lightanticheat.check.checks.movement.noslow.NoSlowA;
import me.vekster.lightanticheat.check.checks.movement.speed.*;
import me.vekster.lightanticheat.check.checks.packet.badpackets.BadPacketsA;
import me.vekster.lightanticheat.check.checks.packet.badpackets.BadPacketsB;
import me.vekster.lightanticheat.check.checks.packet.badpackets.BadPacketsC;
import me.vekster.lightanticheat.check.checks.packet.badpackets.BadPacketsD;
import me.vekster.lightanticheat.check.checks.packet.morepackets.MorePacketsA;
import me.vekster.lightanticheat.check.checks.packet.morepackets.MorePacketsB;
import me.vekster.lightanticheat.check.checks.player.skinblinker.SkinBlinkerA;
import me.vekster.lightanticheat.command.LACCommand;
import me.vekster.lightanticheat.event.LACEventCaller;
import me.vekster.lightanticheat.listener.invalidping.InvalidPingListener;
import me.vekster.lightanticheat.listener.unloadedchunk.UnloadedChunkListener;
import me.vekster.lightanticheat.player.LACPlayerListener;
import me.vekster.lightanticheat.util.api.ApiUtil;
import me.vekster.lightanticheat.util.config.ConfigManager;
import me.vekster.lightanticheat.util.hook.server.folia.FoliaUtil;
import me.vekster.lightanticheat.util.logger.Logger;
import me.vekster.lightanticheat.util.npc.ExternalNPCUtil;
import me.vekster.lightanticheat.util.player.connectionstability.ConnectionStabilityListener;
import me.vekster.lightanticheat.util.player.cps.CPSListener;
import me.vekster.lightanticheat.util.tps.TPSCalculator;
import me.vekster.lightanticheat.util.updater.Updater;
import me.vekster.lightanticheat.util.violation.ViolationHandler;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;
    private static LACEventCaller eventCaller;
    private static final long BUFFER_DURATION_MILS = 20 * 1000L;
    private static final int PLUGIN_ID = 112053;
    private static final int STATS_ID = 12841;

    @Override
    public void onEnable() {
        instance = this;
        FoliaUtil.loadFoliaUtil();
        ConfigManager.loadConfig();

        Buffer.loadBufferCleaner(BUFFER_DURATION_MILS);
        TPSCalculator.loadTPSCalculator();
        Logger.logFile("");
        ApiUtil.setApiInstance();

        LACPlayerListener.loadLACPlayerListener();
        registerListener(new LACPlayerListener());

        ExternalNPCUtil.loadExternalNPCUtil();

        registerListener(new ViolationHandler());

        eventCaller = new LACEventCaller();
        registerListener(eventCaller);

        UnloadedChunkListener.handleUnloadedChunks();
        InvalidPingListener.limitMaxPing();

        CPSListener.loadCpsCalculatorOnReload();
        CPSListener.loadCpsCalculator();
        registerListener(new CPSListener());
        ConnectionStabilityListener.loadConnectionCalculatorOnReload();
        ConnectionStabilityListener.loadConnectionCalculator();
        registerListener(new ConnectionStabilityListener());

        PluginCommand antiCheatCommand = getCommand("lightanticheat");
        if (antiCheatCommand != null) {
            antiCheatCommand.setExecutor(new LACCommand());
            antiCheatCommand.setTabCompleter(new LACCommand());
        }

        Updater.loadUpdateChecker();
        registerListener(new Updater());

        registerCheckListener(new FlightB());
        registerCheckListener(new FlightC());
        registerCheckListener(new FlightC());
        registerCheckListener(new NoFallA());
        registerCheckListener(new NoFallB());
        registerCheckListener(new NoSlowA());
        registerCheckListener(new SpeedA());
        registerCheckListener(new SpeedB());
        registerCheckListener(new SpeedC());
        registerCheckListener(new ReachA());
        registerCheckListener(new BlockPlaceA());
        registerCheckListener(new MorePacketsA());
        registerCheckListener(new MorePacketsB());
        registerCheckListener(new BadPacketsA());
        registerCheckListener(new BadPacketsB());
        registerCheckListener(new BadPacketsC());
        registerCheckListener(new BadPacketsD());
        registerCheckListener(new SkinBlinkerA());
    }

    @Override
    public void onDisable() {
        if (eventCaller != null)
            eventCaller.close();
    }

    public static Main getInstance() {
        return instance;
    }

    public static long getBufferDurationMils() {
        return BUFFER_DURATION_MILS;
    }

    public static int getPluginId() {
        return PLUGIN_ID;
    }

    public static int getStatsId() {
        return STATS_ID;
    }

    private void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    private void registerCheckListener(Object object) {
        Check.registerListener(((Check) object).getCheckSetting().name, (Listener) object);
    }

}
