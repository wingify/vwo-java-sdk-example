/**
 * Copyright 2019-2020 Wingify Software Pvt. Ltd.
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
import com.vwo.VWOAdditionalParams;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AbTestController {
  VWO vwoInstance;
  int pollingTime = 60000; // 60 sec

  private AbTestController() {
    String settingsFile = VWOHelper.getSettingsFile(Config.accountId, Config.sdkKey);
    this.vwoInstance = VWO.launch(settingsFile).withPollingInterval(pollingTime).withSdkKey(Config.sdkKey).withUserStorage(VWOHelper.getUserStorage()).withCustomLogger(VWOHelper.getCustomLogger()).build();
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
}
