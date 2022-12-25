package com.michael.blog.service.PostServiceImpl;

import com.michael.blog.entity.Comment;
import com.michael.blog.entity.Post;
import com.michael.blog.payload.request.CommentRequest;
import com.michael.blog.payload.response.CommentResponse;
import com.michael.blog.payload.response.MessageResponse;
import com.michael.blog.repository.CommentRepository;
import com.michael.blog.repository.PostRepository;
import com.michael.blog.service.CommentService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ModelMapper mapper;


    @Override
    public CommentResponse createComment(long postId, CommentRequest commentRequest) {
        Post post = getPostFromDB(postId);
        Comment comment = Comment.builder()
                .name(commentRequest.getName())
                .email(commentRequest.getEmail())
                .body(commentRequest.getBody())
                .post(post)
                .build();
        comment = commentRepository.save(comment);
        return mapper.map(comment, CommentResponse.class);
    }

    @Override
    public List<CommentResponse> getCommentByPostId(long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        return comments.stream()
                .map(comment -> mapper.map(comment, CommentResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponse getCommentById(Long postId, Long commentId) {
        Post post = getPostFromDB(postId);
        Comment comment = getCommentFromDB(commentId);
        isCommentBelongPost(post, comment);
        return mapper.map(comment, CommentResponse.class);
    }

    @Override
    public CommentResponse updateComment(Long postId, Long commentId, CommentRequest commentRequest) {
        Post post = getPostFromDB(postId);
        Comment comment = getCommentFromDB(commentId);
        isCommentBelongPost(post, comment);

        comment.setName(commentRequest.getName());
        comment.setEmail(commentRequest.getEmail());
        comment.setBody(commentRequest.getBody());
        comment = commentRepository.save(comment);
        return mapper.map(comment, CommentResponse.class);
    }


    @Override
    public MessageResponse deleteComment(Long postId, Long commentId) {
        Post post = getPostFromDB(postId);
        Comment comment = getCommentFromDB(commentId);
        isCommentBelongPost(post, comment);

        commentRepository.delete(comment);
        return new MessageResponse(String.format("Comment with id: %s was deleted", commentId));
    }


    private Comment getCommentFromDB(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Comment with id %s not found", commentId)));
    }

    private Post getPostFromDB(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Post with id: %s not found", postId)));
    }

    private void isCommentBelongPost(Post post, Comment comment) {
        if (!comment.getPost().getId().equals(post.getId())) {
            throw new RuntimeException("Comment does not belong to post");
        }
    }

}
