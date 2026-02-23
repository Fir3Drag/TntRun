package com.fir3drag.tntrun.arena.tasks;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class StartingCountdownTask {
    private final TntRun plugin;
    private final World arena;
    private final String arenaName;

    private BukkitRunnable countdownTask;
    private int countdownTime = -1;
    private boolean isCounting = false;
    private boolean gracePeriodOver = false;

    public StartingCountdownTask(TntRun plugin, World arena) {
        this.plugin = plugin;
        this.arena = arena;
        this.arenaName = arena.getName();
    }

    public void sendTitle(Player player, String title, String subTitle){
        player.sendTitle(ChatColor.YELLOW  + title, ChatColor.YELLOW + subTitle);
    }

    public void startCountdown() {
        int fullCountdown = this.plugin.data.getTntRunConfig().getInt("fullCountdown");
        int halfCountdown = this.plugin.data.getTntRunConfig().getInt("halfCountdown");
        int quarterCountdown = this.plugin.data.getTntRunConfig().getInt("quarterCountdown");
        int gracePeriod = this.plugin.data.getTntRunConfig().getInt("gracePeriod");

        if (isCounting) {
            return;
        }
        isCounting = true;
        gracePeriodOver = false;
        countdownTime = fullCountdown;
        plugin.gameStatusMap.replace(arenaName, "starting");

        countdownTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (countdownTime <= -gracePeriod) {  // 3s grace period
                    gracePeriodOver = true;
                    isCounting = false;

                    for (Player p: plugin.playingMap.get(arenaName)){  // triggers the move event so that they can't stand still at the start
                        plugin.blockRemoverTask.move(p);
                    }
                    this.cancel();
                }
                else if (countdownTime == 0){
                    plugin.gameStatusMap.replace(arenaName, "playing");
                    plugin.playingCountUpMap.get(arenaName).startCountdown();

                    for (Player p: plugin.playingMap.get(arenaName)){ // msg players
                        p.sendMessage(ChatColor.YELLOW + "Go!");
                        sendTitle(p, "Go!", "");
                        plugin.scoreboardMap.get(p).refresh(arenaName);
                        p.teleport(arena.getSpawnLocation());
                    }
                    for (Player p: plugin.spectatingMap.get(arenaName)){ // msg spectators
                        p.sendMessage(ChatColor.YELLOW + "Go!");
                        sendTitle(p, "Go!", "");
                        plugin.scoreboardMap.get(p).refresh(arenaName);
                        p.teleport(arena.getSpawnLocation());
                    }
                }
                else if (countdownTime > 0) {  // displays the time to the players
                    for (Player p: plugin.playingMap.get(arenaName)){  // msg players starting info
                        if (countdownTime <= quarterCountdown || countdownTime == halfCountdown || countdownTime == fullCountdown){
                            p.sendMessage(ChatColor.YELLOW + "Starting in " + countdownTime + " seconds.");
                        }
                        if (countdownTime <= 5) {
                            sendTitle(p, String.valueOf(countdownTime), "");
                        }
                        plugin.scoreboardMap.get(p).refresh(arenaName);

                    }
                    for (Player p: plugin.spectatingMap.get(arenaName)){  // msg spectators starting info
                        if (countdownTime <= quarterCountdown || countdownTime == halfCountdown || countdownTime == fullCountdown){
                            p.sendMessage(ChatColor.YELLOW + "Starting in " + countdownTime + " seconds.");
                        }
                        if (countdownTime <= 5) {
                            sendTitle(p, String.valueOf(countdownTime), "");
                        }
                        plugin.scoreboardMap.get(p).refresh(arenaName);
                    }
                }
                countdownTime--;
            }
        };
        countdownTask.runTaskTimer(plugin, 0L, 20L);
    }

    public void cancelCountdown() {
        if (countdownTask != null && isCounting){
            countdownTask.cancel();
            isCounting = false;
            gracePeriodOver = false;
            plugin.gameStatusMap.replace(arenaName, "stopped");
        }

        for (Player p: this.plugin.playingMap.get(arenaName)){  // msg players
            p.sendMessage(ChatColor.YELLOW + "Countdown canceled.");
            plugin.scoreboardMap.get(p).refresh(arenaName);
        }
        for (Player p: this.plugin.spectatingMap.get(arenaName)){ // msg spectators
            p.sendMessage(ChatColor.YELLOW + "Countdown canceled.");
            plugin.scoreboardMap.get(p).refresh(arenaName);
        }
    }

    public void modifyCountdown(Integer newTime) {
        if (isCounting) {
            countdownTime = newTime;

            // updates the player scoreboard instantly to the new value
            for (Player p: this.plugin.playingMap.get(arenaName)){
                plugin.scoreboardMap.get(p).refresh(arenaName);
            }
            for (Player p: this.plugin.spectatingMap.get(arenaName)){
                plugin.scoreboardMap.get(p).refresh(arenaName);
            }

        }
    }

    public int getCountdownTime(){
        return countdownTime;
    }

    public boolean isCounting() {
        return isCounting;
    }

    public boolean isGracePeriodOver(){
        return gracePeriodOver;
    }
}
