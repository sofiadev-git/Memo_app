package it.uniroma3.siw.memo.dto;

public record DeckSummaryDto(  //usato solo per l'elenco dei deck
        Long id,
        String name,
        String category,
        int likes,
        boolean liked
) {
}
