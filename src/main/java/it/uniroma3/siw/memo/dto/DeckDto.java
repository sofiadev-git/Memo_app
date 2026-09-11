package it.uniroma3.siw.memo.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.List;

public record DeckDto( //usato nella visualizzazione del singolo deck
        Long id,

        @NotBlank
        String name,

        @NotBlank
        String category,

        int likes,
        LocalDateTime createdAt,
        String authorUsername,
        List<FlashcardDto> flashcards,
        boolean liked,
        boolean bookmarked
) {
}
