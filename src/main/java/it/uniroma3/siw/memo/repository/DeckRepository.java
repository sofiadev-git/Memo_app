package it.uniroma3.siw.memo.repository;

import it.uniroma3.siw.memo.model.Deck;
import it.uniroma3.siw.memo.model.Utente;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface DeckRepository extends JpaRepository<Deck, Long> {

    List<Deck> findTop4ByOrderByLikesDescCreatedAtDesc();

    List<Deck> findTop8ByOrderByCreatedAtDesc();

    /*barra di ricerca*/

    List<Deck> findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCaseOrderByLikesDesc(
            String name,
            String category
    );

    List<Deck> findByNameContainingIgnoreCaseOrderByLikesDesc(String name);

    List<Deck> findByCategoryContainingIgnoreCaseOrderByLikesDesc(String category);

    /* --------------- */

    List<Deck> findByAutoreOrderByCreatedAtDesc(Utente autore);

    @EntityGraph(attributePaths = {"flashcards", "autore", "autore.credentials"})
    @Query("select d from Deck d where d.id = :id")
    Optional<Deck> findDetailedById(@Param("id") Long id);
    
    @Query("select d from Utente u join u.bookmarkedDecks d where u.id = :userId order by d.createdAt desc")
    List<Deck> findBookmarks(@Param("userId") Long userId);

    @EntityGraph(attributePaths = {
            "autore",
            "autore.credentials"
    })
    @Query("""
        SELECT d
        FROM Deck d
        ORDER BY d.createdAt DESC
        """)
    Page<Deck> findCatalogDecks(Pageable pageable);
}

/*Se recuperi 8 deck e poi per ognuno accedi alle flashcard, rischi qualcosa del genere:

1 query → recupera gli 8 deck


+ 1 query → flashcard deck 1
+ 1 query → flashcard deck 2
+ 1 query → flashcard deck 3
...
+ 1 query → flashcard deck 8

Totale:

1 + 8 = 9 query.

È il problema N+1,
quando esegui questo metodo del repository, insieme al Deck carica anche le sue flashcards*/