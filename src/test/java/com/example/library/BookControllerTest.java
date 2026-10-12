package com.example.library;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:testdb")
@AutoConfigureMockMvc
class BookControllerTest {
    @Autowired MockMvc mvc;
    @Autowired BookRepository repo;

    @BeforeEach
    void clean() { repo.deleteAll(); }

    @Test
    void homePageLoads() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk()).andExpect(view().name("index"));
    }

    @Test
    void addBookSavesAndRedirects() throws Exception {
        mvc.perform(post("/add").param("title", "Java Basics").param("author", "James"))
           .andExpect(status().is3xxRedirection());
        assertEquals(1, repo.count());
    }

    @Test
    void blankTitleIsNotSaved() throws Exception {
        mvc.perform(post("/add").param("title", " ").param("author", "James"));
        assertEquals(0, repo.count());
    }

    @Test
    void searchFindsMatchingBook() throws Exception {
        repo.save(new Book("Java Basics", "James"));
        repo.save(new Book("Python Intro", "Guido"));
        mvc.perform(get("/").param("q", "java"))
           .andExpect(status().isOk())
           .andExpect(content().string(containsString("Java Basics")))
           .andExpect(content().string(org.hamcrest.Matchers.not(containsString("Python Intro"))));
    }

    @Test
    void deleteRemovesBook() throws Exception {
        Book b = repo.save(new Book("Java Basics", "James"));
        mvc.perform(post("/delete/" + b.getId())).andExpect(status().is3xxRedirection());
        assertEquals(0, repo.count());
    }
}