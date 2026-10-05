package com.thelawala.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Payload to post a comment on a vendor. */
public class CommentRequest {

    @NotBlank(message = "author is required")
    @Size(max = 80, message = "author must be at most 80 characters")
    private String author;

    @NotBlank(message = "text is required")
    @Size(max = 1000, message = "text must be at most 1000 characters")
    private String text;

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
