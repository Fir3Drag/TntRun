package com.fir3drag.tntrun.arena.commands.subcommands;

import com.fir3drag.tntrun.TntRun;
import com.fir3drag.tntrun.arena.commands.interfaces.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class ReloadCommand implements SubCommand {
    private final TntRun plugin;

    public ReloadCommand(TntRun plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!this.plugin.permController.check(commandSender, "tntrun.reload")){
            return;
        }
        commandSender.sendMessage(ChatColor.YELLOW + "Reloading");
        this.plugin.data.reloadConfig();
        commandSender.sendMessage(ChatColor.YELLOW + "Done");

        // reloads the scoreboards for players
        List<String> arenas = this.plugin.data.getDataConfig().getStringList("arenas");

        for (Player p: Bukkit.getOnlinePlayers()){
            String arenaName = p.getWorld().getName();

            if (arenas.contains(arenaName)){
                this.plugin.scoreboardController.refresh(arenaName, p);
            }
            else {
                this.plugin.scoreboardController.refresh("lobby", p);
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        return Collections.emptyList();
    }
}
