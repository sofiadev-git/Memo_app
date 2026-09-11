package it.uniroma3.siw.memo.service;

import it.uniroma3.siw.memo.dto.FlashcardDto;
import it.uniroma3.siw.memo.exception.OperationNotAllowedException;
import it.uniroma3.siw.memo.exception.ResourceNotFoundException;
import it.uniroma3.siw.memo.model.Deck;
import it.uniroma3.siw.memo.model.Flashcard;
import it.uniroma3.siw.memo.model.Utente;
import it.uniroma3.siw.memo.repository.DeckRepository;
import it.uniroma3.siw.memo.repository.FlashcardRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FlashcardService {

    private final FlashcardRepository flashcardRepository;
    private final DeckRepository deckRepository;

    public FlashcardService(FlashcardRepository flashcardRepository,
                            DeckRepository deckRepository) {
        this.flashcardRepository = flashcardRepository;
        this.deckRepository = deckRepository;
    }

    @Transactional
    public FlashcardDto save(Long deckId,
                             FlashcardDto dto,
                             Utente user) {

        Deck deck = deckRepository.findDetailedById(deckId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Mazzo non trovato"));

        checkOwner(deck, user);

        Flashcard card;

        if (dto.id() == null) {

            card = new Flashcard();
            card.setDeckFlashcard(deck);

        } else {

            card = flashcardRepository.findById(dto.id())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Flashcard non trovata"));

            if (!card.getDeckFlashcard()
                    .getId()
                    .equals(deckId)) {

                throw new OperationNotAllowedException(
                        "La flashcard non appartiene a questo mazzo");
            }
        }

        card.setFrontText(dto.frontText().trim());
        card.setFrontDescription(
                clean(dto.frontDescription()));

        card.setBackText(dto.backText().trim());
        card.setBackDescription(
                clean(dto.backDescription()));

        card.setPosition(dto.position());

        return toDto(
                flashcardRepository.save(card));
    }

    @Transactional
    public void delete(Long deckId,
                       Long id,
                       Utente user) {

        Flashcard card = flashcardRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Flashcard non trovata"));

        if (!card.getDeckFlashcard()
                .getId()
                .equals(deckId)) {

            throw new OperationNotAllowedException(
                    "La flashcard non appartiene a questo mazzo");
        }

        checkOwner(
                card.getDeckFlashcard(),
                user);

        flashcardRepository.delete(card);
    }

    private void checkOwner(Deck deck,
                            Utente user) {

        if (!deck.getAutore()
                .getId()
                .equals(user.getId())) {

            throw new OperationNotAllowedException(
                    "Puoi modificare solo le flashcard dei tuoi mazzi");
        }
    }

    private String clean(String value) {

        return value == null || value.isBlank()
                ? null
                : value.trim();
    }

    public FlashcardDto toDto(Flashcard card) {

        return new FlashcardDto(
                card.getId(),
                card.getPosition(),
                card.getFrontText(),
                card.getFrontDescription(),
                card.getBackText(),
                card.getBackDescription()
        );
    }
}