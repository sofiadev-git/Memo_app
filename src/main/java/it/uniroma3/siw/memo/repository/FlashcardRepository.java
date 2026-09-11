package it.uniroma3.siw.memo.repository;

import it.uniroma3.siw.memo.model.Flashcard;
import org.springframework.data.jpa.repository.JpaRepository;



//jpa permetta la paginazione
public interface FlashcardRepository extends JpaRepository<Flashcard,Long> {

}
