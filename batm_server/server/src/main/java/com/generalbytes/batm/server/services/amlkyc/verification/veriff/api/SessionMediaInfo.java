package com.generalbytes.batm.server.services.amlkyc.verification.veriff.api;

import java.util.List;

public class SessionMediaInfo {
    public String status;
    public List<Video> videos;
    public List<Image> images;

    public static class Video {
        /**
         * Type of a video (selfid_video|face-pre-video|document-front-pre-video|document-back-pre-video|document-and-face-pre-video|document-back-barcode-pre-video)
         */
        public String context;
        /**
         * UUID-v4 Video Id
         */
        public String id;
        /**
         * Video download url
         */
        public String url;
        /**
         * Video size in bytes
         */
        public String size;
        public String name;
    }

    public static class Image {
        /**
         * Type of image (face|face-pre|face-nfc|document-back|document-back-pre|document-front|document-front-pre|document-and-face|document-and-face-pre). For proof of address images will return type (address-front)
         */
        public String context;
        /**
         * UUID-v4 Image Id
         */
        public String id;
        /**
         * Image download url
         */
        public String url;
        /**
         * Image size in bytes
         */
        public String size;
    }

}
