package com.github.loafabreadly.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.loafabreadly.ErrorHandler;
import com.github.loafabreadly.Main;
import com.github.loafabreadly.SeriesObject;
import com.github.loafabreadly.SimBoizEmbedBuilder;
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
import java.util.Iterator;
import java.util.List;

public class ModifyCmd implements Command {

    private final @NonNull Logger logger = LogManager.getLogger(Main.class.getName());
    @Override
    @HandleSlash(name = "modify",
        desc = "Modify the Track or Car list",
        global = true,
        options = {
                @Option(type = OptionType.STRING, name = "action", desc = "'add', 'delete'", required = true),
                @Option(type = OptionType.STRING, name = "series", desc = "The Series File name you are wanting to modify", required = true),
                @Option(type = OptionType.STRING, name = "object", desc = "'cars', 'tracks'", required = true),
                @Option(type = OptionType.STRING, name = "input", desc = "the race/track you are modifying", required = true)
        })
    public void run(SlashCommandCreateEvent event) {
        SlashCommandInteraction e = event.getSlashCommandInteraction();
        e.respondLater();
        InteractionFollowupMessageBuilder response = e.createFollowupMessageBuilder();

        String action = e.getArgumentStringValueByName("action").orElseThrow().toLowerCase();
        String inputNoSpace = e.getArgumentStringValueByName("input").orElseThrow().toLowerCase().replace(" ", "");
        String seriesNoSpace = e.getArgumentStringValueByName("series").orElseThrow().toLowerCase().replace(" ", "");
        String objectType = e.getArgumentStringValueByName("object").orElseThrow().toLowerCase().replace(" ", "");

        try {
            File file = new File(seriesNoSpace + ".json");

            if (file.exists()) { //Does the series we want to modify exist even?
                ObjectMapper om = new ObjectMapper();
                SeriesObject seriesObject = om.readValue(file, SeriesObject.class);

                switch (action) {
                    case "add", "a" -> {
                        if (objectType.equalsIgnoreCase("cars")) { //We want to add an item to the cars list
                            List<String> modifiedCarsList = seriesObject.getCars();
                            modifiedCarsList.add(inputNoSpace);
                            SimBoizEmbedBuilder embed = new SimBoizEmbedBuilder();
                            embed.addField("Series Modified", file.getName());
                            modifiedCarsList.forEach(item -> embed.addInlineField("Car", item.toString().toLowerCase()));
                            response.addEmbed(embed);

                            SeriesObject modifiedSeries = new SeriesObject();
                            modifiedSeries.setTracks(seriesObject.getTracks());
                            modifiedSeries.setCars(modifiedCarsList);
                            om.writeValue(file, modifiedSeries); //Save our changes
                            response.append("Successfully saved above changes!").send().join();
                        } else if (objectType.equalsIgnoreCase("tracks")) { //We want to add an item to the tracks list
                            List<String> modifiedTracksList = seriesObject.getTracks();
                            modifiedTracksList.add(inputNoSpace);
                            SimBoizEmbedBuilder embed = new SimBoizEmbedBuilder();
                            embed.addField("Series Modified", file.getName());
                            modifiedTracksList.forEach(item -> embed.addInlineField("Track", item.toString().toLowerCase()));
                            response.addEmbed(embed);

                            SeriesObject modifiedSeries = new SeriesObject();
                            modifiedSeries.setTracks(modifiedTracksList);
                            modifiedSeries.setCars(seriesObject.getCars());
                            om.writeValue(file, modifiedSeries); //Save our changes
                            response.append("Successfully saved above changes!").send().join();
                        } else {
                            logger.error("Invalid object input received on modify command!");
                            response.append("You failed to enter `cars` or `tracks`").send().join();
                        }
                    }
                    case "delete", "d" -> {
                        if (objectType.equalsIgnoreCase("cars")) { //We want to delete an item from the Cars list
                            List<String> modifiedCarsList = seriesObject.getCars();
                            modifiedCarsList.removeIf(item -> item.equalsIgnoreCase(inputNoSpace));
                            SimBoizEmbedBuilder embed = new SimBoizEmbedBuilder();
                            embed.addField("Series Modified", file.getName());
                            modifiedCarsList.forEach(item -> embed.addInlineField("Car", item.toString().toLowerCase()));
                            response.addEmbed(embed);

                            SeriesObject modifiedSeries = new SeriesObject();
                            modifiedSeries.setCars(modifiedCarsList);
                            modifiedSeries.setTracks(seriesObject.getTracks());
                            om.writeValue(file, modifiedSeries);
                            response.append("Successfully saved above changes!").send().join();

                        } else if (objectType.equalsIgnoreCase("tracks")) { //We want to delete an item from the Tracks list
                            List<String> modifiedTracksList = seriesObject.getTracks();
                            modifiedTracksList.removeIf(item -> item.equalsIgnoreCase(inputNoSpace));
                            SimBoizEmbedBuilder embed = new SimBoizEmbedBuilder();
                            embed.addField("Series Modified", file.getName());
                            modifiedTracksList.forEach(item -> embed.addInlineField("Tracks", item.toString().toLowerCase()));
                            response.addEmbed(embed);

                            SeriesObject modifiedSeries = new SeriesObject();
                            modifiedSeries.setCars(seriesObject.getCars());
                            modifiedSeries.setTracks(modifiedTracksList);
                            om.writeValue(file, modifiedSeries);
                            response.append("Successfully saved above changes!").send().join();

                        } else {
                            logger.error("Invalid object input received on modify command!");
                            response.append("You failed to enter `cars` or `tracks`").send().join();
                            }
                    } default -> {
                        logger.error("Failed to find the action type requested!");
                        response.append("We failed to handle your action request").send().join();
                    }
                }


            } else { //The Series file does not exist
                logger.error("Failed to find the correct series file!\n" + seriesNoSpace +"\n"+ e.getUser().getName());
                response.append("A series file by that name does not exist!\n");
                response.append("You had entered: " + seriesNoSpace).send().join();
            }
        } catch (Exception ex) {
            logger.error(ex.toString());
            response.addEmbed(ErrorHandler.embedError(ex)).send().join();
        }
    }
}
