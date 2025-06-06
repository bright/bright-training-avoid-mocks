package com.example.training.user;

import java.util.Date;

/**
 * Web service representation of a customer note.
 */
public class CustomerNoteWS {
    private String noteTitle;
    private Date creationTime;
    private String noteContent;

    public CustomerNoteWS() {
        // Default constructor
    }

    /**
     * Gets the note title.
     *
     * @return the note title
     */
    public String getNoteTitle() {
        return noteTitle;
    }

    /**
     * Sets the note title.
     *
     * @param noteTitle the note title
     */
    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    /**
     * Gets the creation time.
     *
     * @return the creation time
     */
    public Date getCreationTime() {
        return creationTime;
    }

    /**
     * Sets the creation time.
     *
     * @param creationTime the creation time
     */
    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    /**
     * Gets the note content.
     *
     * @return the note content
     */
    public String getNoteContent() {
        return noteContent;
    }

    /**
     * Sets the note content.
     *
     * @param noteContent the note content
     */
    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }
}