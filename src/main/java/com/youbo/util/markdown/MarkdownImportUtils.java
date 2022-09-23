package com.youbo.util.markdown;

import com.vladsch.flexmark.ext.attributes.AttributesExtension;
import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.emoji.EmojiExtension;
import com.vladsch.flexmark.ext.emoji.EmojiImageType;
import com.vladsch.flexmark.ext.emoji.EmojiShortcutType;
import com.vladsch.flexmark.ext.escaped.character.EscapedCharacterExtension;
import com.vladsch.flexmark.ext.footnotes.FootnoteExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.gitlab.GitLabExtension;
import com.vladsch.flexmark.ext.ins.InsExtension;
import com.vladsch.flexmark.ext.media.tags.MediaTagsExtension;
import com.vladsch.flexmark.ext.superscript.SuperscriptExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.toc.TocExtension;
import com.vladsch.flexmark.ext.yaml.front.matter.AbstractYamlFrontMatterVisitor;
import com.vladsch.flexmark.ext.yaml.front.matter.YamlFrontMatterExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.youbo.constant.HaloConst;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 一句话注释
 *
 * @author Administrator
 * @date 2022/9/19
 */
public class MarkdownImportUtils {

    private static final DataHolder OPTIONS =
            new MutableDataSet().set(Parser.EXTENSIONS, Arrays.asList(AttributesExtension.create(),
                            AutolinkExtension.create(),
                            EmojiExtension.create(),
                            EscapedCharacterExtension.create(),
                            StrikethroughExtension.create(),
                            TaskListExtension.create(),
                            InsExtension.create(),
                            MediaTagsExtension.create(),
                            com.vladsch.flexmark.ext.tables.TablesExtension.create(),
                            TocExtension.create(),
                            SuperscriptExtension.create(),
                            YamlFrontMatterExtension.create(),
                            FootnoteExtension.create(),
                            GitLabExtension.create()))
                    .set(TocExtension.LEVELS, 255)
                    .set(com.vladsch.flexmark.ext.tables.TablesExtension.WITH_CAPTION, false)
                    .set(com.vladsch.flexmark.ext.tables.TablesExtension.COLUMN_SPANS, false)
                    .set(com.vladsch.flexmark.ext.tables.TablesExtension.MIN_SEPARATOR_DASHES, 1)
                    .set(com.vladsch.flexmark.ext.tables.TablesExtension.MIN_HEADER_ROWS, 1)
                    .set(com.vladsch.flexmark.ext.tables.TablesExtension.MAX_HEADER_ROWS, 1)
                    .set(com.vladsch.flexmark.ext.tables.TablesExtension.APPEND_MISSING_COLUMNS, true)
                    .set(com.vladsch.flexmark.ext.tables.TablesExtension.DISCARD_EXTRA_COLUMNS, true)
                    .set(TablesExtension.HEADER_SEPARATOR_COLUMN_MATCH, true)
                    .set(EmojiExtension.USE_SHORTCUT_TYPE, EmojiShortcutType.EMOJI_CHEAT_SHEET)
                    .set(EmojiExtension.USE_IMAGE_TYPE, EmojiImageType.UNICODE_ONLY)
                    .set(HtmlRenderer.SOFT_BREAK, "<br />\n")
                    .set(FootnoteExtension.FOOTNOTE_BACK_REF_STRING, "↩︎");

    private static final Parser PARSER = Parser.builder(OPTIONS).build();

    private static final HtmlRenderer RENDERER = HtmlRenderer.builder(OPTIONS).build();
    private static final Pattern FRONT_MATTER = Pattern.compile("^(---)?[\\s\\S]*?---");
    private static final Pattern TABLE = Pattern.compile("\\|\\s*:?---");

    //    /**
    //     * Render html document to markdown document.
    //     *
    //     * @param html html document
    //     * @return markdown document
    //     */
    //    public static String renderMarkdown(String html) {
    //        return FlexmarkHtmlParser.parse(html);
    //    }

    /**
     * Render Markdown content
     *
     * @param markdown content
     * @return String
     */
    public static String renderHtml(String markdown) {
        if (StringUtils.isBlank(markdown)) {
            return StringUtils.EMPTY;
        }

        // Render netease music short url.
        if (markdown.contains(HaloConst.NETEASE_MUSIC_PREFIX)) {
            markdown = markdown
                    .replaceAll(HaloConst.NETEASE_MUSIC_REG_PATTERN, HaloConst.NETEASE_MUSIC_IFRAME);
        }

        // Render bilibili video short url.
        if (markdown.contains(HaloConst.BILIBILI_VIDEO_PREFIX)) {
            markdown = markdown
                    .replaceAll(HaloConst.BILIBILI_VIDEO_REG_PATTERN, HaloConst.BILIBILI_VIDEO_IFRAME);
        }

        // Render youtube video short url.
        if (markdown.contains(HaloConst.YOUTUBE_VIDEO_PREFIX)) {
            markdown = markdown
                    .replaceAll(HaloConst.YOUTUBE_VIDEO_REG_PATTERN, HaloConst.YOUTUBE_VIDEO_IFRAME);
        }

        com.vladsch.flexmark.util.ast.Node document = PARSER.parse(markdown);

        return RENDERER.render(document);
    }

    /**
     * Get front-matter
     *
     * @param markdown markdown
     * @return Map
     */
    public static Map<String, List<String>> getFrontMatter(String markdown) {
        markdown = markdown.trim();
        Matcher matcher = FRONT_MATTER.matcher(markdown);
        if (matcher.find()) {
            markdown = matcher.group();
        }
        markdown = Arrays.stream(markdown.split("\\r?\\n")).map(row -> {
            if (row.startsWith("- ")) {
                return " " + row;
            } else {
                return row;
            }
        }).collect(Collectors.joining("\n"));
        if (!markdown.startsWith("---\n")) {
            markdown = "---\n" + markdown;
        }
        AbstractYamlFrontMatterVisitor visitor = new AbstractYamlFrontMatterVisitor();
        Node document = PARSER.parse(markdown);
        visitor.visit(document);
        return visitor.getData();
    }

    /**
     * remove front matter
     *
     * @param markdown markdown
     * @return markdown
     */
    public static String removeFrontMatter(String markdown) {
        markdown = markdown.trim();
        Matcher matcher = FRONT_MATTER.matcher(markdown);
        // if has '| ---' or '| :---' return
        if (matcher.find() && !TABLE.matcher(matcher.group()).find()) {
            return markdown.replace(matcher.group(), "");
        }
        return markdown;
    }
}
