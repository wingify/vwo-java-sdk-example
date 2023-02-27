/**
 * Copyright 2019-2021 Wingify Software Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.demo.controllers;

import com.example.demo.config.Config;
import com.example.demo.helpers.VWOHelper;
import com.vwo.VWO;
import com.vwo.utils.UUIDUtils;
import com.vwo.VWOAdditionalParams;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Controller
public class AbTestController {
  VWO vwoInstance;
  int pollingTime = 60000; // 60 sec

  private AbTestController() {
    String settingsFile = VWOHelper.getSettingsFile(Config.accountId, Config.sdkKey);
    this.vwoInstance = VWO.launch(settingsFile)
            .withSdkKey(Config.sdkKey)
            .withPollingInterval(pollingTime)
            // .withBatchEvents(VWOHelper.getBatchingData())
            .withUserStorage(VWOHelper.getUserStorage())
            .withCustomLogger(VWOHelper.getCustomLogger())
//            .withShouldTrackReturningUser(true)
            .build();
  }

  @GetMapping(value = "/")
  public String home() {
    return "index";
  }

  @GetMapping(value = "/ab")
  public String abCampaign(
          @RequestParam(value = "userId", required = false) String userId,
          Model model
  ) {
    String variation = "";
    try {
      userId = userId == null ? VWOHelper.getRandomUser() : userId;
      VWOAdditionalParams options = new VWOAdditionalParams();
//      options.setShouldTrackReturningUser(false);

      variation = this.vwoInstance.activate(Config.campaignKey, userId);
      this.vwoInstance.track(Config.campaignKey, userId, Config.goalIdentifier);
    } finally {
      model.addAttribute("title", "VWO | Java-sdk example | " + variation);
      model.addAttribute("userId", userId);
      model.addAttribute("isPartOfCampaign", variation != null && !variation.isEmpty());
      model.addAttribute("variation", variation);
      model.addAttribute("campaignKey", Config.campaignKey);
      model.addAttribute("goalIdentifier", Config.goalIdentifier);
      model.addAttribute("settingsFile", VWOHelper.prettyJsonSting(this.vwoInstance.getSettingFileString()));

      return "ab";
    }
  }

  @GetMapping(value = "/feature-rollout")
  public String featureRollout(
          @RequestParam(value = "userId", required = false) String userId,
          Model model
  ) {
    userId = userId == null ? VWOHelper.getRandomUser() : userId;

    boolean isFeatureEnabled = this.vwoInstance.isFeatureEnabled(Config.featureRolloutCampaignKey, userId);

    model.addAttribute("userId", userId);
    model.addAttribute("isFeatureEnabled", isFeatureEnabled);
    model.addAttribute("featureRolloutCampaignKey", Config.featureRolloutCampaignKey);
    model.addAttribute("settingsFile", VWOHelper.prettyJsonSting(this.vwoInstance.getSettingFileString()));

    return "feature-rollout";
  }

  @GetMapping(value = "/feature-test")
  public String featureTest(
          @RequestParam(value = "userId", required = false) String userId,
          Model model
  ) {
    userId = userId == null ? VWOHelper.getRandomUser() : userId;

    VWOAdditionalParams additionalParams = new VWOAdditionalParams();
    additionalParams.setRevenueValue(Config.featureTestRevenue);
//    additionalParams.setShouldTrackReturningUser(false);

    boolean isFeatureEnabled = this.vwoInstance.isFeatureEnabled(Config.featureTestCampaignKey, userId);
    this.vwoInstance.track(Config.featureTestCampaignKey, userId, Config.featureTestGoalIdentifier, additionalParams);

    Object featureVariableValue = this.vwoInstance.getFeatureVariableValue(Config.featureTestCampaignKey, Config.variableKey, userId);

    model.addAttribute("userId", userId);
    model.addAttribute("isFeatureEnabled", isFeatureEnabled);
    model.addAttribute("featureTestCampaignKey", Config.featureTestCampaignKey);
    model.addAttribute("featureTestGoalIdentifier", Config.featureTestGoalIdentifier);
    model.addAttribute("featureVariableValue", featureVariableValue);
    model.addAttribute("settingsFile", VWOHelper.prettyJsonSting(this.vwoInstance.getSettingFileString()));

    return "feature-test";
  }

  @PostMapping("/webhook")
  @ResponseStatus(HttpStatus.OK)
  public void webhook(
    @RequestHeader("x-vwo-auth") String secretKey,
    @RequestBody String body
    ) {
    if (Config.webhookAuthKey != null && secretKey != null) {
      if (secretKey.equals(Config.webhookAuthKey)) {
        System.out.println("\nVWO webhook authenticated successfully.\n");
      } else {
        System.out.println("\nVWO webhook authentication failed. Please check.\n");
        return;
      }
    } else {
      System.out.println("\nSkipping Webhook Authentication as webhookAuthKey is not provided in Config.java\n");
    }

    if (this.vwoInstance != null) {
      System.out.println(this.vwoInstance.getSettingFileString());
      this.vwoInstance.getAndUpdateSettingsFile(Config.accountId, Config.sdkKey);
      System.out.println(this.vwoInstance.getSettingFileString());
    }
  }

  @GetMapping(value = "/push")
  public String push(
          @RequestParam(value = "userId", required = false) String userId,
          Model model
  ) {
    boolean variation = false;
    try {
      userId = userId == null ? VWOHelper.getRandomUser() : userId;
      this.vwoInstance.push(Config.customDimensionTagKey, Config.customDimensionTagValue, userId);
    } finally {
      model.addAttribute("title", "VWO | Java-sdk example | ");
      model.addAttribute("userId", userId);
      model.addAttribute("tagKey", Config.customDimensionTagKey);
      model.addAttribute("tagValue", Config.customDimensionTagValue);
      model.addAttribute("settingsFile", VWOHelper.prettyJsonSting(this.vwoInstance.getSettingFileString()));

      return "push";
    }
  }

  @GetMapping(value = "/flushEvents")
  public String flush(Model model) {
    boolean isEventsQueueFlushed = false;
    try {
      isEventsQueueFlushed = this.vwoInstance.flushEvents();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      model.addAttribute("isEventsQueueFlushed",isEventsQueueFlushed);
      model.addAttribute("settingsFile", VWOHelper.prettyJsonSting(this.vwoInstance.getSettingFileString()));
    }
    return "flush";
  }

  @GetMapping(value = "/getUserData")
  public String getUserData(
        @RequestParam(value = "userId", required = false) String userId,
        Model model
  ) {
    if (userId == "") {
      userId = "Specify a User ID by sending query-param. Eg: userId=Abc";

      model.addAttribute("userId", userId);
      return "user-data";
    }

    String uuid = UUIDUtils.getUUId(Integer.parseInt(Config.accountId), userId);
    ArrayList<Map<String, String>> usersData = VWOHelper.getUsersData();

    Object userIdData = "No data found for user-id: " + userId;

    for(int i = 0; i < usersData.size(); i++)
    {
      Map<String, String> userDetail = usersData.get(i);

      if (!userDetail.isEmpty()) {
        if (userId != "" && userDetail.get("userId").contains(userId)) {
          userIdData = userDetail;
          break;
        }
      }
    }

    model.addAttribute("users_data", usersData);
    model.addAttribute("userId", userId);
    model.addAttribute("vwo_uuid", uuid);
    model.addAttribute("userid_data", userIdData);

    return "user-data";
  }

}
