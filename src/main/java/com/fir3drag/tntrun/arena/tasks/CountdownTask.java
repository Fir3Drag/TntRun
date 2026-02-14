package com.fir3drag.tntrun.arena.tasks;

import com.fir3drag.tntrun.TntRun;
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

    public void startCountdown() {
        int fullCountdown = this.plugin.data.getTntRunConfig().getInt("fullCountdown");
        int halfCountdown = this.plugin.data.getTntRunConfig().getInt("halfCountdown");
        int quarterCountdown = this.plugin.data.getTntRunConfig().getInt("quarterCountdown");

        if (isCounting) {
            return;
        }
        isCounting = true;
        countdownTime = fullCountdown;
        plugin.gameStatusMap.replace(arena.getName(), "starting");

        countdownTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (countdownTime <= 0) {
                    for (Player p: arena.getPlayers()){
                        p.sendMessage(ChatColor.YELLOW + "Go!");
                    }
                    plugin.gameStatusMap.replace(arena.getName(), "playing");
                    isCounting = false;

                    for (Player p: plugin.playingMap.get(arena.getName())){  // triggers the move event so that they can't stand still at the start
                        plugin.tntRunBlockRemover.move(p);
                    }
                    this.cancel();
                } else {
                    for (Player p: arena.getPlayers()){
                        if (countdownTime <= quarterCountdown || countdownTime == halfCountdown || countdownTime == fullCountdown){
                            p.sendMessage(ChatColor.YELLOW + "Starting in " + countdownTime + " seconds.");
                        }
                    }
                    countdownTime--;
                }
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

    public void modifyCountdown(int newTime) {
        if (isCounting) {
            countdownTime = newTime;
        }
    }

    public boolean isCounting() {
        return isCounting;
    }
}
