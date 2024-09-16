package com.ms24053396.emanime;

import java.util.List;

public class Anime {

    String animeID;
    String name;
    Integer episodeCount = 0;
    List<String> episodes;
    Float rating;

    public String getAnimeID() {
        return animeID;
    }

    public void setAnimeID(String animeID) {
        this.animeID = animeID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getEpisodeCount() {
        return episodeCount;
    }

    public void setEpisodeCount(Integer episodeCount) {
        this.episodeCount = episodeCount;
    }

    public List<String> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<String> episodes) {
        this.episodes = episodes;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }
}
