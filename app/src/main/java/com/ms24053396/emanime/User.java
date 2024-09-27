package com.ms24053396.emanime;

import java.util.List;

public class User {
    private String username;
    private String password;
    private String type = "standard";
    private String dp;
    private List<String> anime;
    private List<String> completed;
    private List<String> onHold;
    private List<String> watching;
    private List<String> dropped;
    private List<String> planToWatch;

    public List<String> getCompleted() {
        return completed;
    }

    public void setCompleted(List<String> completed) {
        this.completed = completed;
    }

    public List<String> getOnHold() {
        return onHold;
    }

    public void setOnHold(List<String> onHold) {
        this.onHold = onHold;
    }

    public List<String> getWatching() {
        return watching;
    }

    public void setWatching(List<String> watching) {
        this.watching = watching;
    }

    public List<String> getDropped() {
        return dropped;
    }

    public void setDropped(List<String> dropped) {
        this.dropped = dropped;
    }

    public List<String> getPlanToWatch() {
        return planToWatch;
    }

    public void setPlanToWatch(List<String> planToWatch) {
        this.planToWatch = planToWatch;
    }


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
