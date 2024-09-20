package com.ms24053396.emanime;

import java.util.List;

public class User {
    private String username;
    private String password;
    private String type = "standard";
    private String dp;
    private List<String> anime;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getAnime() {
        return anime;
    }

    public void setAnime(List<String> anime) { this.anime = anime; }

    public String getDp() { return dp; }

    public void setDp(String dp) {  this.dp = dp; }

}
