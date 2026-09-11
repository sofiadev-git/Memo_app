package it.uniroma3.siw.memo.controller;

import it.uniroma3.siw.memo.service.CredentialsService;
import it.uniroma3.siw.memo.service.DeckService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ThymeleafController {

    private final CredentialsService credentialsService;
    private final DeckService deckService;

    public ThymeleafController(CredentialsService credentialsService,
                         DeckService deckService) {
        this.credentialsService = credentialsService;
        this.deckService = deckService;
    }

    @GetMapping("/register")
    public String registrationPage() {
        return "paginaRegistrazione";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute(
                    "error",
                    "Le password non coincidono."
            );

            model.addAttribute("username", username);

            return "paginaRegistrazione";
        }

        try {
            credentialsService.register(username, password);

            return "redirect:http://localhost:5173/login?registered=true";

        } catch (IllegalArgumentException exception) {

            model.addAttribute(
                    "error",
                    exception.getMessage()
            );

            model.addAttribute("username", username);

            return "paginaRegistrazione";
        }
    }

    @GetMapping("/catalogo")
    public String catalog(
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        model.addAttribute(
                "deckPage",
                deckService.catalog(page)
        );

        return "catalogo";
    }
}
