package com.example.educoder_backend.controller;

import com.example.educoder_backend.entity.*;
import com.example.educoder_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/forum")
public class ForumController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CodeReviewRepository codeReviewRepository;

    @Autowired
    private UserRepository userRepository;

    // 获取所有帖子
    @GetMapping("/posts")
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return ResponseEntity.ok(posts);
    }

    // 获取单个帖子详情
    @GetMapping("/posts/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent()) {
            post.get().setViews(post.get().getViews() + 1);
            postRepository.save(post.get());
            return ResponseEntity.ok(post.get());
        }
        return ResponseEntity.notFound().build();
    }

    // 创建帖子
    @PostMapping("/posts")
    public ResponseEntity<Post> createPost(@RequestBody PostRequest request) {
        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new RuntimeException("用户未找到"));
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setCategory(request.getCategory());
        post.setAuthor(author);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        Post savedPost = postRepository.save(post);
        return ResponseEntity.ok(savedPost);
    }

    // 创建回帖
    @PostMapping("/posts/{postId}/replies")
    public ResponseEntity<Reply> createReply(@PathVariable Long postId, @RequestBody ReplyRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("帖子未找到"));
        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new RuntimeException("用户未找到"));
        Reply reply = new Reply();
        reply.setPost(post);
        reply.setContent(request.getContent());
        reply.setAuthor(author);
        reply.setCreatedAt(LocalDateTime.now());
        reply.setUpdatedAt(LocalDateTime.now());
        Reply savedReply = replyRepository.save(reply);
        return ResponseEntity.ok(savedReply);
    }

    // 获取帖子下的回帖
    @GetMapping("/posts/{postId}/replies")
    public ResponseEntity<List<Reply>> getRepliesByPostId(@PathVariable Long postId) {
        List<Reply> replies = replyRepository.findByPostId(postId);
        return ResponseEntity.ok(replies);
    }

    // 创建文档
    @PostMapping("/documents")
    public ResponseEntity<Document> createDocument(@RequestBody DocumentRequest request) {
        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new RuntimeException("用户未找到"));
        Document document = new Document();
        document.setTitle(request.getTitle());
        document.setContent(request.getContent());
        document.setAuthor(author);
        document.setCreatedAt(LocalDateTime.now());
        document.setUpdatedAt(LocalDateTime.now());
        Document savedDocument = documentRepository.save(document);
        return ResponseEntity.ok(savedDocument);
    }

    // 编辑文档
    @PutMapping("/documents/{id}")
    public ResponseEntity<Document> updateDocument(@PathVariable Long id, @RequestBody DocumentRequest request) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文档未找到"));
        document.setTitle(request.getTitle());
        document.setContent(request.getContent());
        document.setUpdatedAt(LocalDateTime.now());
        Document updatedDocument = documentRepository.save(document);
        return ResponseEntity.ok(updatedDocument);
    }

    // 获取所有文档
    @GetMapping("/documents")
    public ResponseEntity<List<Document>> getAllDocuments() {
        List<Document> documents = documentRepository.findAll();
        return ResponseEntity.ok(documents);
    }

    // 创建文档评论
    @PostMapping("/documents/{documentId}/comments")
    public ResponseEntity<Comment> createComment(@PathVariable Long documentId, @RequestBody CommentRequest request) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("文档未找到"));
        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new RuntimeException("用户未找到"));
        Comment comment = new Comment();
        comment.setDocument(document);
        comment.setContent(request.getContent());
        comment.setAuthor(author);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);
        return ResponseEntity.ok(savedComment);
    }

    // 获取文档下的评论
    @GetMapping("/documents/{documentId}/comments")
    public ResponseEntity<List<Comment>> getCommentsByDocumentId(@PathVariable Long documentId) {
        List<Comment> comments = commentRepository.findByDocumentId(documentId);
        return ResponseEntity.ok(comments);
    }

    // 创建代码点评
    @PostMapping("/posts/{postId}/code-reviews")
    public ResponseEntity<CodeReview> createCodeReview(@PathVariable Long postId, @RequestBody CodeReviewRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("帖子未找到"));
        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new RuntimeException("教师未找到"));
        // 移除角色校验
        CodeReview codeReview = new CodeReview();
        codeReview.setPost(post);
        codeReview.setCodeSnippet(request.getCodeSnippet());
        codeReview.setReviewContent(request.getReviewContent());
        codeReview.setTeacher(teacher);
        codeReview.setCreatedAt(LocalDateTime.now());
        codeReview.setUpdatedAt(LocalDateTime.now());
        CodeReview savedCodeReview = codeReviewRepository.save(codeReview);
        return ResponseEntity.ok(savedCodeReview);
    }

    // 获取帖子下的代码点评
    @GetMapping("/posts/{postId}/code-reviews")
    public ResponseEntity<List<CodeReview>> getCodeReviewsByPostId(@PathVariable Long postId) {
        List<CodeReview> codeReviews = codeReviewRepository.findByPostId(postId);
        return ResponseEntity.ok(codeReviews);
    }
}

class PostRequest {
    private String title;
    private String content;
    private String category;
    private Long authorId;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
}

class ReplyRequest {
    private String content;
    private Long authorId;

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
}

class DocumentRequest {
    private String title;
    private String content;
    private Long authorId;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
}

class CommentRequest {
    private String content;
    private Long authorId;

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
}

class CodeReviewRequest {
    private String codeSnippet;
    private String reviewContent;
    private Long teacherId;

    public String getCodeSnippet() { return codeSnippet; }
    public void setCodeSnippet(String codeSnippet) { this.codeSnippet = codeSnippet; }
    public String getReviewContent() { return reviewContent; }
    public void setReviewContent(String reviewContent) { this.reviewContent = reviewContent; }
    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
}