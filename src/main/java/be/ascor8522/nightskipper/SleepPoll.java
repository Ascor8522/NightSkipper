package be.ascor8522.nightskipper;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

public class SleepPoll {

    private final int duration = 60 * 1000; // 60 secs * 1000 ms

    private Date startedTimestamp;

    private Player starter;

    private Collection<Player> voting;
    private Collection<Player> playersFor;
    private Collection<Player> playersAgainst;
    private Collection<Player> playersNeutral;

    private World world;

    SleepPoll(Player player) {
        this.starter = player;

        this.startedTimestamp = new Date();

        this.voting = new ArrayList<>();
        this.playersFor = new ArrayList<>();
        this.playersAgainst = new ArrayList<>();
        this.playersNeutral = new ArrayList<>();

        this.voting = player.getWorld().getPlayers();
        this.voting.remove(player);
        this.playersFor.add(player);

        this.world = player.getWorld();
    }

    Player getStarter() {
        return this.starter;
    }

    Date getStartedTimestamp() {
        return this.startedTimestamp;
    }

    Collection<Player> getVoting() {
        return new ArrayList<>(this.voting);
    }

    Collection<Player> getPlayersFor() {
        return new ArrayList<>(this.playersFor);
    }

    Collection<Player> getPlayersAgainst() {
        return new ArrayList<>(this.playersAgainst);
    }

    Collection<Player> getAllVoters() {
        Collection<Player> allPlayers = new ArrayList<>();
        allPlayers.addAll(this.voting);
        allPlayers.addAll(this.playersFor);
        allPlayers.addAll(this.playersAgainst);
        return allPlayers;
    }

    World getWorld() {
        return this.world;
    }

    boolean isValid() {
        return /* durationLeft() > 0 && */ this.world.getTime() >= 12000 && playersFor.stream().anyMatch(LivingEntity::isSleeping);
    }

    int getDurationLeft() {
        return -1;
    }

    boolean canSkipNight() {
        return  this.playersFor.size() > (this.playersAgainst.size() + this.voting.size())
                && ((this.voting.size() + this.playersFor.size()) + this.playersAgainst.size()) > 1;
    }

    void voteFor(Player player) {
        this.voting.remove(player);
        this.playersFor.remove(player);
        this.playersAgainst.remove(player);
        this.playersNeutral.remove(player);

        this.playersFor.add(player);

        if(this.canSkipNight()) {
            this.skipNight();
        }
    }

    void voteAgainst(Player player) {
        this.voting.remove(player);
        this.playersFor.remove(player);
        this.playersAgainst.remove(player);
        this.playersNeutral.remove(player);

        this.playersAgainst.add(player);
    }

    void voteNeutral(Player player) {
        this.voting.remove(player);
        this.playersFor.remove(player);
        this.playersAgainst.remove(player);
        this.playersNeutral.remove(player);

        this.playersNeutral.add(player);

        if(this.canSkipNight()) {
            this.skipNight();
        }
    }

    void removePlayer(Player player) {
        this.voting.remove(player);
        this.playersFor.remove(player);
        this.playersAgainst.remove(player);
        this.playersNeutral.remove(player);
    }

    private void skipNight() {
        this.world.setFullTime(this.world.getFullTime() + (24000 - this.world.getTime()));
    }

    @Override
    public String toString() {
        return String.format(
                "{\n" +
                "  Poll created by: %s\n" +
                "  in world: %s\n" +
                "  at: %tc\n" +
                "  Results:\n" +
                "    - For: %d%% (%d) [%s]\n" +
                "    - Against: %d%% (%d) [%s]\n" +
                "    - Neutral: %d%% (%d) [%s]\n" +
                "}",
                this.starter.getDisplayName(),
                this.world.getName(),
                this.startedTimestamp,
                this.playersFor.size() / this.getAllVoters().size(),
                this.playersFor.size(),
                String.join(", ", this.playersFor.stream().map(Player::getDisplayName).collect(Collectors.toSet())),
                this.playersAgainst.size() / this.getAllVoters().size(),
                this.playersAgainst.size(),
                String.join(", ", this.playersAgainst.stream().map(Player::getDisplayName).collect(Collectors.toSet())),
                this.playersNeutral.size() / this.getAllVoters().size(),
                this.playersNeutral.size(),
                String.join(", ", this.playersNeutral.stream().map(Player::getDisplayName).collect(Collectors.toSet()))
        );
    }

    void messagePlayer(Player player, String message) {
        player.sendMessage(message);
        player.playNote(player.getLocation(), Instrument.IRON_XYLOPHONE, Note.sharp(2, Note.Tone.F));
    }

    void messagePlayer(Player player, TextComponent ... messages) {
        player.spigot().sendMessage(messages);
        player.playNote(player.getLocation(), Instrument.IRON_XYLOPHONE, Note.sharp(2, Note.Tone.F));
    }

    void messageAll(String message) {
        ArrayList<Player> players = new ArrayList<>();
        players.addAll(this.playersFor);
        players.addAll(this.playersAgainst);
        players.addAll(this.playersNeutral);

        players.addAll(this.voting);

        players.forEach(player -> {
            player.sendMessage(message);
            player.playNote(player.getLocation(), Instrument.IRON_XYLOPHONE, Note.sharp(2, Note.Tone.F));
        });
    }

    void messageAll(TextComponent ... messages) {
        ArrayList<Player> players = new ArrayList<>();
        players.addAll(this.playersFor);
        players.addAll(this.playersAgainst);
        players.addAll(this.playersNeutral);

        players.addAll(this.voting);

        players.forEach(player -> {
            player.spigot().sendMessage(messages);
            player.playNote(player.getLocation(), Instrument.IRON_XYLOPHONE, Note.sharp(2, Note.Tone.F));
        });
    }

    void messageAllExcept(Player player, String message) {
        ArrayList<Player> players = new ArrayList<>();
        players.addAll(this.playersFor);
        players.addAll(this.playersAgainst);
        players.addAll(this.playersNeutral);
        players.addAll(this.voting);

        players.remove(player);

        players.forEach(playerMsg -> {
            playerMsg.sendMessage(message);
            playerMsg.playNote(playerMsg.getLocation(), Instrument.IRON_XYLOPHONE, Note.sharp(2, Note.Tone.F));
        });
    }

    void messageAllExcept(Player player, TextComponent ... messages) {
        ArrayList<Player> players = new ArrayList<>();
        players.addAll(this.playersFor);
        players.addAll(this.playersAgainst);
        players.addAll(this.playersNeutral);
        players.addAll(this.voting);

        players.remove(player);

        players.forEach(playerMsg -> {
            playerMsg.spigot().sendMessage(messages);
            playerMsg.playNote(playerMsg.getLocation(), Instrument.IRON_XYLOPHONE, Note.sharp(2, Note.Tone.F));
        });
    }
}