package it.uniroma3.siw.memo.service;

import it.uniroma3.siw.memo.dto.DeckDto;
import it.uniroma3.siw.memo.dto.DeckSummaryDto;
import it.uniroma3.siw.memo.dto.FlashcardDto;
import it.uniroma3.siw.memo.exception.OperationNotAllowedException;
import it.uniroma3.siw.memo.exception.ResourceNotFoundException;
import it.uniroma3.siw.memo.model.Credentials;
import it.uniroma3.siw.memo.model.Deck;
import it.uniroma3.siw.memo.model.Utente;
import it.uniroma3.siw.memo.repository.DeckRepository;
import it.uniroma3.siw.memo.repository.UtenteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DeckService {

    private final DeckRepository deckRepository;
    private final UtenteRepository utenteRepository;
    private final FlashcardService flashcardService;

    public DeckService(DeckRepository deckRepository,
                       UtenteRepository utenteRepository,
                       FlashcardService flashcardService) {
        this.deckRepository = deckRepository;
        this.utenteRepository = utenteRepository;
        this.flashcardService = flashcardService;
    }

    @Transactional(readOnly = true)
    public List<DeckSummaryDto> top(Utente currentUser) {
        return deckRepository.findTop4ByOrderByLikesDescCreatedAtDesc().stream()
                .map(deck -> toSummaryDto(deck, currentUser))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DeckSummaryDto> recent(Utente currentUser) {
        return deckRepository.findTop8ByOrderByCreatedAtDesc().stream()
                .map(deck -> toSummaryDto(deck, currentUser))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DeckSummaryDto> search(String query, String filter, Utente currentUser) {
        String value = query == null ? "" : query.trim();

        if (value.isBlank()) {
            return List.of();
        }

        String searchFilter =
                filter == null ? "all" : filter.trim().toLowerCase();

        List<Deck> decks = switch (searchFilter) {
            case "name" ->
                    deckRepository.findByNameContainingIgnoreCaseOrderByLikesDesc(value);

            case "category" ->
                    deckRepository.findByCategoryContainingIgnoreCaseOrderByLikesDesc(value);

            default ->
                    deckRepository
                            .findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCaseOrderByLikesDesc(
                                    value,
                                    value
                            );
        };

        return decks.stream()
                .map(deck -> toSummaryDto(deck, currentUser))
                .toList();
    }

    @Transactional(readOnly = true)
    public DeckDto get(Long id, Utente currentUser) {
        return toDto(requireDetailedDeck(id), currentUser);
    }

    @Transactional(readOnly = true)
    public List<DeckSummaryDto> mine(Utente user) {
        return deckRepository.findByAutoreOrderByCreatedAtDesc(user).stream()
                .map(deck -> toSummaryDto(deck, user))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DeckSummaryDto> bookmarks(Utente user) {
        return deckRepository.findBookmarks(user.getId()).stream()
                .map(deck -> toSummaryDto(deck, user))
                .toList();
    }

    @Transactional
    public DeckDto create(DeckDto dto, Utente author) {
        Deck deck = new Deck();
        deck.setName(dto.name().trim());
        deck.setCategory(dto.category().trim());
        deck.setAutore(author);

        return toDto(deckRepository.save(deck), author);
    }

    @Transactional
    public DeckDto update(Long id, DeckDto dto, Utente user) {
        Deck deck = requireDeck(id);
        checkOwner(deck, user);

        deck.setName(dto.name().trim());
        deck.setCategory(dto.category().trim());

        return toDto(deckRepository.save(deck), user);
    }

    @Transactional
    public void delete(Long id, Utente user) {
        Deck deck = requireDeck(id);
        checkOwnerOrAdmin(deck, user);

        utenteRepository.deleteBookmarkLinks(id);
        utenteRepository.deleteLikeLinks(id);
        
        deckRepository.delete(deck);
    }

    @Transactional
    public DeckSummaryDto toggleLike(Long id, Utente user) {
        Deck deck = requireDeck(id);

        if (user.getLikedDecks().remove(deck)) {
            deck.setLikes(Math.max(0, deck.getLikes() - 1));
        } else {
            user.getLikedDecks().add(deck);
            deck.setLikes(deck.getLikes() + 1);
        }

        utenteRepository.save(user);
        deckRepository.save(deck);

        // Il like può essere premuto direttamente da una DeckCard:
        // restituiamo quindi soltanto il riepilogo, senza caricare le flashcard.
        return toSummaryDto(deck, user);
    }

    @Transactional
    public DeckDto toggleBookmark(Long id, Utente user) {
        Deck deck = requireDeck(id);

        if (deck.getAutore().getId().equals(user.getId())) {
            throw new OperationNotAllowedException("Non puoi aggiungere ai bookmark un tuo mazzo");
        }

        if (user.getBookmarkedDecks().remove(deck)) {
            // era già nei bookmark: viene rimosso
        } else {
            user.getBookmarkedDecks().add(deck);
        }

        utenteRepository.save(user);
        return toDto(deck, user);
    }

    /*
     * Recupero leggero: non inizializza le flashcard.
     * Va bene per update/delete/like e per le operazioni che non devono
     * restituire il contenuto completo del mazzo.
     */
    private Deck requireDeck(Long id) {
        return deckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mazzo non trovato"));
    }

    /* Recupero completo usato solo per la visualizzazione del singolo mazzo. */
    private Deck requireDetailedDeck(Long id) {
        return deckRepository.findDetailedById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mazzo non trovato"));
    }

    private void checkOwner(Deck deck, Utente user) {
        if (!deck.getAutore().getId().equals(user.getId())) {
            throw new OperationNotAllowedException("Puoi modificare solo i tuoi mazzi");
        }
    }

    private void checkOwnerOrAdmin(Deck deck, Utente user) {
        boolean isOwner = deck.getAutore().getId().equals(user.getId());
        boolean isAdmin = Credentials.ADMIN_ROLE.equals(user.getCredentials().getRole());

        if (!isOwner && !isAdmin) {
            throw new OperationNotAllowedException(
                    "Puoi eliminare solo i tuoi mazzi, salvo i privilegi di amministratore"
            );
        }
    }

    private DeckSummaryDto toSummaryDto(Deck deck, Utente currentUser) {
        boolean liked = currentUser != null && currentUser.getLikedDecks().contains(deck);

        return new DeckSummaryDto(
                deck.getId(),
                deck.getName(),
                deck.getCategory(),
                deck.getLikes(),
                liked
        );
    }

    private DeckDto toDto(Deck deck, Utente currentUser) {
        boolean liked = currentUser != null && currentUser.getLikedDecks().contains(deck);
        boolean bookmarked = currentUser != null && currentUser.getBookmarkedDecks().contains(deck);

        List<FlashcardDto> cards = deck.getFlashcards().stream()
                .map(flashcardService::toDto)
                .toList();

        return new DeckDto(
                deck.getId(),
                deck.getName(),
                deck.getCategory(),
                deck.getLikes(),
                deck.getCreatedAt(),
                deck.getAutore().getCredentials().getUsername(),
                cards,
                liked,
                bookmarked
        );
    }

    @Transactional(readOnly = true)
    public Page<Deck> catalog(int page) {

        int safePage = Math.max(page, 0);

        return deckRepository
                .findCatalogDecks(
                        PageRequest.of(
                                safePage,
                                12
                        )
                );
    }
}
