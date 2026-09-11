package it.uniroma3.siw.memo.repository;

import it.uniroma3.siw.memo.model.Utente;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UtenteRepository extends JpaRepository<Utente, Long> {

    @EntityGraph(attributePaths = {"credentials", "likedDecks", "bookmarkedDecks", "decks"})
    @Query("select distinct u from Utente u join u.credentials c where c.username = :username")
    Optional<Utente> findByUsername(@Param("username") String username);

    @Modifying
    @Query(value = "delete from user_bookmarked_decks where deck_id = :deckId", nativeQuery = true)
    void deleteBookmarkLinks(@Param("deckId") Long deckId);

    @Modifying
    @Query(value = "delete from user_liked_decks where deck_id = :deckId", nativeQuery = true)
    void deleteLikeLinks(@Param("deckId") Long deckId);
}
