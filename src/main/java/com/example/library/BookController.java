package com.example.library;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class BookController {
    private final BookRepository repo;

    public BookController(BookRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/")
    public String home(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("books",
            (q == null || q.isBlank()) ? repo.findAll() : repo.findByTitleContainingIgnoreCase(q));
        model.addAttribute("q", q == null ? "" : q);
        return "index";
    }

    @PostMapping("/add")
    public String add(@RequestParam String title, @RequestParam String author) {
        if (!title.isBlank() && !author.isBlank()) {
            repo.save(new Book(title, author));
        }
        return "redirect:/";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/";
    }
}