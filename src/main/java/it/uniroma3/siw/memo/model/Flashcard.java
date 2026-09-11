package it.uniroma3.siw.memo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Flashcard {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String frontText;

    @Column(length = 1000)
    private String frontDescription;

    @Column(nullable = false, length = 2000)
    private String backText;

    @Column(length = 1000)
    private String backDescription;

    @Column(name = "position_index", nullable = false)
    private int position;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id", nullable = false)
    private Deck deckFlashcard;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFrontText() {
        return frontText;
    }

    public void setFrontText(String frontText) {
        this.frontText = frontText;
    }

    public String getFrontDescription() {
        return frontDescription;
    }

    public void setFrontDescription(String frontDescription) {
        this.frontDescription = frontDescription;
    }

    public String getBackText() {
        return backText;
    }

    public void setBackText(String backText) {
        this.backText = backText;
    }

    public String getBackDescription() {
        return backDescription;
    }

    public void setBackDescription(String backDescription) {
        this.backDescription = backDescription;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Deck getDeckFlashcard() {
        return deckFlashcard;
    }

    public void setDeckFlashcard(Deck deckFlashcard) {
        this.deckFlashcard = deckFlashcard;
    }
}
