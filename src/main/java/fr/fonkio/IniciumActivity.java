package fr.fonkio;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.RichPresence;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IniciumActivity implements Activity {
    @Override
    public boolean isRich() {
        return true;
    }
    @Override
    public String getUrl() {
        return null;
    }
    @Override
    public @NotNull ActivityType getType() {
        return ActivityType.LISTENING;
    }
    @Override
    public Timestamps getTimestamps() {
        return new Timestamps(System.currentTimeMillis(), System.currentTimeMillis()+10000000L);
    }
    @Nullable
    @Override
    public EmojiUnion getEmoji() {
        return Emoji.fromFormatted("milky_way");
    }

    @NotNull
    @Override
    public Activity withState(@Nullable String state) {
        return null;
    }

    @Override
    public @NotNull String getName() {
        return "/play";
    }

    @Nullable
    @Override
    public String getState() {
        return null;
    }

    @Override
    public RichPresence asRichPresence() {
        return null;
    }
}
