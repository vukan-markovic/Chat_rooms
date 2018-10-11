package vukan.com.chatRooms;

/**
 * <h1>Message</h1>
 *
 * <p><b>Message</b> class represent chat message stored in Firebase Realtime database.</p>
 */
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

    /**
     * This method return content of the chat message.
     *
     * @return String which represent content of the chat message.
     */
    public String getText() {
        return text;
    }

    /**
     * This method set content of the message.
     *
     * @param text content of the message.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * This method return name of the author of the chat message.
     *
     * @return String which represent user name.
     */
    public String getName() {
        return name;
    }

    /**
     * This method set the name of the author of the chat message.
     *
     * @param name represent name of the author of the chat message.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This method return date and time when chat message was sent.
     *
     * @return String which represent date and time when chat message was sent.
     */
    public String getDateTime() {
        return dateTime;
    }

    /**
     * This method set date and time when chat message is send by the user.
     *
     * @param dateTime represent date and time when user send the chat message.
     */
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * This method return url of the profile image of the sender of the chat message which is obtained by provider with which user is logged in (Google, Facebook or Twitter).
     *
     * @return String represent url of profile picture of the user.
     */
    public String getProfileUrl() {
        return profileUrl;
    }

    /**
     * This method set url of the user profile picture.
     *
     * @param profileUrl represent url of user profile picture.
     */
    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}