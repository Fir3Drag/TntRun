package com.fir3drag.tntrun.arena.commands;

import com.fir3drag.tntrun.TntRun;
import com.fir3drag.tntrun.arena.commands.interfaces.SubCommand;
import com.fir3drag.tntrun.arena.commands.subcommands.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.*;

public class CommandManager implements CommandExecutor, TabCompleter {
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public CommandManager(TntRun plugin){
        subCommands.put("createArena", new CreateCommand(plugin));
        subCommands.put("deleteArena", new DeleteCommand(plugin));
        subCommands.put("editArena", new EditCommand(plugin));
        subCommands.put("forceStart", new ForceStartCommand(plugin));
        subCommands.put("join", new JoinCommand(plugin));
        subCommands.put("leave", new LeaveCommand(plugin));
        subCommands.put("listArenas", new ListCommand(plugin));
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        String errorMsg = ChatColor.RED + "/tntrun createArena | deleteArena | editArena | forceStart | join | leave | listArenas";

        if (args.length == 0) {
            commandSender.sendMessage(errorMsg);
            return true;
        }

        if (!subCommands.containsKey(args[0])){
            commandSender.sendMessage(errorMsg);
            return true;
        }

        SubCommand subCommand = subCommands.get(args[0]);
        if (subCommand != null){
            String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
            subCommand.execute(commandSender, command, s, subArgs);
        }
        else {
            commandSender.sendMessage(ChatColor.RED + "Unknown command");
            commandSender.sendMessage(errorMsg);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 1){
            List<String> allCompletions = Arrays.asList("createArena", "deleteArena", "editArena", "forceStart", "join", "leave", "listArenas");
            List<String> completions = new ArrayList<>();

            for (String completion: allCompletions){ // dynamically updates the tab list depending on whats written
                if (completion.startsWith(args[0]))
                {
                    completions.add(completion);
                }
            }
            return completions;
        }
        if (args.length == 2 && subCommands.containsKey(args[0])){  // check there is subcommand present
            SubCommand subCommand = subCommands.get(args[0]);
            if (subCommand != null){
                String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
                return subCommand.tabComplete(commandSender, command, s, subArgs);
            }
        }
        return Collections.emptyList();
    }
}
