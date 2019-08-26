package com.example.demo.helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.vwo.VWO;
import com.vwo.logger.VWOLogger;
import com.vwo.userprofile.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class VWOHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(VWOHelper.class);

    /**
     * Before evaluating the variation of a user for a campaign, lookup method is called.
     * Any custom logic to provide the saved variation can be added here.
     * Also, when the variation is evaluated, 'save' method is called and which can be used to save the variation details.
     *
     * @return {UserProfileService} User profile service instance
     */
    public static UserProfileService getUserProfileService() {
        ArrayList<Map<String, String>> savedCampaignBucketArray = new ArrayList<>();

        return new UserProfileService() {
            @Override
            public Map<String, String> lookup(String userId, String campaignName) throws Exception {
                // Can define any custom logic to get saved variation of a user for a campaign.
                // Return null if variation not found.

                for (Map<String, String> savedCampaignBucket: savedCampaignBucketArray) {
                    if (savedCampaignBucket.get("userId") == userId && savedCampaignBucket.get("campaign") == campaignName) {
                        Map<String, String> campaignBucketMap = new HashMap<>();
                        campaignBucketMap.put(UserProfileService.userId, userId);
                        campaignBucketMap.put(UserProfileService.campaignKey, campaignName);
                        campaignBucketMap.put(UserProfileService.variationKey, savedCampaignBucket.get("variationName"));

                        return campaignBucketMap;
                    }
                }

                return null;
            }

            @Override
            public void save(Map<String, String> map) throws Exception {
                Map<String, String> campaignBucketMap = new HashMap<>();
                campaignBucketMap.put(UserProfileService.userId, map.get("userId"));
                campaignBucketMap.put(UserProfileService.campaignKey, map.get("campaign"));
                campaignBucketMap.put(UserProfileService.variationKey, map.get("variationName"));

                savedCampaignBucketArray.add(campaignBucketMap);
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
        return new VWOLogger() {
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
        return VWO.getSetting(accountId, sdkKey);
    }

    public static String getRandomUser() {
        String[] users = {
            "Alice",
            "Bob",
            "Charlie",
            "Don",
            "Eli",
            "Fabio",
            "Gary",
            "Helen",
            "Ian",
            "Jil"
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
