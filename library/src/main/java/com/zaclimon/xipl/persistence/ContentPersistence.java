/*
 * Copyright 2017 Isaac Pateau
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zaclimon.xipl.persistence;

import com.zaclimon.xipl.model.AvContent;

import java.util.List;

/**
 * Persistence API for {@link AvContent}.
 * Will mostly be used if a content must be persisted to the disk (For caching or other reasons)
 *
 * @author zaclimon
 * Creation date: 11/08/17
 */

public interface ContentPersistence {

    /**
     * Gives a content to be persisted
     *
     * @param avContent the content that will be saved
     */
    void insert(AvContent avContent);

    /**
     * Gives a list of content to be to be persisted
     *
     * @param avContents the list that will be saved
     */
    void insert(List<AvContent> avContents);

    /**
     * Deletes a category based on {@link AvContent#mContentCategory}
     *
     * @param category the category which will get deleted.
     */
    void deleteCategory(String category);

    /**
     * Deletes all the items that have been persisted.
     */
    void deleteAll();

    /**
     * Gets all the items that have been persisted.
     *
     * @return the list of all the {@link AvContent} that have been persisted
     */
    List<AvContent> getAll();

    /**
     * Gives a list of {@link AvContent} containing a given title. The list can be sorted or not.
     *
     * @param title the title in which the content must correspond
     * @param isAlphabeticallySorted whether the retrieved contents should be sorted by alphabetical order
     * @return the list of contents.
     */
    List<AvContent> getFromTitle(String title, boolean isAlphabeticallySorted);

    /**
     * Gives a list of the {@link AvContent} belonging to a given category. The list can be sorted
     * or not. A category can be defined as the root of the content tree (VOD, TV shows, TV Catchup)
     *
     * @param category the given category for a content
     * @param isAlphabeticallySorted whether the retrieved contents should be sorted by alphabetical order
     * @return the list of contents
     */
    List<AvContent> getFromCategory(String category, boolean isAlphabeticallySorted);

    /**
     * Gets the total size of the persistence medium
     *
     * @return the total number of elements for this medium.
     */
    long size();

    /**
     * Gets the size for a given category in the persistence medium
     *
     * @param category the category in which a size is wanted
     * @return the total number of elements for that given category.
     */
    long size(String category);
}
