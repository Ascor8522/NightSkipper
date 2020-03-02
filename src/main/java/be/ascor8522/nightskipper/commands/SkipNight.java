package be.ascor8522.nightskipper.commands;

import be.ascor8522.nightskipper.SleepPollManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkipNight implements CommandExecutor {

    private SleepPollManager manager;

    public SkipNight(SleepPollManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            manager.voteFor((Player) sender);
            return true;
        }
        sender.sendMessage("[NightSkipper] Error, only players may use this command. You are not a player, aren't you?");
        return false;
    }
}
