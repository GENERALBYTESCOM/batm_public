package com.generalbytes.batm.server.extensions;

import java.util.Date;

public interface IIdentityNote {

    /**
     * @return content of the note
     */
    String getText();

    /**
     * @return time of create the note
     */
    Date getCreateAt();

    /**
     * @return time of delete the note
     */
    Date getDeleteAt();

    /**
     * @return name of the user who created the note
     */
    String getUserName();

    /**
     * @return true if note is marked as deleted
     */
    boolean isDeleted();

}
