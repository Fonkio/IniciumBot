package fr.fonkio.command.impl;

import fr.fonkio.command.AbstractCommand;
import fr.fonkio.inicium.Inicium;
import fr.fonkio.message.EmbedGenerator;
import fr.fonkio.message.StringsConst;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class CommandPlay extends AbstractCommand {

    public CommandPlay() {
        blacklistable = true;
    }

    @Override
    public boolean run(SlashCommandInteractionEvent eventSlash, ButtonInteractionEvent eventButton) {
        if (eventSlash == null) {
            return false;
        }
        InteractionHook hook = eventSlash.deferReply().complete();
        User user = eventSlash.getUser();
        Guild guild = eventSlash.getGuild();
        if (guild != null) {
            if (canNotSendCommand(user, guild, hook)) {
                return true;
            }
            OptionMapping musiqueOption = eventSlash.getOption("musique");

            String musiqueParameter = null;
            if(musiqueOption != null) {
                musiqueParameter = musiqueOption.getAsString();
            }
            Member member = guild.getMember(user);
            if (member != null && musiqueParameter != null) {
                GuildVoiceState guildVoiceState = member.getVoiceState();
                if (guildVoiceState != null) {
                    AudioChannel voiceChannel = guildVoiceState.getChannel();
                    if (!guild.getAudioManager().isConnected()) {
                        if (voiceChannel == null) {
                            hook.editOriginalEmbeds(EmbedGenerator.generate(user, StringsConst.COMMAND_PLAY_TITLE, StringsConst.MESSAGE_NOT_CONNECTED)).queue();
                            return true;
                        }
                        try {
                            guild.getAudioManager().openAudioConnection(voiceChannel);
                            guild.getAudioManager().setSelfDeafened(true);
                        } catch (InsufficientPermissionException e){
                            hook.editOriginalEmbeds(EmbedGenerator.generate(user, StringsConst.COMMAND_PLAY_TITLE, StringsConst.MESSAGE_CANT_CONNECT)).queue();
                            return true;
                        }
                    } else {
                        if (voiceChannel == null) {
                            hook.editOriginalEmbeds(EmbedGenerator.generate(user, StringsConst.COMMAND_PLAY_TITLE, StringsConst.MESSAGE_NOT_CONNECTED)).queue();
                            return true;
                        }
                        // Verification que l'utilisateur soit dans le même chan
                        Member memberBot = guild.getMember(Inicium.getJda().getSelfUser());
                        if (memberBot != null) {
                            GuildVoiceState guildVoiceStateBot = memberBot.getVoiceState();
                            if (guildVoiceStateBot != null) {
                                AudioChannel audioChannel = guildVoiceStateBot.getChannel();
                                if (audioChannel != null && !voiceChannel.getId().equals(audioChannel.getId())) {
                                    try {
                                        guild.getAudioManager().openAudioConnection(voiceChannel);
                                    } catch (InsufficientPermissionException e) {
                                        hook.editOriginalEmbeds(EmbedGenerator.generate(user, StringsConst.COMMAND_PLAY_TITLE, StringsConst.MESSAGE_NO_PERMISSIONS)).queue();
                                    }
                                }
                            }
                        }
                    }
                    Inicium.manager.loadTrack(guild, youtubeSearch.searchOrUrl(musiqueParameter), user, hook);
                }
            }
        }
        return true;
    }
}
