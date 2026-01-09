package com.juangomez.feedservice.repository;

import com.juangomez.feedservice.model.entity.FeedItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface FeedRepository extends JpaRepository<FeedItem, UUID> {

    // Main Feed: (Friends + Me) OR (Posts where my username is in the tags)
    @Query("""
        SELECT DISTINCT f FROM FeedItem f
        LEFT JOIN f.tags t
        WHERE f.authorId IN :authorIds 
           OR :myUsername = t
        ORDER BY f.createdAt DESC
    """)
    List<FeedItem> getFeed(
            @Param("authorIds") Set<UUID> authorIds,
            @Param("myUsername") String myUsername
    );

    // Search Body: Same scope + text filter
    @Query("""
        SELECT DISTINCT f FROM FeedItem f
        LEFT JOIN f.tags t
        WHERE (f.authorId IN :authorIds OR :myUsername = t)
        AND LOWER(f.postBody) LIKE LOWER(CONCAT('%', :body, '%'))
        ORDER BY f.createdAt DESC
    """)
    List<FeedItem> searchByBody(
            @Param("authorIds") Set<UUID> authorIds,
            @Param("myUsername") String myUsername,
            @Param("body") String body
    );

    // Search Tags: Same scope + tag match
    @Query("""
        SELECT DISTINCT f FROM FeedItem f
        JOIN f.tags searchT
        LEFT JOIN f.tags userT
        WHERE (f.authorId IN :authorIds OR :myUsername = userT)
        AND searchT IN :tags
        ORDER BY f.createdAt DESC
    """)
    List<FeedItem> searchByTags(
            @Param("authorIds") Set<UUID> authorIds,
            @Param("myUsername") String myUsername,
            @Param("tags") Set<String> tags
    );

    List<FeedItem> findAllByAuthorIdOrderByCreatedAtDesc(UUID authorId);

    boolean existsByPostId(UUID postId);

    void deleteByPostId(UUID postId);

    FeedItem findByPostId(UUID postId);
}