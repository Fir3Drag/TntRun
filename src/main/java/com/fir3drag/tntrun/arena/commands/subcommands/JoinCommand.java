package com.fir3drag.tntrun.arena.commands.subcommands;

import com.fir3drag.tntrun.TntRun;
import com.fir3drag.tntrun.arena.commands.interfaces.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class JoinCommand implements SubCommand {
    private final TntRun plugin;

    public JoinCommand(TntRun plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender commandSender, Command command, String s, String[] args) {
        if (!this.plugin.checkPerms.check(commandSender, "tntrun.join")){
            return;
        }

        List<String> arenas = this.plugin.data.getDataConfig().getStringList("Arenas");
        int maxPlayers = this.plugin.data.getTntRunConfig().getInt("maxPlayers");

        if (commandSender instanceof Player){
            Player player = (Player) commandSender;
            World currentWorld = player.getWorld();
            String currentWorldName = player.getWorld().getName();

            if (args.length == 0){
                player.sendMessage(ChatColor.RED + "/tntrun join [arena]");
                return;
            }

            String arenaName = args[0];
            World arena = Bukkit.getWorld(arenaName);

            if (arena == null){
                player.sendMessage(ChatColor.RED + "Cannot find arena: " + arenaName + ".");
                return;
            }

            if (Objects.equals(currentWorldName, arenaName)) {  // checks if your going to the world ur already in
                player.sendMessage(ChatColor.RED + "Your already in that arena.");
                return;
            }

            if (this.plugin.gameStatusMap.get(arenaName).equals("starting") && arena.getPlayers().size() >= maxPlayers) {  // checks if your going to the world ur already in
                player.sendMessage(ChatColor.RED + "That arena is full, please wait for it to start and then join to be set as a spectator.");
                return;
            }

            if (arenas.contains(arenaName)) {  // prevents you joining the arena whilst its restarting
                if (this.plugin.gameStatusMap.get(arenaName).equals("restarting")){
                    player.sendMessage(ChatColor.RED + "That arena is currently restarting.");
                    return;
                }
                player.teleport(arena.getSpawnLocation());  // teleport first to make sure they get the right chat msgs
                player.setGameMode(GameMode.SURVIVAL);

                if (this.plugin.gameStatusMap.get(arenaName).equals("stopped") || this.plugin.gameStatusMap.get(arenaName).equals("starting")) {  // if the game is not started add them as a player
                    this.plugin.changePlayerMaps.addPlayerToPlaying(arenaName, player);  // adds the player to the playing list
                    this.plugin.countdown.checkForStart(arena, 0);  // handles the countdown times depending on player size
                }

                if (this.plugin.gameStatusMap.get(arenaName).equals("playing")) {  // if the game is started add them as a spectator
                    this.plugin.changePlayerMaps.addPlayerToSpectating(arenaName, player);// adds the player to the spectating list
                }
            }
            // handles the world you were in (done after to make sure msgs are sent correctly, e.g. tp player out of world before the countdown cancel msg appears
            if (arenas.contains(currentWorldName)) { // current world checks
                // if the player is already in an arena remove them from the lists before tping them to the new arena
                this.plugin.changePlayerMaps.removePlayerAll(arenaName, player);
                this.plugin.countdown.checkForCancel(currentWorld);  // handles the countdown
                this.plugin.checkForWinner.check(currentWorld, player);  // handles players leaving during game
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 1){
            List<String> allCompletions = this.plugin.data.getDataConfig().getStringList("Arenas");
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
