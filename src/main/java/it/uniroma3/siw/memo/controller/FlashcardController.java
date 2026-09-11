package it.uniroma3.siw.memo.controller;

import it.uniroma3.siw.memo.dto.FlashcardDto;
import it.uniroma3.siw.memo.model.Utente;
import it.uniroma3.siw.memo.service.FlashcardService;
import it.uniroma3.siw.memo.service.UtenteService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/decks/{deckId}/flashcards")
public class FlashcardController {

    private final FlashcardService flashcardService;
    private final UtenteService utenteService;

    public FlashcardController(
            FlashcardService flashcardService,
            UtenteService utenteService) {

        this.flashcardService = flashcardService;
        this.utenteService = utenteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FlashcardDto create(
            @PathVariable Long deckId,
            @Valid @RequestBody FlashcardDto dto,
            Authentication authentication) {

        Utente user =
                utenteService.find(
                        authentication.getName());

        FlashcardDto newCard =
                new FlashcardDto(
                        null,
                        dto.position(),
                        dto.frontText(),
                        dto.frontDescription(),
                        dto.backText(),
                        dto.backDescription()
                );

        return flashcardService.save(
                deckId,
                newCard,
                user);
    }

    @PutMapping("/{id}")
    public FlashcardDto update(
            @PathVariable Long deckId,
            @PathVariable Long id,
            @Valid @RequestBody FlashcardDto dto,
            Authentication authentication) {

        Utente user =
                utenteService.find(
                        authentication.getName());

        FlashcardDto cardToUpdate =
                new FlashcardDto(
                        id,
                        dto.position(),
                        dto.frontText(),
                        dto.frontDescription(),
                        dto.backText(),
                        dto.backDescription()
                );

        return flashcardService.save(
                deckId,
                cardToUpdate,
                user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long deckId,
            @PathVariable Long id,
            Authentication authentication) {

        Utente user =
                utenteService.find(
                        authentication.getName());

        flashcardService.delete(
                deckId,
                id,
                user);
    }
}