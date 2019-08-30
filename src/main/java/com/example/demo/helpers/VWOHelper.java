package com.example.demo.helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.vwo.VWO;
import com.vwo.logger.VWOLogger;
import com.vwo.userprofile.UserProfileService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class VWOHelper {
    /**
     * Before evaluating the variation of a user for a campaign, lookup method is called.
     * Any custom logic to provide the saved variation can be added here.
     * Also, when the variation is evaluated, 'save' method is called and which can be used to save the variation details.
     *
     * @return {UserProfileService} User profile service instance
     */
    public static UserProfileService getUserProfileService() {
        return new UserProfileService() {
            @Override
            public Map<String, String> lookup(String userId, String campaignName) throws Exception {
                // Can define any custom logic to get saved variation of a user for a campaign.
                // Return null if variation not found.

                campaignName = campaignName == null ? "FIRST" : campaignName;
                userId = userId == null ? "Steven" : userId;

                String variation;
                switch (userId) {
                    case "Steven":
                        variation = "Variation-1";
                        break;
                    default:
                        variation = null;
                }


                Map<String, String> campaignBucketMap = new HashMap<>();
                campaignBucketMap.put(UserProfileService.userId, userId);
                campaignBucketMap.put(UserProfileService.campaignKey, campaignName);
                campaignBucketMap.put(UserProfileService.variationKey, variation);

                return campaignBucketMap;
            }

            @Override
            public void save(Map<String, String> map) throws Exception {
                // Map contains 'userId', 'campaignName' and 'variation'.
                // Can save it using custom logic.
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
            public void trace(String var1, Object... var2) {
                System.out.println("Custom Logger [Trace]: " + var1 + Arrays.toString(var2));
            }

            @Override
            public void debug(String var1, Object... var2) {
                System.out.println("Custom Logger [Debug]: " + var1 + Arrays.toString(var2));

            }

            @Override
            public void info(String var1, Object... var2) {
                System.out.println("Custom Logger [Info]: " + var1 + Arrays.toString(var2));

            }

            @Override
            public void warn(String var1, Object... var2) {
                System.out.println("Custom Logger [Warn]: " + var1 + Arrays.toString(var2));

            }

            @Override
            public void error(String var1, Object... var2) {
                System.out.println("Custom Logger [Error]: " + var1 + Arrays.toString(var2));
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
