package in.thewire.linuxterminal;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by linux on 4:21 PM.
 */

class WireParser {
    private static final String ns = null;

    // We don't use namespaces
    private int slider = 0;

    public List<Entry> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }


    private List<Entry> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Entry> entries = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, "channel");
        while (parser.next() != XmlPullParser.END_TAG) {

            if (parser.getEventType() != XmlPullParser.START_TAG) {

                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("item")) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "item");
        String title = null;
        String summary = null;
        String link = null;
        String image = null;
        String type = null;
        String pubd = null;
        String author = null;
        String category = "sss";
        while (parser.next() != XmlPullParser.END_TAG) {

            String g2 = parser.getName();
            //Log.d("Eventtype",g2+parser.getEventType()+" aaa"+ XmlPullParser.START_TAG);
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                String g = parser.getName();
                // Log.d("Event",g+parser.getEventType()+ XmlPullParser.START_TAG);

                continue;
            }
            // parser.nextTag();
            String name = parser.getName();


            switch (name) {
                case "title":
                    title = readText(parser);
                    //Log.d("text",readText(parser));

                    //title = readTitle(parser);
                    break;
                case "content:encoded":
                    //summary = readSummary(parser);
                    //Log.d("text",readText(parser));

                    summary = readText(parser);
                    image = findImage(summary);


                    break;
                case "link":
                    //link = readLink(parser);
                    //Log.d("text",readText(parser));

                    link = readText(parser);
                    if (slider < 5) {
                        type = "slider";
                        slider++;
                    } else {
                        type = "two_way";
                    }
                    break;
                case "dc:creator":
                    author = readText(parser);
                    break;
                case "pubDate":
                    pubd = readText(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        // Log.d("title",summary);
        return new Entry(title, summary, link, image, type, pubd, author);
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private String findImage(String summary) {

        String id = "";
        String thubmailurl = "";
        String regex = "http(s?)://([\\w-]+\\.)+[\\w-]+(/[\\w- ./]*)+\\.(?:[gG][iI][fF]|[jJ][pP][gG]|[jJ][pP][eE][gG]|[pP][nN][gG]|[bB][mM][pP])";

        Matcher m = Pattern.compile(regex).matcher(summary);

        if (m.find()) {
            thubmailurl = m.group(0);
        }
        if (thubmailurl.equals("")) {
            Matcher matcher = Pattern.compile("<iframe[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>").matcher(summary);
            matcher.find();

            String src = null;
            try {
                src = matcher.group(1);
            } catch (IllegalStateException e) {
                System.out.println("Error " + e.getMessage());
                return null;

            }
            String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";

            Pattern compiledPattern = Pattern.compile(pattern);
            matcher = compiledPattern.matcher(src);

            if (matcher.find()) {
                id = matcher.group();
            }
            thubmailurl = "https://img.youtube.com/vi/" + id + "/maxresdefault.jpg";
        }

        return thubmailurl;
    }

    public static class Entry {
        public final String title;
        public final String link;
        public final String summary;
        public final String image;
        public final String type;
        public final String pubdate;
        public final String author;


        private Entry(String title, String summary, String link, String image,
                      String type, String pubdate, String author) {
            this.title = title;
            this.summary = summary;
            this.link = link;
            this.image = image;
            this.type = type;
            this.author = author;
            this.pubdate = pubdate;

        }

        public String getPubdate() {
            return pubdate;
        }

        public String getAuthor() {
            return author;
        }

        public String getType() {
            return type;
        }

        public String getTitle() {
            return title;
        }

        public String getImage() {
            return image;
        }

        public String getLink() {
            return link;
        }

        public String getSummary() {
            return summary;
        }
    }
}
