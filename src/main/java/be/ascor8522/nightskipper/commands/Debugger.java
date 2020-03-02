package be.ascor8522.nightskipper.commands;

import be.ascor8522.nightskipper.SleepPollManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Debugger implements CommandExecutor {

    private SleepPollManager sleepPollManager;

    public Debugger(SleepPollManager manager) {
        this.sleepPollManager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(this.sleepPollManager.toString());
        return true;
    }
}
