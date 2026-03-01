package com.fir3drag.tntrun.arena.commands.subcommands;

import com.fir3drag.tntrun.TntRun;
import com.fir3drag.tntrun.arena.commands.interfaces.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class ListCommand implements SubCommand {
    private final TntRun plugin;

    public ListCommand(TntRun plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!this.plugin.permController.check(commandSender, "tntrun.list")){
            return;
        }
        commandSender.sendMessage(ChatColor.YELLOW + "Arenas: " + this.plugin.defaultValues.getArenas());
        commandSender.sendMessage(ChatColor.YELLOW + "Disabled arenas: " + this.plugin.defaultValues.getDisabledArenas());
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        return Collections.emptyList();
    }
}
