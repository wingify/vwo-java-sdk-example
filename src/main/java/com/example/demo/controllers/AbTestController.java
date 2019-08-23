package com.example.demo.controllers;

import com.example.demo.config.Config;
import com.example.demo.helpers.VWOHelper;
import com.vwo.VWO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AbTestController {
    String currentSettingsFile;
    VWO vwoInstance;
    Number pollTime = 1000;

    private AbTestController() {
        this.fetchSettingsAndCreateInstance();
    }

    private void fetchSettingsAndCreateInstance() {
        try {
            String settingsFile = VWOHelper.getSettingsFile(Config.accountId, Config.sdkKey);

            if (settingsFile == null || !settingsFile.equals(this.currentSettingsFile)) {
                this.currentSettingsFile = settingsFile;
                this.vwoInstance = VWO.createInstance(settingsFile).withUserProfileService(VWOHelper.getUserProfileService()).withCustomLogger(VWOHelper.getCustomLogger()).build();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping(value = "/")
    public String trackUser(
        @RequestParam(value = "userId", required = false) String userId,
        Model model
    ) {
        userId = userId == null ? VWOHelper.getRandomUser() : userId;

        String variation = this.vwoInstance.activate(Config.campaignTestKey, userId);
        this.vwoInstance.track(Config.campaignTestKey, userId, Config.goalIdentifier);

        model.addAttribute("title", "VWO | Java-sdk example | " + variation);
        model.addAttribute("userId", userId);
        model.addAttribute("isPartOfCampaign", variation != null);
        model.addAttribute("variation", variation);
        model.addAttribute("campaignTestKey", Config.campaignTestKey);
        model.addAttribute("goalIdentifier", Config.goalIdentifier);
        model.addAttribute("settingsFile", VWOHelper.prettyJsonSting(this.currentSettingsFile));

        return "index";
    }
}
