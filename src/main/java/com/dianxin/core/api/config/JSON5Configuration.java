package com.dianxin.core.api.config;

import com.dianxin.core.api.config.yaml.FileConfiguration;
import com.dianxin.core.api.config.yaml.MemorySection;
import org.apache.juneau.marshaller.Json5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Json5Configuration là class quản lý config JSON5.
 * Hỗ trợ load, save, reload và save default từ resource.
 * @deprecated
 */
@Deprecated
public class JSON5Configuration implements FileConfiguration {

    private final Logger logger = LoggerFactory.getLogger(JSON5Configuration.class);

    private File file;
    private String defaultResourceName;
    private Map<String, Object> map = new LinkedHashMap<>();

    public JSON5Configuration() { }

    public JSON5Configuration(String defaultResourceName) {
        this.defaultResourceName = defaultResourceName;
    }

    public JSON5Configuration(String defaultResourceName, String filePath) {
        this.defaultResourceName = defaultResourceName;
        this.file = new File(filePath);
    }

    // ================= LOAD =================
    @Override
    public void load(File file) throws IOException {
        this.file = file;

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
            return;
        }

        try (InputStream in = new FileInputStream(file)) {
            String json5 = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            Map<String, Object> loaded = Json5.to(json5, Map.class);
            if (loaded != null) this.map = loaded;
        }
    }

    @Override
    public void load(String file) throws IOException {
        load(new File(file));
    }

    // ================= SAVE DEFAULT =================
    @Override
    public void saveDefaultConfig() {
        if (file == null)
            throw new IllegalStateException("No config file specified.");

        if (file.exists()) return;

        try (InputStream input = getClass().getClassLoader().getResourceAsStream(defaultResourceName)) {
            if (input == null)
                throw new RuntimeException("Default resource not found: " + defaultResourceName);

            file.getParentFile().mkdirs();
            try (OutputStream output = new FileOutputStream(file)) {
                input.transferTo(output);
            }
        } catch (IOException e) {
            logger.error("Không thể lưu default config: {}", defaultResourceName, e);
        }
    }

    // ================= SAVE =================
    @Override
    public void save(File file) throws IOException {
        try (Writer writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write(Json5.of(map).build());
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
            logger.error("Lỗi khi save config: {}", file, e);
        }
    }

    @Override
    public void reloadConfig() {
        try {
            if (file != null) load(file);
        } catch (IOException e) {
            logger.error("Lỗi khi reload config: {}", file, e);
        }
    }

    // ================= GETTERS =================
    @Override
    public String getString(String path) {
        Object o = map.get(path);
        return o != null ? o.toString() : null;
    }

    @Override
    public String getString(String path, String def) {
        return getString(path) != null ? getString(path) : def;
    }

    @Override
    public int getInt(String path) {
        Object o = map.get(path);
        if (o instanceof Number n) return n.intValue();
        try { return Integer.parseInt(String.valueOf(o)); } catch(Exception e) { return 0; }
    }

    @Override
    public int getInt(String path, int def) {
        int v = getInt(path);
        return v != 0 ? v : def;
    }

    @Override
    public boolean getBoolean(String path) {
        Object o = map.get(path);
        if (o instanceof Boolean b) return b;
        return Boolean.parseBoolean(String.valueOf(o));
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        Object o = map.get(path);
        return o != null ? getBoolean(path) : def;
    }

    @Override
    public double getDouble(String path) {
        Object o = map.get(path);
        if (o instanceof Number n) return n.doubleValue();
        try { return Double.parseDouble(String.valueOf(o)); } catch(Exception e) { return 0; }
    }

    @Override
    public double getDouble(String path, double def) {
        double v = getDouble(path);
        return v != 0 ? v : def;
    }

    @Override
    public long getLong(String path) {
        Object o = map.get(path);
        if (o instanceof Number n) return n.longValue();
        try { return Long.parseLong(String.valueOf(o)); } catch(Exception e) { return 0; }
    }

    @Override
    public long getLong(String path, long def) {
        long v = getLong(path);
        return v != 0 ? v : def;
    }

    @Override
    public java.util.List<String> getStringList(String path) {
        Object o = map.get(path);
        if (o instanceof java.util.List<?> list) {
            return list.stream().map(Object::toString).toList();
        }
        return java.util.Collections.emptyList();
    }

    @Override
    public java.util.List<?> getList(String path) {
        Object o = map.get(path);
        if (o instanceof java.util.List<?> list) return list;
        return java.util.Collections.emptyList();
    }

    @Override
    public MemorySection getSection(String path) {
        Object o = map.get(path);
        if (o instanceof Map<?, ?> m) {
            MemorySection sec = new MemorySection(null, path);
            sec.map.putAll((Map<String, Object>) m);
            return sec;
        }
        return null;
    }

    @Override
    public boolean contains(String path) {
        return map.containsKey(path);
    }

    @Override
    public boolean contains(String path, boolean ignoreDefault) {
        return contains(path);
    }

    @Override
    public boolean isSet(String path) {
        return contains(path);
    }

    @Override
    public Set<String> getKeys(boolean deep) {
        return map.keySet();
    }

    @Override
    public Map<String, Object> getValues(boolean deep) {
        return map;
    }
}
