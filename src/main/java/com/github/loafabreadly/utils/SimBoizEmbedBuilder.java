package com.github.loafabreadly.utils;

import org.javacord.api.entity.message.embed.EmbedBuilder;

public class SimBoizEmbedBuilder extends EmbedBuilder {

    public SimBoizEmbedBuilder() {
        super();
        setAuthor(Constants.BOT_NAME);
        setThumbnail(Constants.BOT_ICON);
        setColor(Constants.SimBoiz_Blue);
    }

}


