package com.fir3drag.tntrun.arena.tasks;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CountdownTask {
    private final TntRun plugin;
    private final World arena;

    private BukkitRunnable countdownTask;
    private int countdownTime;
    private boolean isCounting = false;

    public CountdownTask(TntRun plugin, World arena) {
        this.plugin = plugin;
        this.arena = arena;
    }

    public void sendTitle(Player player, String title, String subTitle){
        player.sendTitle(ChatColor.YELLOW  + title, ChatColor.YELLOW + subTitle);
    }

    public void startCountdown() {
        int fullCountdown = this.plugin.data.getTntRunConfig().getInt("fullCountdown");
        int halfCountdown = this.plugin.data.getTntRunConfig().getInt("halfCountdown");
        int quarterCountdown = this.plugin.data.getTntRunConfig().getInt("quarterCountdown");
        int gracePeriod = this.plugin.data.getTntRunConfig().getInt("gracePeriod");

        String arenaName = arena.getName();

        if (isCounting) {
            return;
        }
        isCounting = true;
        countdownTime = fullCountdown;
        plugin.gameStatusMap.replace(arenaName, "starting");

        countdownTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (countdownTime <= -gracePeriod) {  // 3s grace period
                    plugin.gameStatusMap.replace(arenaName, "playing");
                    isCounting = false;

                    for (Player p: plugin.playingMap.get(arenaName)){  // triggers the move event so that they can't stand still at the start
                        plugin.blockRemoverTask.move(p);
                    }
                    this.cancel();
                }
                else if (countdownTime == 0){  // end of countdown
                    for (Player p: plugin.playingMap.get(arenaName)){
                        p.sendMessage(ChatColor.YELLOW + "Go!");
                        sendTitle(p, "Go!", "");
                        p.teleport(arena.getSpawnLocation());
                    }
                }
                else if (countdownTime > 0) {  // displays the time to the players
                    for (Player p: plugin.playingMap.get(arenaName)){
                        if (countdownTime <= quarterCountdown || countdownTime == halfCountdown || countdownTime == fullCountdown){
                            p.sendMessage(ChatColor.YELLOW + "Starting in " + countdownTime + " seconds.");
                        }
                        if (countdownTime <= 5) {
                            sendTitle(p, String.valueOf(countdownTime), "");
                        }

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
            plugin.gameStatusMap.replace(arena.getName(), "stopped");
        }

        for (Player p: arena.getPlayers()){
            p.sendMessage(ChatColor.YELLOW + "Countdown canceled.");
        }
    }

    public void modifyCountdown(Integer newTime) {
        if (isCounting) {
            countdownTime = newTime;
        }
    }

    public long getCountdownTime(){
        return countdownTime;
    }

    public boolean isCounting() {
        return isCounting;
    }
}
