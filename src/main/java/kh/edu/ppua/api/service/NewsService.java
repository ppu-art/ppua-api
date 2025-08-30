package kh.edu.ppua.api.service;

import kh.edu.ppua.api.model.NewsEntity;
import kh.edu.ppua.api.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NewsService {

    @Autowired
    private NewsRepository repository;

    public List<NewsEntity> getAllNews() {
        return repository.findAll();
    }

    public Optional<NewsEntity> getNewsById(Long id) {
        return repository.findById(id);
    }

    public NewsEntity createNews(NewsEntity news) {
        news.setPublishedDate(LocalDateTime.now());
        return repository.save(news);
    }

    public NewsEntity updateNews(Long id, NewsEntity newsDetails) {
        NewsEntity news = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found with id: " + id));
        news.setTitle(newsDetails.getTitle());
        news.setContent(newsDetails.getContent());
        news.setAuthor(newsDetails.getAuthor());
        return repository.save(news);
    }

    public void deleteNews(Long id) {
        repository.deleteById(id);
    }
}
