package com.github.yukihane.actions;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public class App {

    public static void main(final String[] args)
        throws IllegalArgumentException, MalformedURLException, FeedException, IOException {
        new App().exec();
    }

    public void exec() throws IllegalArgumentException, MalformedURLException, FeedException, IOException {

        // https://github.com/rometools/rome を利用してfeedからタイトル取得
        final String url = "https://ja.stackoverflow.com/feeds";
        final SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(url)));
        final String title = feed.getTitle();
        final List<String> entries = feed.getEntries().stream()
            .map(SyndEntry::getTitle).collect(Collectors.toList());

        final Context ctx = new Context();
        ctx.setVariable("title", title);
        ctx.setVariable("entries", entries);
        ctx.setVariable("currentDateTime", OffsetDateTime.now(ZoneOffset.ofHours(9)));

        final TemplateEngine templateEngine = new TemplateEngine();
        templateEngine
            .setTemplateResolver(new ClassLoaderTemplateResolver(Thread.currentThread().getContextClassLoader()));
        final Path publicDir = Paths.get("public");
        Files.createDirectories(publicDir);
        final Path file = Paths.get("index.html");

        try (BufferedWriter writer = Files.newBufferedWriter(publicDir.resolve(file), StandardCharsets.UTF_8)) {
            templateEngine.process("template.html", ctx, writer);
        }
    }
}
