package com.dianxin.core.api.config;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions;

import java.io.*;
import java.util.*;

public class YamlConfiguration extends MemoryConfiguration implements FileConfiguration {

    private final Yaml yaml;
    private String defaultResourceName;

    private File file;

    public YamlConfiguration() {
        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        this.yaml = new Yaml(options);
    }

    public YamlConfiguration(String defaultResourceName) {
        this();
        this.defaultResourceName = defaultResourceName;
    }

    // LOAD
    @Override
    public void load(File file) throws IOException {
        this.file = file;

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
            return;
        }

        try (InputStream input = new FileInputStream(file)) {
            Object loaded = yaml.load(input);

            if (loaded instanceof Map<?, ?> map)
                this.map = convertToMap(map);
        }
    }

    @Override
    public void load(String file) throws IOException {
        load(new File(file));
    }

    @Override
    public void saveDefaultConfig() {
        if (file == null) {
            throw new IllegalStateException("No config file specified.");
        }

        if (file.exists()) return;

        try (InputStream input = getClass().getClassLoader().getResourceAsStream(defaultResourceName)) {
            if (input == null) {
                throw new RuntimeException("Default resource not found: " + defaultResourceName);
            }

            file.getParentFile().mkdirs();

            try (OutputStream output = new FileOutputStream(file)) {
                input.transferTo(output);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // SAVE
    @Override
    public void save(File file) throws IOException {
        try (Writer writer = new FileWriter(file)) {
            yaml.dump(map, writer);
        }
    }

    @Override
    public void save(String file) throws IOException {
        save(new File(file));
    }

    @Override
    public void saveConfig() {
        try {
            if (file != null) save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reloadConfig() {
        try {
            if (file != null) load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, Object> convertToMap(Map<?, ?> input) {
        Map<String, Object> result = new LinkedHashMap<>();

        input.forEach((k, v) -> {
            String key = String.valueOf(k);

            if (v instanceof Map<?, ?> child) {
                result.put(key, convertToMap(child));
            } else {
                result.put(key, v);
            }
        });

        return result;
    }
}
