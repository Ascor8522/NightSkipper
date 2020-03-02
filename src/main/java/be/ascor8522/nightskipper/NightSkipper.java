package be.ascor8522.nightskipper;

import be.ascor8522.nightskipper.commands.Debugger;
import be.ascor8522.nightskipper.commands.NotSkipNight;
import be.ascor8522.nightskipper.commands.SkipNight;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;

public class NightSkipper extends JavaPlugin implements Listener {

	private SleepPollManager sleepPollManager;

	@Override
	public void onLoad() {
		super.onLoad();
		getLogger().log(Level.INFO, "Plugin loaded");
	}

	@Override
	public void onEnable() {
		super.onEnable();
		this.sleepPollManager = new SleepPollManager();
		getServer().getPluginManager().registerEvents(this.sleepPollManager, this);
		Objects.requireNonNull(getCommand("debugger")).setExecutor(new Debugger(this.sleepPollManager));
		Objects.requireNonNull(getCommand("skipnight")).setExecutor(new SkipNight(this.sleepPollManager));
		Objects.requireNonNull(getCommand("notskipnight")).setExecutor(new NotSkipNight(this.sleepPollManager));
	}
}