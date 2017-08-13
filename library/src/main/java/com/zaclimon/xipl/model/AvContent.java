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

package com.zaclimon.xipl.model;

/**
 * Offers a basic representation of a audio-visual content.
 *
 * @author zaclimon
 * Creation date: 01/07/17
 */

public class AvContent {

    private String mTitle;
    private String mLogo;
    private String mGroup;
    private String mContentLink;
    private String mContentCategory;
    private int mId;

    public AvContent() {}

    /**
     * Base constructor
     *
     * @param title         the title of the content
     * @param logo          the URL pointing to the related logo of the content
     * @param group         the group in which a content might belong to
     * @param contentCategory the category in which a content might belong to
     * @param contentLink   the URL pointing to the content itself
     */
    public AvContent(String title, String logo, String group, String contentCategory, String contentLink) {
        mTitle = title;
        mLogo = logo;
        mGroup = group;
        mContentCategory = contentCategory;
        mContentLink = contentLink;
    }

    /**
     * Constructor for an AvContent having a special identifier.
     *
     * @param title         the title of the content
     * @param logo          the URL pointing to the related logo of the content
     * @param group         the category in which a content might belong to
     * @param contentCategory the category in which a content might belong to
     * @param contentLink   the URL pointing to the content itself
     * @param id            An additional id that can be given to the content
     */
    public AvContent(String title, String logo, String group, String contentCategory, String contentLink, int id) {
        mTitle = title;
        mLogo = logo;
        mGroup = group;
        mContentCategory = contentCategory;
        mContentLink = contentLink;
        mId = id;
    }

    /**
     * Gets the title
     *
     * @return the title of the content
     */
    public String getTitle() {
        return (mTitle);
    }

    /**
     * Gets the logo
     *
     * @return the logo URL of the content
     */
    public String getLogo() {
        return (mLogo);
    }

    /**
     * Gets the category
     *
     * @return the category of the content
     */
    public String getGroup() {
        return (mGroup);
    }

    /**
     * Gets the content category
     *
     * @return the category in which this content belongs to.
     */
    public String getContentCategory() {
        return (mContentCategory);
    }

    /**
     * Gets the link
     *
     * @return the link to the content
     */
    public String getContentLink() {
        return (mContentLink);
    }

    /**
     * Gets the id
     *
     * @return the id of the content if any
     */
    public int getId() {
        return (mId);
    }
}