package com.example.psoft_22_23_project.plansmanagement.model;


import lombok.Data;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;


@Embeddable
@Data
public class MusicSuggestion {
    @Column(name = "Music_Suggestion")
    @NotNull
    @Pattern(regexp = "(automatic|personalized)")
    private String musicSuggestion;

    public void setMusicSuggestion(String musicSuggestion) {
        if (musicSuggestion.equals("automatic") || musicSuggestion.equals("personalized")){
            this.musicSuggestion = musicSuggestion;

        }else throw new IllegalArgumentException("Music Suggestion can only be 'personalized' or 'automatic' ");

    }
}
