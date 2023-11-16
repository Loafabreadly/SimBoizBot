package com.github.loafabreadly;


import com.github.loafabreadly.command.Command;
import com.github.loafabreadly.command.CreateSeriesCmd;
import com.github.loafabreadly.command.ModifyCmd;
import com.github.loafabreadly.command.RandomPickCmd;
import me.koply.kcommando.KCommando;
import me.koply.kcommando.integration.impl.javacord.JavacordIntegration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;

import java.util.Objects;

public class Main {


    private static final Logger logger = LogManager.getLogger(Main.class);


    public static void main(String[] args) {

        System.setProperty("log4j.configurationFile", "/src/main/resources/log4j2.xml");
        Constants.BOT_TOKEN = getToken();
        logger.info("Starting Bot...\n");
        logger.debug("logging in with: " + Constants.BOT_TOKEN + "\n");

        try {
            DiscordApi api = new DiscordApiBuilder()
                    .setToken(Constants.BOT_TOKEN)
                    .addIntents(Intent.MESSAGE_CONTENT)
                    .addIntents(Intent.GUILDS)
                    .login().join();

            logger.info("Setting bot status to match internal version of " + Constants.VERSION);
            api.updateActivity("v." + Constants.VERSION);

            JavacordIntegration jci = new JavacordIntegration(api);
            KCommando kc = new KCommando(jci)
                    .addPackage(Command.class.getPackageName())
                    .setReadBotMessages(false)
                    .setPrefix("/")
                    .setOwners(Constants.OWNER_ID)
                    .setVerbose(false);

            kc.registerObject(new CreateSeriesCmd());
            kc.registerObject(new ModifyCmd());
            kc.registerObject(new RandomPickCmd());
            logger.info("Successfully registered our KC commands!");

            kc.build();
        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    //TODO Add in View command
    //TODO Add in series tracker JSON file
    //TODO Add in randomizer command

    private static String getToken() {
        String token = System.getenv("DISCORD_TOKEN");
        logger.info("Env var was defined for token!");
        return Objects.requireNonNull(token);
    }
}
