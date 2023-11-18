package com.github.loafabreadly.utils;

import java.io.File;

public class BotFile extends File {
    public BotFile(String pathname) {
        super("dbfiles/" + pathname);
    }
}
