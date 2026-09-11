package it.uniroma3.siw.memo.service;

import it.uniroma3.siw.memo.exception.ResourceNotFoundException;
import it.uniroma3.siw.memo.model.Deck;
import it.uniroma3.siw.memo.model.Utente;
import it.uniroma3.siw.memo.repository.DeckRepository;
import it.uniroma3.siw.memo.repository.UtenteRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UtenteService {

    private final UtenteRepository utenteRepository;
    private final DeckRepository deckRepository;
    private final DeckService deckService;

    public UtenteService(UtenteRepository utenteRepository,
                         DeckRepository deckRepository,
                         DeckService deckService) {
        this.utenteRepository = utenteRepository;
        this.deckRepository = deckRepository;
        this.deckService = deckService;
    }

    @Transactional(readOnly = true)
    public Utente find(String username) {
        String normalized = CredentialsService.normalize(username);

        return utenteRepository.findByUsername(normalized)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Utente non trovato"));
    }

    @Transactional
    public void delete(String username) {
        Utente user = find(username);

        /*
         * Prima di eliminare l'utente aggiorniamo il contatore dei like
         * dei deck che aveva apprezzato. La relazione many-to-many verrà
         * rimossa, ma senza questo passaggio Deck.likes rimarrebbe più alto
         * del numero reale di utenti che hanno messo like.
         */
        List<Deck> likedDecks = new ArrayList<>(user.getLikedDecks());

        for (Deck deck : likedDecks) {
            deck.setLikes(Math.max(0, deck.getLikes() - 1));
            deckRepository.save(deck);
        }

        user.getLikedDecks().clear();
        user.getBookmarkedDecks().clear();

        /*
         * Copiamo gli id prima di cancellare i deck, perché deckService.delete()
         * modifica la collezione user.getDecks().
         */
        List<Long> deckIds = new ArrayList<>();

        for (Deck deck : user.getDecks()) {
            deckIds.add(deck.getId());
        }

        for (Long deckId : deckIds) {
            deckService.delete(deckId, user);
        }

        utenteRepository.delete(user);
    }
}
