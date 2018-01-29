package in.thewire.linuxterminal;

/**
 * Created by linux on 4:21 PM.
 */

class Modals {

    private String title;
    private String thumbnail;
    private String link;

    public Modals(String title, String link, String thumbnail) {

        this.title = title;
        this.thumbnail = thumbnail;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
