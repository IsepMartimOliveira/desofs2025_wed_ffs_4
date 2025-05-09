package com.example.psoft_22_23_project.plansmanagement.model;


import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Embeddable
@Data
public class MusicCollection {
    @Column(name = "Music_Collection")
    @NotNull
    @Min(0)
    private Integer musicCollection;

    public void setMusicCollection(Integer musicCollection) {
        if (musicCollection == null || musicCollection < 0) {
            throw new IllegalArgumentException("Music Collection number must be positive");
        }
        this.musicCollection = musicCollection;
    }
}
