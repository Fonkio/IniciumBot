package fr.fonkio.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.fonkio.message.MusicPlayer;
import fr.fonkio.message.StringsConst;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.util.HashMap;
import java.util.Map;

public class MusicManager {

    private final AudioPlayerManager manager = new DefaultAudioPlayerManager();
    private final Map<String, MusicPlayer> players = new HashMap<>();

    public MusicManager() {
        AudioSourceManagers.registerRemoteSources(manager);
        AudioSourceManagers.registerLocalSource(manager);
    }

    public synchronized MusicPlayer getPlayer (Guild guild) {
        if (!players.containsKey(guild.getId())) {
            players.put(guild.getId(), new MusicPlayer(manager.createPlayer(), guild));
        }
        return players.get(guild.getId());
    }


    public void loadTrack (final Guild guild, final String source, User author, InteractionHook hook) {
        loadTrack(guild, source, author, hook, true);
    }
    public void loadTrack (final Guild guild, final String source, User author, InteractionHook hook, boolean message) {
        MusicPlayer player = getPlayer(guild);
        guild.getAudioManager().setSendingHandler(player.getAudioHandler());
        player.getPlayerMessage().updatePlayerMessage("\uD83D\uDD03 " + StringsConst.MESSAGE_LOADING_TITLE, StringsConst.MESSAGE_LOADING, author, true, hook);
        manager.loadItemOrdered(player, source, new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack track) {
                player.playTrack(track);
                if (message) {
                    player.getPlayerMessage().updatePlayerMessage(StringsConst.COMMAND_PLAY_TITLE,
                            "\uD83D\uDCBF\u200B "+ StringsConst.MESSAGE_ADDING_TRACK +" \uD83D\uDCBF\u200B" +
                                    "\n\uD83D\uDD17\u200B " + track.getInfo().uri +
                                    "\n\uD83C\uDFB5\u200B " + track.getInfo().title +
                                    "\n\uD83C\uDF99️\u200B " + track.getInfo().author
                            , author, true, hook);
                }
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                for(int i = 0; i < playlist.getTracks().size(); i++) {
                    AudioTrack track = playlist.getTracks().get(i);
                    player.playTrack(track);
                }
                if (message) {
                    player.getPlayerMessage().updatePlayerMessage(StringsConst.COMMAND_PLAY_TITLE, StringsConst.MESSAGE_ADDING_PLAYLIST + "**" + playlist.getName() + "**\n",author, true, hook);
                }
            }

            @Override
            public void noMatches() {
                if (message) {
                    player.getPlayerMessage().updatePlayerMessage(StringsConst.COMMAND_PLAY_TITLE, StringsConst.MESSAGE_THE_TRACK + source + StringsConst.MESSAGE_NOT_FOUND, author, true, hook);
                }
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                exception.printStackTrace();
                if (message) {
                    player.getPlayerMessage().updatePlayerMessage(StringsConst.COMMAND_PLAY_TITLE,
                            StringsConst.MESSAGE_CANT_PLAY_RESON + exception.getMessage() + ")\n**" + StringsConst.MESSAGE_ADD_LYRIC_TO_SEARCH + "**"
                            , author, true, hook);
                }
            }
        });
    }
}

