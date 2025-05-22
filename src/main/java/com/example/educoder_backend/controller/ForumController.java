package com.example.educoder_backend.controller;

import com.example.educoder_backend.entity.*;
import com.example.educoder_backend.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/forum")
public class ForumController {

    private static final Logger logger = LoggerFactory.getLogger(ForumController.class);

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
    public ResponseEntity<?> getAllPosts() {
        logger.debug("收到获取所有帖子请求");
        try {
            List<Post> posts = postRepository.findAllWithAuthorAndReplies();
            logger.debug("查询到 {} 条帖子", posts.size());
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            logger.error("获取帖子列表失败，错误: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("获取帖子列表失败：" + e.getMessage());
        }
    }

    // 获取单个帖子详情
    @GetMapping("/posts/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Long id) {
        logger.debug("收到获取帖子详情请求，帖子ID: {}", id);
        try {
            Optional<Post> post = postRepository.findById(id);
            if (post.isPresent()) {
                post.get().setViews(post.get().getViews() + 1);
                Post updatedPost = postRepository.save(post.get());
                return ResponseEntity.ok(updatedPost);
            }
            logger.warn("帖子未找到，ID: {}", id);
            return ResponseEntity.status(404).body("帖子未找到");
        } catch (Exception e) {
            logger.error("获取帖子失败，帖子ID: {}, 错误: {}", id, e.getMessage(), e);
            return ResponseEntity.status(500).body("获取帖子失败：" + e.getMessage());
        }
    }

    // 创建帖子
    @PostMapping("/posts")
    public ResponseEntity<?> createPost(@RequestBody PostRequest request) {
        logger.debug("收到创建帖子请求，作者ID: {}", request.getAuthorId());
        try {
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
        } catch (Exception e) {
            logger.error("创建帖子失败，错误: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("创建帖子失败：" + e.getMessage());
        }
    }

    // 创建回帖
    @PostMapping("/posts/{postId}/replies")
    public ResponseEntity<?> createReply(@PathVariable Long postId, @RequestBody ReplyRequest request) {
        logger.debug("收到创建回帖请求，帖子ID: {}, 作者ID: {}", postId, request.getAuthorId());
        try {
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
        } catch (Exception e) {
            logger.error("创建回帖失败，帖子ID: {}, 错误: {}", postId, e.getMessage(), e);
            return ResponseEntity.status(500).body("创建回帖失败：" + e.getMessage());
        }
    }

    // 获取帖子下的回帖
    @GetMapping("/posts/{postId}/replies")
    public ResponseEntity<?> getRepliesByPostId(@PathVariable Long postId) {
        logger.debug("收到获取帖子回帖请求，帖子ID: {}", postId);
        try {
            List<Reply> replies = replyRepository.findByPostId(postId);
            return ResponseEntity.ok(replies);
        } catch (Exception e) {
            logger.error("获取回帖失败，帖子ID: {}, 错误: {}", postId, e.getMessage(), e);
            return ResponseEntity.status(500).body("获取回帖失败：" + e.getMessage());
        }
    }

    // 创建文档
    @PostMapping("/documents")
    public ResponseEntity<?> createDocument(@RequestBody DocumentRequest request) {
        logger.debug("收到创建文档请求，作者ID: {}", request.getAuthorId());
        try {
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
        } catch (Exception e) {
            logger.error("创建文档失败，错误: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("创建文档失败：" + e.getMessage());
        }
    }

    // 编辑文档
    @PutMapping("/documents/{id}")
    public ResponseEntity<?> updateDocument(@PathVariable Long id, @RequestBody DocumentRequest request) {
        logger.debug("收到编辑文档请求，文档ID: {}", id);
        try {
            Document document = documentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("文档未找到"));
            document.setTitle(request.getTitle());
            document.setContent(request.getContent());
            document.setUpdatedAt(LocalDateTime.now());
            Document updatedDocument = documentRepository.save(document);
            return ResponseEntity.ok(updatedDocument);
        } catch (Exception e) {
            logger.error("编辑文档失败，文档ID: {}, 错误: {}", id, e.getMessage(), e);
            return ResponseEntity.status(500).body("编辑文档失败：" + e.getMessage());
        }
    }

    // 获取所有文档
    @GetMapping("/documents")
    public ResponseEntity<?> getAllDocuments() {
        logger.debug("收到获取所有文档请求");
        try {
            List<Document> documents = documentRepository.findAll();
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            logger.error("获取文档列表失败，错误: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("获取文档列表失败：" + e.getMessage());
        }
    }

    // 创建文档评论
    @PostMapping("/documents/{documentId}/comments")
    public ResponseEntity<?> createComment(@PathVariable Long documentId, @RequestBody CommentRequest request) {
        logger.debug("收到创建文档评论请求，文档ID: {}, 作者ID: {}", documentId, request.getAuthorId());
        try {
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
        } catch (Exception e) {
            logger.error("创建文档评论失败，文档ID: {}, 错误: {}", documentId, e.getMessage(), e);
            return ResponseEntity.status(500).body("创建文档评论失败：" + e.getMessage());
        }
    }

    // 获取文档下的评论
    @GetMapping("/documents/{documentId}/comments")
    public ResponseEntity<?> getCommentsByDocumentId(@PathVariable Long documentId) {
        logger.debug("收到获取文档评论请求，文档ID: {}", documentId);
        try {
            List<Comment> comments = commentRepository.findByDocumentId(documentId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            logger.error("获取文档评论失败，文档ID: {}, 错误: {}", documentId, e.getMessage(), e);
            return ResponseEntity.status(500).body("获取文档评论失败：" + e.getMessage());
        }
    }

    // 创建代码点评
    @PostMapping("/posts/{postId}/code-reviews")
    public ResponseEntity<?> createCodeReview(@PathVariable Long postId, @RequestBody CodeReviewRequest request) {
        logger.debug("收到创建代码点评请求，帖子ID: {}, 教师ID: {}", postId, request.getTeacherId());
        try {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("帖子未找到"));
            User teacher = userRepository.findById(request.getTeacherId())
                    .orElseThrow(() -> new RuntimeException("教师未找到"));
            CodeReview codeReview = new CodeReview();
            codeReview.setPost(post);
            codeReview.setCodeSnippet(request.getCodeSnippet());
            codeReview.setReviewContent(request.getReviewContent());
            codeReview.setTeacher(teacher);
            codeReview.setCreatedAt(LocalDateTime.now());
            codeReview.setUpdatedAt(LocalDateTime.now());
            CodeReview savedCodeReview = codeReviewRepository.save(codeReview);
            return ResponseEntity.ok(savedCodeReview);
        } catch (Exception e) {
            logger.error("创建代码点评失败，帖子ID: {}, 错误: {}", postId, e.getMessage(), e);
            return ResponseEntity.status(500).body("创建代码点评失败：" + e.getMessage());
        }
    }

    // 获取帖子下的代码点评
    @GetMapping("/posts/{postId}/code-reviews")
    public ResponseEntity<?> getCodeReviewsByPostId(@PathVariable Long postId) {
        logger.debug("收到获取帖子代码点评请求，帖子ID: {}", postId);
        try {
            List<CodeReview> codeReviews = codeReviewRepository.findByPostId(postId);
            return ResponseEntity.ok(codeReviews);
        } catch (Exception e) {
            logger.error("获取代码点评失败，帖子ID: {}, 错误: {}", postId, e.getMessage(), e);
            return ResponseEntity.status(500).body("获取代码点评失败：" + e.getMessage());
        }
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