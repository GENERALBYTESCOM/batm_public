/*************************************************************************************
 * Copyright (C) 2014-2020 GENERAL BYTES s.r.o. All rights reserved.
 *
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 *
 * Contact information
 * -------------------
 *
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/
package com.generalbytes.batm.server.extensions;


/**
 * A task that performs an action repeatedly until it is considered finished and a result is available.
 */
public interface ITask {
    /**
     * Called once after the task is created
     *
     * @return false to indicate an error and to stop further task execution,
     * true to indicate the task was created/initiated successfully
     */
    boolean onCreate();

    /**
     * Called periodically to perform repeating jobs until {@link #isFinished()} returns true
     *
     * @return anything - the returned value is ignored (deprecated)
     */
    boolean onDoStep();

    /**
     * Called once when the task is finished (after {@link #isFinished()} returns true)
     */
    void onFinish();

    /**
     * This method is called by the task manager to test if it is finished and should be removed from the list of tasks
     */
    boolean isFinished();

    /**
     * Called after the task processing is finished to obtain the result of the task
     *
     * @return null if task failed or not finished yet, the result otherwise
     */
    Object getResult();

    /**
     * Called after the task processing is finished to check if task has failed
     */
    boolean isFailed();

    /**
     * Called by task manager to correctly schedule next call of onDoStep
     *
     * @return minimum time in milliseconds between two consecutive {@link #onDoStep()} calls
     */
    long getShortestTimeForNexStepInvocation();
}
