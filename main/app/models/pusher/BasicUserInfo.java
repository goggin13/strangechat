package models.pusher;

public class BasicUserInfo {

    private String name;
    private String avatar;
    private long user_id;
    private String session;
    
    public BasicUserInfo() {
    }

    public BasicUserInfo(long user_id, String name, String avatar, String session) {
        this.name = name;
        this.user_id = user_id;
        this.avatar = avatar;
        this.session = session;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSession() {
        return this.session;
    }

    public void setSession(String s) {
        this.session = s;
    }
    
    public void setAvatar (String v) {
        this.avatar = v;
    }
    
    public String getAvatar () {
        return this.avatar;
    }
    
    public void setUser_id (long v) {
        this.user_id = v;
    }
    
    public long getUser_id () {
        return this.user_id;
    }
}
