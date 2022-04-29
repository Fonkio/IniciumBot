package fr.fonkio.command.impl;

import fr.fonkio.command.AbstractCommand;
import fr.fonkio.inicium.Inicium;
import fr.fonkio.message.EmbedGenerator;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;


public class CommandPlaySkip extends AbstractCommand {

    public CommandPlaySkip() {
        blacklistable = true;
    }

    @Override
    public boolean run(SlashCommandInteractionEvent eventSlash, ButtonInteractionEvent eventButton) {
        if (eventSlash == null) {
            return false;
        }

        User user = eventSlash.getUser();
        Guild guild = eventSlash.getGuild();

        if (guild != null) {
            if (canNotSendCommand(user, guild, eventSlash)) {
                return true;
            }

            Inicium.manager.getPlayer(guild).skipTrack();
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
                            Inicium.manager.getPlayer(guild).getPlayerMessage().newMessage("▶ ⏭ PlaySkip","Tu dois être co sur un channel vocal pour demander ça.", user, false, eventSlash);
                        }
                        try {
                            guild.getAudioManager().openAudioConnection(voiceChannel);
                            guild.getAudioManager().setSelfDeafened(true);
                        } catch (InsufficientPermissionException e){
                            Inicium.manager.getPlayer(guild).getPlayerMessage().newMessage("▶ ⏭ PlaySkip","Je n'ai pas la permission de rejoindre ce channel !", user, false, eventSlash);
                        }

                    } else {
                        if (voiceChannel == null) {
                            Inicium.manager.getPlayer(guild).getPlayerMessage().newMessage("▶ ⏭ PlaySkip","Tu dois être co sur un channel vocal pour demander ça.", user, false, eventSlash);
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
                                        eventSlash.replyEmbeds(EmbedGenerator.generate(user, "\uD83D\uDEAB Permission", "Je n'ai pas la permission de rejoindre ce channel !")).setEphemeral(true).queue();
                                    }
                                }
                            }
                        }
                    }
                    Inicium.manager.loadTrack(guild, youtubeSearch.searchOrUrl(musiqueParameter), user, eventSlash);

                }
            }
        }
        return false;
    }
}