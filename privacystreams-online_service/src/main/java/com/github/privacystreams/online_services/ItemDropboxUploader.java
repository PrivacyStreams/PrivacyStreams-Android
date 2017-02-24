package com.github.privacystreams.online_services;

import android.Manifest;
import android.content.Context;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.WriteMode;

import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.privacystreams.core.Item;
import com.github.privacystreams.core.UQI;
import com.github.privacystreams.core.utilities.ItemFunction;
import com.github.privacystreams.core.utils.Assertions;
import com.github.privacystreams.core.utils.Consts;
import com.github.privacystreams.core.utils.Logging;
import com.github.privacystreams.core.utils.time.TimeUtils;

/**
 * Created by yuanchun on 27/12/2016.
 * Write the item to a file
 */

final class ItemDropboxUploader extends ItemFunction<Void> {

    private final String DropboxToken;
    private final String fileTag;

    private static final String DROPBOX_FILE_PREFIX = "dropbox";
    private static final String WORD_SEPARATOR = "__";

    ItemDropboxUploader(String DropboxToken, String fileTag) {
        this.DropboxToken = Assertions.notNull("DropboxToken", DropboxToken);
        this.fileTag = Assertions.notNull("fileTag", fileTag);

        if (this.fileTag.contains(WORD_SEPARATOR)) {
            throw new IllegalArgumentException("Illegal fileTag, can not contain: " + WORD_SEPARATOR);
        }

        this.addParameters(DropboxToken, fileTag);
        this.addRequiredPermissions(Manifest.permission.INTERNET);
    }

    @Override
    public Void apply(UQI uqi, Item input) {
        String localFileName = StringUtils.join(
                new String[]{DROPBOX_FILE_PREFIX, fileTag, TimeUtils.now().toString()}, WORD_SEPARATOR) + ".json";
        try {
            FileOutputStream fileOutputStream = uqi.getContext().openFileOutput(localFileName
                    , Context.MODE_PRIVATE);
            fileOutputStream.write(uqi.getGson().toJson(input).getBytes());
            fileOutputStream.close();
            uploadDropboxFiles(uqi, this.DropboxToken);
        } catch (IOException e) {
            Logging.warn("error writing item to file.");
            e.printStackTrace();
        }
        return null;
    }

    private static synchronized void uploadDropboxFiles(UQI uqi, String DropboxToken) {
        try {
            String[] localFiles = uqi.getContext().fileList();
            ArrayList<String> filesToUpload = new ArrayList<>();

            for (String localFile : localFiles) {
                String[] words = StringUtils.split(localFile, WORD_SEPARATOR);
                if (words.length == 3 && DROPBOX_FILE_PREFIX.equals(words[0])) {
                    filesToUpload.add(localFile);
                }
            }

            Logging.debug("Trying to upload file to Dropbox: " + filesToUpload);

            // Create Dropbox client
            DbxRequestConfig config = new DbxRequestConfig(Consts.LIB_TAG);
            DbxClientV2 client = new DbxClientV2(config, DropboxToken);

            for (String fileToUpload : filesToUpload) {
                FileInputStream inputStream = uqi.getContext().openFileInput(fileToUpload);
                client.files().uploadBuilder("/" + uqi.getUUID() + "/" + fileToUpload)
                        .withMode(WriteMode.OVERWRITE)
                        .uploadAndFinish(inputStream);
                inputStream.close();
                uqi.getContext().deleteFile(fileToUpload);
            }

            Logging.debug("Upload finished.");

        } catch (IOException | DbxException e) {
            Logging.warn("error uploading files to Dropbox.");
            e.printStackTrace();
        }
    }
}