package com.fir3drag.tntrun.arena.commands.subcommands;

import com.fir3drag.tntrun.TntRun;
import com.fir3drag.tntrun.arena.commands.interfaces.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EnableCommand implements SubCommand {
    private final TntRun plugin;

    public EnableCommand(TntRun plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender commandSender, Command command, String s, String[] args) {
        if (!this.plugin.checkPerms.check(commandSender, "tntrun.enable")){
            return;
        }

        if (args.length != 1){
            commandSender.sendMessage(ChatColor.RED + "/tntrun enable [arena]");
            return;
        }
        List<String> arenas = this.plugin.data.getDataConfig().getStringList("arenas");
        List<String> disabledArenas = this.plugin.data.getDataConfig().getStringList("disabledArenas");
        String arenaName = args[0];

        if (!disabledArenas.contains(arenaName)){  // can't enable when not disabled
            commandSender.sendMessage(ChatColor.RED + "The arena '" + arenaName + "' is not disabled.");
        }

        // enable arena
        if (!arenas.contains(arenaName)){
            commandSender.sendMessage(ChatColor.RED + "That arena does not exist.");
            return;
        }
        disabledArenas.remove(arenaName);
        this.plugin.data.getDataConfig().set("disabledArenas", disabledArenas);
        this.plugin.data.saveConfig();
        commandSender.sendMessage(ChatColor.YELLOW + "Successfully enabled arena '" + arenaName + "'.");
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 1){
            List<String> allCompletions = this.plugin.data.getDataConfig().getStringList("disabledArenas");
            List<String> completions = new ArrayList<>();

            for (String completion: allCompletions){ // dynamically updates the tab list depending on whats written
                if (completion.startsWith(args[0]))
                {
                    completions.add(completion);
                }
            }
            return completions;
        }

        return Collections.emptyList();
    }
}
