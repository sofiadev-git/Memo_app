package it.uniroma3.siw.memo.controller;

import it.uniroma3.siw.memo.dto.DeckDto;
import it.uniroma3.siw.memo.dto.DeckSummaryDto;
import it.uniroma3.siw.memo.model.Utente;
import it.uniroma3.siw.memo.service.DeckService;
import it.uniroma3.siw.memo.service.UtenteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/decks")
public class DeckController {

    private final DeckService deckService;
    private final UtenteService utenteService;

    public DeckController(DeckService deckService,
                          UtenteService utenteService) {
        this.deckService = deckService;
        this.utenteService = utenteService;
    }

    @GetMapping("/top")
    public List<DeckSummaryDto> top(Authentication authentication) {
        return deckService.top(current(authentication));
    }

    @GetMapping("/recent")
    public List<DeckSummaryDto> recent(Authentication authentication) {
        return deckService.recent(current(authentication));
    }

    @GetMapping("/search")
    public List<DeckSummaryDto> search(@RequestParam String q,
                                       @RequestParam(defaultValue = "all") String filter,
                                       Authentication authentication) {
        return deckService.search(q, filter, current(authentication));
    }

    @GetMapping("/{id}")
    public DeckDto get(@PathVariable Long id,
                       Authentication authentication) {
        return deckService.get(id, current(authentication));
    }

    @GetMapping("/mine")
    public List<DeckSummaryDto> mine(Authentication authentication) {
        Utente user = utenteService.find(authentication.getName());
        return deckService.mine(user);
    }

    @GetMapping("/bookmarks")
    public List<DeckSummaryDto> bookmarks(Authentication authentication) {
        Utente user = utenteService.find(authentication.getName());
        return deckService.bookmarks(user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeckDto create(@Valid @RequestBody DeckDto dto,
                          Authentication authentication) {
        Utente user = utenteService.find(authentication.getName());
        return deckService.create(dto, user);
    }

    @PutMapping("/{id}")
    public DeckDto update(@PathVariable Long id,
                          @Valid @RequestBody DeckDto dto,
                          Authentication authentication) {
        Utente user = utenteService.find(authentication.getName());
        return deckService.update(id, dto, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id,
                       Authentication authentication) {
        Utente user = utenteService.find(authentication.getName());
        deckService.delete(id, user);
    }

    @PostMapping("/{id}/like")
    public DeckSummaryDto toggleLike(@PathVariable Long id,
                                     Authentication authentication) {
        Utente user = utenteService.find(authentication.getName());
        return deckService.toggleLike(id, user);
    }

    @PostMapping("/{id}/bookmark")
    public DeckDto toggleBookmark(@PathVariable Long id,
                                  Authentication authentication) {
        Utente user = utenteService.find(authentication.getName());
        return deckService.toggleBookmark(id, user);
    }

    private Utente current(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            return null;
        }
        return utenteService.find(authentication.getName());
    }
}
