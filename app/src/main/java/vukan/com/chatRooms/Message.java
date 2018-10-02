package vukan.com.chatRooms;

class Message {
    private String text, name, dateTime, profileUrl;

    public Message() {
    }

    public Message(String text, String name, String profileUrl, String dateTime) {
        this.text = text;
        this.name = name;
        this.profileUrl = profileUrl;
        this.dateTime = dateTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}