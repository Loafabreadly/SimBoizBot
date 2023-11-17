package com.github.loafabreadly.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.loafabreadly.utils.BotSeriesFile;
import com.github.loafabreadly.utils.ErrorHandler;
import com.github.loafabreadly.Main;
import com.github.loafabreadly.structures.SeriesObject;
import lombok.NonNull;
import me.koply.kcommando.internal.OptionType;
import me.koply.kcommando.internal.annotations.HandleSlash;
import me.koply.kcommando.internal.annotations.Option;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionFollowupMessageBuilder;

import java.io.File;
import java.util.ArrayList;

public class CreateSeriesCmd implements Command {

    private final @NonNull Logger logger = LogManager.getLogger(Main.class.getName());

    @Override
    @HandleSlash(name = "create",
    desc = "create a Series file",
    global = true,
    options = @Option(type = OptionType.STRING, name = "series",desc = "Series Name (no spaces)", required = true))
    public void run(SlashCommandCreateEvent event) {

        SlashCommandInteraction e = event.getSlashCommandInteraction();
        e.respondLater();
        InteractionFollowupMessageBuilder response = e.createFollowupMessageBuilder();

        try {
            String optionWithoutSpace = e.getArgumentStringValueByName("series").orElseThrow().toLowerCase().replace(" ", "");
            logger.trace(optionWithoutSpace);
            BotSeriesFile seriesFile = new BotSeriesFile(optionWithoutSpace + ".json");

            if (seriesFile.exists()) {
                response.append("This series file name already exists!").send().join();
            } else {
                SeriesObject seriesObject = new SeriesObject();
                seriesObject.setCars(new ArrayList<>());
                seriesObject.setTracks(new ArrayList<>());

                ObjectMapper om = new ObjectMapper();
                om.writeValue(seriesFile, seriesObject);

                logger.info("Created a Series file for " + optionWithoutSpace);
                response.append("Successfully created a blank series file for " + optionWithoutSpace + "!").send().join();
            }

        } catch (Exception ex) {
            logger.error(ex.toString());
            response.addEmbed(ErrorHandler.embedError(ex)).send().join();
        }
    }
}
