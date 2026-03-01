package com.fir3drag.tntrun.arena.commands.subcommands;

import com.fir3drag.tntrun.TntRun;
import com.fir3drag.tntrun.arena.commands.interfaces.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class EnableCommand implements SubCommand {
    private final TntRun plugin;

    public EnableCommand(TntRun plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!this.plugin.permController.check(commandSender, "tntrun.enable")){
            return;
        }

        if (args.length != 1){
            commandSender.sendMessage(ChatColor.RED + "/tntrun enable [arena]");
            return;
        }
        List<String> arenas = this.plugin.defaultValues.getArenas();
        List<String> disabledArenas = this.plugin.defaultValues.getDisabledArenas();
        String arenaName = args[0];

        if (!disabledArenas.contains(arenaName)){  // can't enable when not disabled
            commandSender.sendMessage(ChatColor.RED + "The arena '" + arenaName + "' is not disabled.");
        }

        // enable arena
        Bukkit.broadcastMessage(arenas.toString());
        Bukkit.broadcastMessage(" ");
        Bukkit.broadcastMessage(disabledArenas.toString());
        if (!arenas.contains(arenaName)){
            commandSender.sendMessage(ChatColor.RED + "That arena does not exist.");
            return;
        }
        disabledArenas.remove(arenaName);
        this.plugin.data.getDataConfig().set("disabledArenas", disabledArenas);
        this.plugin.data.saveConfig();
        this.plugin.lobbyController.createJoiningArenaInventory();
        commandSender.sendMessage(ChatColor.YELLOW + "Successfully enabled arena '" + arenaName + "'.");
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 1){
            List<String> allCompletions = this.plugin.defaultValues.getDisabledArenas();
            List<String> completions = new ArrayList<>();

            for (String completion: allCompletions){ // dynamically updates the tab list depending on whats written
                if (completion.toLowerCase(Locale.ROOT).startsWith(args[0].toLowerCase(Locale.ROOT)))
                {
                    completions.add(completion);
                }
            }
            return completions;
        }

        return Collections.emptyList();
    }
}
