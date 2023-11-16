package com.github.loafabreadly;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.message.embed.EmbedBuilder;

public class ErrorHandler {
    private static final Logger logger = LogManager.getLogger(Main.class);
    public static EmbedBuilder embedError(Exception ex) {
        logger.error(ex.toString());
        logger.error(ex.getMessage());
        return new EmbedBuilder()
                .setAuthor(Constants.BOT_NAME)
                .setColor(Constants.ERROR_COLOR)
                .setTitle("We hit an error while replying!")
                .addField("Stack Trace", ex.getMessage())
                .addInlineField("Submit an issue!", "Submit an issue on my Github and I will take a look")
                .addInlineField("Link", "https://github.com/Loafabreadly/SimBoizBot/issues/new");
    }
}
