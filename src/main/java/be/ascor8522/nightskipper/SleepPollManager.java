package be.ascor8522.nightskipper;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static org.bukkit.Bukkit.getLogger;

public class SleepPollManager implements Listener {

    static final String voteFor = "§6§![NightSkipper]§r You voted to skip the night.";
    static final String voteAgainst = "§6§![NightSkipper]§r You voted not to skip the night.";
    static final String voteNeutral = "§6§![NightSkipper]§r You voted neutral when it comes to skipping the night.";

    static final String noPoll = "§6§![NightSkipper]§r There is currently no poll to answer to.\nAsk someone to go to bed first.";

    static final String youWentToSleep = "§6§![NightSkipper]§r You went to sleep.";
    static final String creatingPoll = "§6§![NightSkipper]§r Creating a sleep poll.";
    static final String heWentToSleep = "§6§![NightSkipper]§r %s went to sleep.\nDo you want to skip the night too ?";

    private ConcurrentHashMap<String, SleepPoll> polls;

    @EventHandler
    public void onPlayerEnteringBed(PlayerBedEnterEvent playerBedEnterEvent) {
        if (playerBedEnterEvent.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK) {
            if (playerBedEnterEvent.getBed().getWorld().isGameRule("doDaylightCycle")) {
                playerBedEnterEvent.getPlayer().sendMessage(SleepPollManager.youWentToSleep);
                this.voteFor(playerBedEnterEvent.getPlayer());
            } else {
                getLogger().log(Level.WARNING, "Cannot skip the night since gamerule 'doDaylightCycle' is not turned on.");
            }
        }
    }

    @EventHandler
    public void onPlayerLeavingBed(PlayerBedLeaveEvent playerBedLeaveEvent) {
        this.voteNeutral(playerBedLeaveEvent.getPlayer());
    }

    @EventHandler
    public void onPlayerExitingBed(PlayerBedLeaveEvent playerBedLeaveEvent) {
        this.voteNeutral(playerBedLeaveEvent.getPlayer());
    }

    @EventHandler
    public void onPlayerChangingWorld(PlayerChangedWorldEvent playerChangedWorldEvent) {
        this.removePlayer(playerChangedWorldEvent.getPlayer());
    }

    public SleepPollManager() {
        this.polls = new ConcurrentHashMap<String, SleepPoll>();
    }

    public ConcurrentHashMap<String, SleepPoll> getPolls() {
        return this.polls;
    }

    public void voteFor(Player player) {
        String world = player.getWorld().getName();

        SleepPoll poll = this.polls.get(world);
        if(poll == null) {
            if(player.isSleeping()) {
                poll = new SleepPoll(player);
                this.polls.put(world, poll);
            } else {
                player.sendMessage(SleepPollManager.noPoll);
                return;
            }
        }
        poll.voteFor(player);

        if(poll.getAllVoters().size() == 1) {
            poll.messagePlayer(player, SleepPollManager.creatingPoll);

            TextComponent msg1 = new TextComponent(String.format(SleepPollManager.heWentToSleep, player.getDisplayName()));

            TextComponent msg2 = new TextComponent("\n");

            TextComponent msg3 = new TextComponent("§a[YES]");
            msg3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§a§nClick to tell others you want to skip the night.").create()));
            msg3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/skipnight"));

            TextComponent msg4 = new TextComponent("  ");

            TextComponent msg5 = new TextComponent("§c[NO]");
            msg5.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§c§nClick to tell others you don't want to skip the night.").create()));
            msg5.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/notskipnight"));

            poll.messageAllExcept(player, msg1, msg2, msg3, msg4, msg5);
        } else {
            poll.messagePlayer(player, SleepPollManager.voteFor);
        }
    }

    public void voteAgainst(Player player) {
        String world = player.getWorld().getName();

        SleepPoll poll = this.polls.get(world);
        if(poll == null) {
            if(!player.isSleeping()) {
                player.sendMessage(SleepPollManager.noPoll);
            }
            return;
        }
        poll.voteAgainst(player);
        poll.messagePlayer(player, SleepPollManager.voteAgainst);
    }

    public void voteNeutral(Player player) {
        String world = player.getWorld().getName();

        SleepPoll poll = this.polls.get(world);
        if(poll == null) {
            if(!player.isSleeping()) {
                player.sendMessage(SleepPollManager.noPoll);
            }
            return;
        }
        poll.voteNeutral(player);
        poll.messagePlayer(player, SleepPollManager.voteNeutral);
    }

    public void removePlayer(Player player) {
        String world = player.getWorld().getName();

        SleepPoll poll = this.polls.get(world);
        if(poll == null) {
            getLogger().log(Level.SEVERE, "No poll to remove the player from");
            return;
        }
        poll.removePlayer(player);
    }

    @Override
    public String toString() {
        return  String.format("PollManager :\nPolls :\n[\n%s\n]",
                String.join(",\n", this.polls.entrySet().stream().map(Object::toString).collect(Collectors.toSet())));
    }
}