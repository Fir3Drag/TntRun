package com.fir3drag.tntrun.arena.controllers;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class PermController {
    public boolean check(CommandSender sender, String perm){
        if (!sender.hasPermission(perm) && !sender.hasPermission("tntrun.admin")){
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return false;
        }
        return true;
    }
}
