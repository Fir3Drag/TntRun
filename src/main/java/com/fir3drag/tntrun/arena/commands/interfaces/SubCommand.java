package com.fir3drag.tntrun.arena.commands.interfaces;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface SubCommand {
    void execute(CommandSender commandSender, Command command, String s, String[] args);
    List<String> tabComplete(CommandSender commandSender, Command command, String s, String[] args);
}
