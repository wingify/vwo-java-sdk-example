package com.example.demo.helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.vwo.VWO;
import com.vwo.logger.VWOLogger;
import com.vwo.services.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class VWOHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(VWOHelper.class);

    /**
     * Before evaluating the variation of a user for a campaign, lookup method is called.
     * Any custom logic to provide the saved variation can be added here.
     * Also, when the variation is evaluated, 'save' method is called and which can be used to save the variation details.
     *
     * @return {Storage.User} User storage instance
     */
    public static Storage.User getUserStorage() {
        ArrayList<Map<String, String>> campaignStorageArray = new ArrayList<>();

        return new Storage.User() {
            @Override
            public Map<String, String> get(String userId, String campaignKey) {
                for (Map<String, String> savedCampaign: campaignStorageArray) {
                    if (savedCampaign.get("userId").equals(userId) && savedCampaign.get("campaignKey").equals(campaignKey)) {
                        return savedCampaign;
                    }
                }
                return null;
            }

            @Override
            public void set(Map<String, String> map) {
                campaignStorageArray.add(map);
            }
        };
    }

    /**
     * The custom logger that will be used throughout the SDK.
     * If not passed while initializing or returned null then default one is used which prints to console.
     *
     * @return {VWOLogger} VWO logger instance
     */
    public static VWOLogger getCustomLogger() {
        return new VWOLogger(VWO.Enums.LOGGER_LEVEL.DEBUG.value()) {

            @Override
            public void trace(String message, Object... params) {
                LOGGER.trace(message, params);
            }

            @Override
            public void debug(String message, Object... params) {
                LOGGER.debug(message, params);
            }

            @Override
            public void info(String message, Object... params) {
                LOGGER.info(message, params);
            }

            @Override
            public void warn(String message, Object... params) {
                LOGGER.warn(message, params);
            }

            @Override
            public void error(String message, Object... params) {
                LOGGER.error(message, params);
            }
        };
    }

    public static String getSettingsFile(String accountId, String sdkKey) {
        return VWO.getSettingsFile(accountId, sdkKey);
    }

    public static String getRandomUser() {
        String[] users = {
                "Ashley",
                "Bill",
                "Chris",
                "Dominic",
                "Emma",
                "Faizan",
                "Gimmy",
                "Harry",
                "Ian",
                "John",
                "King",
                "Lisa",
                "Mona",
                "Nina",
                "Olivia",
                "Pete",
                "Queen",
                "Robert",
                "Sarah",
                "Tierra",
                "Una",
                "Varun",
                "Will",
                "Xin",
                "You",
                "Zeba"
        };

        return users[new Random().nextInt(users.length)];
    }

    public static String prettyJsonSting(String rawJsonString) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(rawJsonString);
        return gson.toJson(je);
    }
}
