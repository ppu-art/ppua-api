package kh.edu.ppua.api.comtroller;

import kh.edu.ppua.api.model.NewsEntity;
import kh.edu.ppua.api.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/news")
public class NewsController {

    @Autowired
    private NewsService service;

    // Get all news
    @GetMapping
    public List<NewsEntity> getAllNews() {
        return service.getAllNews();
    }

    // Get single news
    @GetMapping("/{id}")
    public ResponseEntity<NewsEntity> getNewsById(@PathVariable Long id) {
        return service.getNewsById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create news
    @PostMapping
    public NewsEntity createNews(@RequestBody NewsEntity news) {
        return service.createNews(news);
    }

    // Update news
    @PutMapping("/{id}")
    public ResponseEntity<NewsEntity> updateNews(@PathVariable Long id, @RequestBody NewsEntity newsDetails) {
        try {
            return ResponseEntity.ok(service.updateNews(id, newsDetails));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete news
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) {
        service.deleteNews(id);
        return ResponseEntity.noContent().build();
    }
}
