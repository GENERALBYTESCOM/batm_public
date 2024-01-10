package com.generalbytes.batm.server.extensions.examples.location;

import com.generalbytes.batm.server.extensions.INote;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NoteExample implements INote {
    private Long id;
    private String text;
    private Date createdAt;
    private Date deletedAt;
    private String userName;
    private boolean deleted;

    public NoteExample() {
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public static List<INote> xx(List<NoteExample> list) {
        if(list == null) {
            return null;
        }
        List<INote> result = new ArrayList<>();
        for (NoteExample noteExample : list) {
            result.add(noteExample);
        }
        return result;
    }
}
