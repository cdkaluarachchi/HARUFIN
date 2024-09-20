package com.ms24053396.emanime;

import java.util.Base64;
import java.util.List;

class Episode {
    String episodeName;
    String episodeNumber;
    String episodeURL;

    public String getEpisodeURL() {
        return episodeURL;
    }

    public void setEpisodeURL(String episodeURL) {
        this.episodeURL = episodeURL;
    }

    public String getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(String episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public String getEpisodeName() {
        return episodeName;
    }

    public void setEpisodeName(String episodeName) {
        this.episodeName = episodeName;
    }
}

public class Anime {

    String animeID;
    String name;
    Integer episodeCount = 0;
    List<Episode> episodes;
    Float rating;
    String image;
    String description;

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

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
