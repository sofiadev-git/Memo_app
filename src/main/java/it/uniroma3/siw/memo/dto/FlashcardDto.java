package it.uniroma3.siw.memo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record FlashcardDto(
        Long id,

        @PositiveOrZero
        int position,

        @NotBlank
        @Size(max = 2000)
        String frontText,

        @Size(max = 1000)
        String frontDescription,

        @NotBlank
        @Size(max = 2000)
        String backText,

        @Size(max = 1000)
        String backDescription
) {
}
