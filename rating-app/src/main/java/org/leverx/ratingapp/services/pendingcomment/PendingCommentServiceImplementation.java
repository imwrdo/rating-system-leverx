package org.leverx.ratingapp.services.pendingcomment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.leverx.ratingapp.dtos.comments.CommentRequestDTO;
import org.leverx.ratingapp.dtos.comments.PendingCommentDTO;
import org.leverx.ratingapp.exceptions.InvalidOperationException;
import org.leverx.ratingapp.repositories.token.PendingCommentRepository;
import org.leverx.ratingapp.services.comment.CommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PendingCommentServiceImplementation implements PendingCommentService {
    private final PendingCommentRepository pendingCommentRepository;
    private final CommentService commentService;
    private final ObjectMapper objectMapper;

    @Override
    public void savePendingComment(String email, Long sellerId, String comment,Integer grade) {
        PendingCommentDTO pendingComment = PendingCommentDTO.builder()
                .sellerId(sellerId)
                .message(comment)
                .grade(grade)
                .build();
        try {
            String commentJson = objectMapper.writeValueAsString(pendingComment);
            pendingCommentRepository.savePendingComment(email, commentJson);
        } catch (JsonProcessingException e) {
            throw new InvalidOperationException("Failed to save pending comment");
        }
    }

    @Override
    public void processPendingComment(String email) {
        String pendingCommentJson = pendingCommentRepository.getPendingComment(email);
        if (pendingCommentJson != null) {
            try {
                PendingCommentDTO pendingComment = objectMapper.readValue(pendingCommentJson, PendingCommentDTO.class);
                commentService.create(pendingComment.sellerId(), new CommentRequestDTO(pendingComment.message(),pendingComment.grade()));
                pendingCommentRepository.removePendingComment(email);
            } catch (JsonProcessingException e) {
                throw new InvalidOperationException("Failed to process pending comment");
            }
        }
    }
}
