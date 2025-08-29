package com.example.demo.controllers;

import com.example.demo.entities.Note;
import com.example.demo.entities.User;
import com.example.demo.repositories.NoteRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/notes")

public class NoteController {
    private final NoteRepository noteRepo;
    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;

    public NoteController(NoteRepository noteRepo, UserRepository userRepo, JwtUtil jwtUtil) {
        this.noteRepo = noteRepo;
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
    }
    @GetMapping
    public List<Note> getNotes(@RequestHeader("Authorization") String token) {
        String email = jwtUtil.extractEmail(token.substring(7));
        User user = userRepo.findByEmail(email).get();
        return noteRepo.findByUserId(user.getId());
    }

    @PostMapping
    public Note createNote(@RequestHeader("Authorization") String token, @RequestBody Note note) {
        String email = jwtUtil.extractEmail(token.substring(7));
        User user = userRepo.findByEmail(email).get();
        note.setUser(user);
        return noteRepo.save(note);
    }

    @PutMapping("/{id}")
    public Note updateNote(@PathVariable UUID id, @RequestBody Note updatedNote) {
        Note note = noteRepo.findById(id).orElseThrow();
        note.setTitle(updatedNote.getTitle());
        note.setContent(updatedNote.getContent());
        note.setUpdatedAt(LocalDateTime.now());
        return noteRepo.save(note);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable UUID id) {
        noteRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Deleted"));
    }
}

