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
                fetchSettingsAndLaunch();
                  Thread.sleep(pollingTime);
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }
          }
      })).start();
  }

  private void fetchSettingsAndLaunch() {
    try {
      String settingsFile = VWOHelper.getSettingsFile(Config.accountId, Config.sdkKey);

      if (settingsFile == null || !settingsFile.equals(this.currentSettingsFile)) {
        this.currentSettingsFile = settingsFile;
        this.vwoInstance = VWO.launch(settingsFile).withUserStorage(VWOHelper.getUserStorage()).withCustomLogger(VWOHelper.getCustomLogger()).build();
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

      variation = this.vwoInstance.activate(Config.campaignKey, userId);
      this.vwoInstance.track(Config.campaignKey, userId, Config.goalIdentifier);
    } finally {
      model.addAttribute("title", "VWO | Java-sdk example | " + variation);
      model.addAttribute("userId", userId);
      model.addAttribute("isPartOfCampaign", variation != null && !variation.isEmpty());
      model.addAttribute("variation", variation);
      model.addAttribute("campaignKey", Config.campaignKey);
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

    Object featureVariableValue = this.vwoInstance.getFeatureVariableValue(Config.featureTestCampaignKey, Config.variableKey, userId);

    model.addAttribute("userId", userId);
    model.addAttribute("isFeatureEnabled", isFeatureEnabled);
    model.addAttribute("featureTestCampaignKey", Config.featureTestCampaignKey);
    model.addAttribute("featureTestGoalIdentifier", Config.featureTestGoalIdentifier);
    model.addAttribute("featureVariableValue", featureVariableValue);
    model.addAttribute("settingsFile", VWOHelper.prettyJsonSting(this.currentSettingsFile));

    return "feature-test";
  }
}
