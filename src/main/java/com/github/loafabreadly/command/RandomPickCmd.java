package com.github.loafabreadly.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.loafabreadly.utils.BotSeriesFile;
import com.github.loafabreadly.utils.ErrorHandler;
import com.github.loafabreadly.Main;
import com.github.loafabreadly.structures.SeriesObject;
import com.github.loafabreadly.utils.SimBoizEmbedBuilder;
import com.github.loafabreadly.utils.BotFile;
import lombok.NonNull;
import me.koply.kcommando.internal.OptionType;
import me.koply.kcommando.internal.annotations.HandleSlash;
import me.koply.kcommando.internal.annotations.Option;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionFollowupMessageBuilder;

import java.util.Random;

public class RandomPickCmd implements Command {
    private final @NonNull Logger logger = LogManager.getLogger(Main.class.getName());

    @Override
    @HandleSlash(name = "randompick",
            desc = "Pick a random Car x Track combo from a Series!",
            global = true,
            options = @Option(type = OptionType.STRING, name = "series", desc = "The series you want to pick from", required = true)
    )
    public void run(SlashCommandCreateEvent event) {

        SlashCommandInteraction e = event.getSlashCommandInteraction();
        e.respondLater();
        InteractionFollowupMessageBuilder response = e.createFollowupMessageBuilder();
        String seriesNoSpace = e.getArgumentStringValueByName("series").orElseThrow().toLowerCase().replace(" ", "");

        try {
            if (!new BotSeriesFile(seriesNoSpace + ".json").exists()) {
                response.append("This series file does not exist!").send().join();
                return;
            } else {
                BotSeriesFile file = new BotSeriesFile(seriesNoSpace +".json");
                ObjectMapper om = new ObjectMapper();
                SeriesObject seriesObject = om.readValue(file, SeriesObject.class);
                Random r = new Random();
                int carsIndex = r.nextInt(seriesObject.getCars().size());
                int trackIndex = r.nextInt(seriesObject.getTracks().size());
                response.addEmbed(new SimBoizEmbedBuilder()
                        .addField("Series chosen", file.getName())
                        .addField("Random Car", seriesObject.getCars().get(carsIndex))
                        .addField("Random Track", seriesObject.getTracks().get(trackIndex))).send().join();
            }
        }catch (Exception ex) {
            logger.error(ex.toString());
            response.addEmbed(ErrorHandler.embedError(ex)).send().join();
        }
    }
}
