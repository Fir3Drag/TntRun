package com.fir3drag.tntrun.arena.tasks;

import com.fir3drag.tntrun.TntRun;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayingCountUpTask {
    private final TntRun plugin;
    private final String arenaName;

    private BukkitRunnable countdownTask;
    private int countdownTime = 0;
    private boolean isCounting = false;

    public PlayingCountUpTask(TntRun plugin, World arena) {
        this.plugin = plugin;
        this.arenaName = arena.getName();
    }

    public void startCountdown() {
        if (isCounting) {
            return;
        }
        isCounting = true;
        countdownTime = 0;

        countdownTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p: plugin.playingMap.get(arenaName)){  // msg players starting info
                    plugin.scoreboardMap.get(p).refresh(arenaName);
                }
                for (Player p: plugin.spectatingMap.get(arenaName)) {  // msg spectators starting info
                    plugin.scoreboardMap.get(p).refresh(arenaName);
                }
                countdownTime++;
            }
        };
        countdownTask.runTaskTimer(plugin, 0L, 20L);
    }

    public void cancelCountdown() {
        if (countdownTask != null && isCounting){
            countdownTask.cancel();
            isCounting = false;
        }
    }

    public int getCountdownTime(){
        return countdownTime;
    }
}
