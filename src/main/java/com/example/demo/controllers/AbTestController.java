package com.example.demo.controllers;

import com.example.demo.config.Config;
import com.example.demo.helpers.VWOHelper;
import com.vwo.VWO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AbTestController {
  String currentSettingsFile;
  VWO vwoInstance;
  int pollingTime = 60000; // 60 sec

  private AbTestController() {
    (new Thread(() -> {
      while (true) {
        try {
          fetchSettingsAndCreateInstance();
          Thread.sleep(pollingTime);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    })).start();
  }

  private void fetchSettingsAndCreateInstance() {
    try {
      String settingsFile = VWOHelper.getSettingsFile(Config.accountId, Config.sdkKey);

      if (settingsFile == null || !settingsFile.equals(this.currentSettingsFile)) {
        this.currentSettingsFile = settingsFile;
        this.vwoInstance = VWO.createInstance(settingsFile).withUserProfileService(VWOHelper.getUserProfileService()).withCustomLogger(VWOHelper.getCustomLogger()).build();
      }
    } catch (Exception e) {
    }
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

      variation = this.vwoInstance.activate(Config.campaignTestKey, userId);
      this.vwoInstance.track(Config.campaignTestKey, userId, Config.goalIdentifier);
    } finally {
      model.addAttribute("title", "VWO | Java-sdk example | " + variation);
      model.addAttribute("userId", userId);
      model.addAttribute("isPartOfCampaign", variation != null && !variation.isEmpty());
      model.addAttribute("variation", variation);
      model.addAttribute("campaignTestKey", Config.campaignTestKey);
      model.addAttribute("goalIdentifier", Config.goalIdentifier);
      model.addAttribute("settingsFile", VWOHelper.prettyJsonSting(this.currentSettingsFile));

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
    model.addAttribute("settingsFile", VWOHelper.prettyJsonSting(this.currentSettingsFile));

    return "feature-rollout";
  }

  @GetMapping(value = "/feature-test")
  public String featureTest(
          @RequestParam(value = "userId", required = false) String userId,
          Model model
  ) {
    userId = userId == null ? VWOHelper.getRandomUser() : userId;

    boolean isFeatureEnabled = this.vwoInstance.isFeatureEnabled(Config.featureTestCampaignKey, userId);
    this.vwoInstance.track(Config.featureTestCampaignKey, userId, Config.featureTestGoalIdentifier, Config.featureTestRevenue);

    String stringVariableValue = this.vwoInstance.getFeatureVariableString(Config.featureTestCampaignKey, Config.stringVariableKey, userId);
    Integer integerVariableValue = this.vwoInstance.getFeatureVariableInteger(Config.featureTestCampaignKey, Config.integerVariableKey, userId);
    Boolean booleanVariableValue = this.vwoInstance.getFeatureVariableBoolean(Config.featureTestCampaignKey, Config.booleanVariableKey, userId);
    Double doubleVariableValue = this.vwoInstance.getFeatureVariableDouble(Config.featureTestCampaignKey, Config.doubleVariableKey, userId);

    model.addAttribute("userId", userId);
    model.addAttribute("isFeatureEnabled", isFeatureEnabled);
    model.addAttribute("featureTestCampaignKey", Config.featureTestCampaignKey);
    model.addAttribute("featureTestGoalIdentifier", Config.featureTestGoalIdentifier);
    model.addAttribute("stringVariableValue", stringVariableValue);
    model.addAttribute("integerVariableValue", integerVariableValue);
    model.addAttribute("booleanVariableValue", booleanVariableValue);
    model.addAttribute("doubleVariableValue", doubleVariableValue);
    model.addAttribute("settingsFile", VWOHelper.prettyJsonSting(this.currentSettingsFile));

    return "feature-test";
  }
}
